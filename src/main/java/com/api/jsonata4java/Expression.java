/**
 * 
 */
package com.api.jsonata4java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.api.jsonata4java.expressions.EvaluateException;
import com.api.jsonata4java.expressions.EvaluateRuntimeException;
import com.api.jsonata4java.expressions.Expressions;
import com.api.jsonata4java.expressions.ExpressionsVisitor;
import com.api.jsonata4java.expressions.ParseException;
import com.api.jsonata4java.expressions.functions.DeclaredFunction;
import com.api.jsonata4java.expressions.functions.Function;
import com.api.jsonata4java.expressions.functions.JavaFunction;
import com.api.jsonata4java.expressions.functions.TypedFunction;
import com.api.jsonata4java.expressions.generated.MappingExpressionParser.ExprContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Class to provide embedding and extending JSONata features
 */
public class Expression implements Evaluatable {

   /**
    * Genearte a new Expression based on evaluating the supplied expression
    * 
    * @param expression
    *                   the logic to be parsed for later execution via the evaluate
    *                   methods
    * @return new Expression object
    * @throws ParseException
    */
   public static Expression jsonata(String expression) throws ParseException {
      return new Expression(expression);
   }

   /**
    * Testing the various methods based on
    * https://docs.jsonata.org/embedding-extending#expressionregisterfunctionname-implementation-signature
    * 
    * @param args
    *             not used
    */
   public static void main(String[] args) {
      try {
         String exprString = "$sum(example.value)";
         String inputString = "{\"example\": [{\"value\": 4}, {\"value\": 7}, {\"value\": 13}]}";
         System.out.println("Expression is " + exprString);
         System.out.println("Input is " + inputString);
         Expression expression = Expression.jsonata(exprString);
         JsonNode obj = new ObjectMapper().readTree(inputString);
         JsonNode result = expression.evaluate(obj);
         System.out.println("Result is " + result);

         expression = Expression.jsonata("$a +$b()");
         expression.assign("a", "4");
         expression.assign("$b", "function(){1}");
         result = expression.evaluate(obj);
         System.out.println("Input is \"$a + $b()\" with assignments \"a\":4, \"$b\":\"function(){1}\"");
         System.out.println("Result is " + result);

         JsonNode bindingObj = new ObjectMapper().readTree("{\"a\":4, \"b\":\"function(){78}\"}");
         System.out.println("Input is \"$a + $b()\" with binding object: " + bindingObj.toString());
         System.out.println("Result is " + Expression.jsonata("$a + $b()").evaluate(obj, bindingObj));

         bindingObj = new ObjectMapper().readTree("{\"a\":4, \"b\":\"function($c){$c+78}\",\"c\":7}");
         System.out.println("Input is \"$a + $b($c)\" with binding object: " + bindingObj.toString());
         System.out.println("Result is " + Expression.jsonata("$a + $b($c)").evaluate(obj, bindingObj));
         try {
            expression = Expression.jsonata("$notafunction()");
            result = expression.evaluate(JsonNodeFactory.instance.objectNode());
            throw new Exception("Expression " + expression + " should have generated an exception");
         } catch (EvaluateRuntimeException ere) {
            System.out
                  .println("Result is we got the expected EvaluateRuntimeException for " + ere.getLocalizedMessage());
         }
      } catch (ParseException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (EvaluateException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   ExpressionsVisitor _eval = null;
   Expressions _expr = null;
   Map<String, DeclaredFunction> _functionMap = new HashMap<String, DeclaredFunction>();
   Map<String, ExprContext> _variableMap = new HashMap<String, ExprContext>();
   final Map<String, Function> _javaFunctionMap = new HashMap<>();

   /**
    * Constructor for Expression
    * 
    * @param expression
    *                   the logic to be parsed for later execution via evaluate
    *                   methods
    * @throws ParseException
    */
   public Expression(String expression) throws ParseException {
      _expr = Expressions.parse(expression);
      _eval = _expr.getExpr();
   }

   /**
    * Assign the binding to the environment preparing for evaluation
    * 
    * @param binding
    */
   public void assign(Binding binding) {
      if (binding.getType() == BindingType.VARIABLE) {
         _variableMap.put(binding.getVarName(), binding.getExpression());
      } else {
         _functionMap.put(binding.getVarName(), binding.getFunction());
      }
   }

   /**
    * Assign the expression (variable or function declaration) to the variable name
    * supplied
    * 
    * @param varname
    *                   name of the variable to map to a variable expression or
    *                   function declaration expression
    * @param expression
    *                   logic to be assigned to the variable name
    * @throws ParseException
    */
   public void assign(String varname, String expression) throws ParseException {
      Binding binding = new Binding(varname, expression);
      assign(binding);
   }

   /**
    * Assign a TypedFunction to a variable name.
    *
    * @param varname
    *                   name to map to the function
    * @param function
    *                   the function to invoke
    */
   public <R> void assign(String varname, TypedFunction<JsonNode> function) {
      String prefixedVarname = varname.startsWith("$") ? varname : "$" + varname;
      _javaFunctionMap.put(prefixedVarname, new JavaFunction(prefixedVarname, function));
   }

   /**
    * Generate a result form the Expression's parsed expression and variable
    * assignments or registered functions
    * 
    * @param rootContext
    *                    JSON object specifying the content used to evaluate the
    *                    expression
    * @return the result from executing the Expression's parsed expression and
    *         variable assignments or registered functions
    * @throws EvaluateException
    */
   public JsonNode evaluate(JsonNode rootContext) throws EvaluateException {
      ExpressionsVisitor eval = new ExpressionsVisitor(rootContext);
      Map<String, JsonNode> varMap = eval.getVariableMap();
      Map<String, DeclaredFunction> fctMap = eval.getFunctionMap();
      // process any stored bindings
      for (Iterator<String> it = _variableMap.keySet().iterator(); it.hasNext();) {
         String key = it.next();
         ExprContext ctx = _variableMap.get(key);
         varMap.put(key, eval.visit(ctx));
      }
      for (Iterator<String> it = _functionMap.keySet().iterator(); it.hasNext();) {
         String key = it.next();
         DeclaredFunction fct = _functionMap.get(key);
         fctMap.put(key, fct);
      }
      eval.getJavaFunctionMap().putAll(_javaFunctionMap);
      return eval.visit(_expr.getTree());
   }

   /**
    * Generate a result form the Expression's parsed expression and variable
    * assignments or registered functions specified in the list of bindings
    * 
    * @param rootContext
    *                    JSON object specifying the content used to evaluate the
    *                    expression
    * @param bindings
    *                    assignments of variable names to variable expressions or
    *                    function declarations
    * @return the result from executing the Expression's parsed expression and
    *         variable assignments or registered functions specified in the list of
    *         bindings
    * @throws EvaluateException
    * @throws ParseException
    */
   public JsonNode evaluate(JsonNode rootContext, List<Binding> bindings) throws EvaluateException, ParseException {
      JsonNode result = null;
      // first do variables
      for (Binding binding : bindings) {
         assign(binding);
      }
      result = evaluate(rootContext);
      return result;
   }

   /**
    * Generate a result form the Expression's parsed expression and variable
    * assignments or registered functions specified in the bindings object
    * 
    * @param rootContext
    *                    JSON object specifying the content used to evaluate the
    *                    expression
    * @param bindingObj
    *                    a JSON object containing the assignments of variable names
    *                    to variable expressions or function declarations
    * @return the result from executing the Expression's parsed expression and
    *         variable assignments or registered functions specified in the
    *         bindings object
    * @throws EvaluateException
    * @throws ParseException
    */
   public JsonNode evaluate(JsonNode rootContext, JsonNode bindingObj) throws EvaluateException, ParseException {
      List<Binding> bindings = new ArrayList<Binding>();
      for (Iterator<String> it = bindingObj.fieldNames(); it.hasNext();) {
         String key = it.next();
         Object testObj = bindingObj.get(key);
         String expression = "";
         if (testObj instanceof TextNode == false) {
            expression = testObj.toString();
         } else {
            expression = ((TextNode) testObj).asText();
         }
         Binding binding = new Binding(key, expression);
         bindings.add(binding);
      }
      return evaluate(rootContext, bindings);
   }

   /**
    * Registers a function implementation (declaration) by name
    * 
    * @param fctName
    *                       the name of the function
    * @param implementation
    *                       the function declaration
    * @throws ParseException
    */
   public void registerFunction(String fctName, String implementation) throws ParseException {
      Binding fctBinding = new Binding(fctName, implementation);
      _functionMap.put(fctBinding.getVarName(), fctBinding.getFunction());
   }
}
