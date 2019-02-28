/**
 * (c) Copyright 2018, 2019 IBM Corporation
 * 1 New Orchard Road, 
 * Armonk, New York, 10504-1722
 * United States
 * +1 914 499 1900
 * support: Nathaniel Mills wnm3@us.ibm.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.api.jsonata4java.test.expressions;

import static com.api.jsonata4java.text.expressions.utils.Utils.ensureAllIntegralsAreLongs;
import static com.api.jsonata4java.text.expressions.utils.Utils.simpleTest;
import static com.api.jsonata4java.text.expressions.utils.Utils.simpleTestExpectException;
import static com.api.jsonata4java.text.expressions.utils.Utils.test;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.api.jsonata4java.expressions.EvaluateException;
import com.api.jsonata4java.expressions.Expressions;
import com.api.jsonata4java.expressions.NonNumericArrayIndexException;
import com.api.jsonata4java.expressions.ParseException;
import com.api.jsonata4java.expressions.functions.AppendFunction;
import com.api.jsonata4java.expressions.functions.AverageFunction;
import com.api.jsonata4java.expressions.functions.CountFunction;
import com.api.jsonata4java.expressions.functions.ExistsFunction;
import com.api.jsonata4java.expressions.functions.LookupFunction;
import com.api.jsonata4java.expressions.functions.MergeFunction;
import com.api.jsonata4java.expressions.functions.ShuffleFunction;
import com.api.jsonata4java.expressions.functions.SortFunction;
import com.api.jsonata4java.expressions.functions.SpreadFunction;
import com.api.jsonata4java.expressions.functions.SubstringFunction;
import com.api.jsonata4java.expressions.functions.SumFunction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * A collection of poorly-structured test cases. Don't add new tests to this
 * class. Retained because we might as well keep them (they execute in seconds).
 */
@SuppressWarnings("deprecation")
public class BasicExpressionsTest {
   static String json = "{\n" + //
            "  \"Account\": {\n" + //
            "    \"Account Name\": \"Firefly\",\n" + //
            "    \"Order\": [\n" + //
            "      {\n" + //
            "        \"OrderID\": \"order103\",\n" + //
            "        \"Product\": [\n" + //
            "          {\n" + //
            "            \"Product Name\": \"Bowler Hat\",\n" + //
            "            \"ProductID\": 858383,\n" + //
            "            \"SKU\": \"0406654608\",\n" + //
            "            \"Description\": {\n" + //
            "              \"Colour\": \"Purple\",\n" + //
            "              \"Width\": 300,\n" + //
            "              \"Height\": 200,\n" + //
            "              \"Depth\": 210,\n" + //
            "              \"Weight\": 0.75\n" + //
            "            },\n" + //
            "            \"Price\": 34.45,\n" + //
            "            \"Quantity\": 2\n" + //
            "          },\n" + //
            "          {\n" + //
            "            \"Product Name\": \"Trilby hat\",\n" + //
            "            \"ProductID\": 858236,\n" + //
            "            \"SKU\": \"0406634348\",\n" + //
            "            \"Description\": {\n" + //
            "              \"Colour\": \"Orange\",\n" + //
            "              \"Width\": 300,\n" + //
            "              \"Height\": 200,\n" + //
            "              \"Depth\": 210,\n" + //
            "              \"Weight\": 0.6\n" + //
            "            },\n" + //
            "            \"Price\": 21.67,\n" + //
            "            \"Quantity\": 1\n" + //
            "          }\n" + //
            "        ]\n" + //
            "      },\n" + //
            "      {\n" + //
            "        \"OrderID\": \"order104\",\n" + //
            "        \"Product\": [\n" + //
            "          {\n" + //
            "            \"Product Name\": \"Bowler Hat\",\n" + //
            "            \"ProductID\": 858383,\n" + //
            "            \"SKU\": \"040657863\",\n" + //
            "            \"Description\": {\n" + //
            "              \"Colour\": \"Purple\",\n" + //
            "              \"Width\": 300,\n" + //
            "              \"Height\": 200,\n" + //
            "              \"Depth\": 210,\n" + //
            "              \"Weight\": 0.75\n" + //
            "            },\n" + //
            "            \"Price\": 34.45,\n" + //
            "            \"Quantity\": 4\n" + //
            "          },\n" + //
            "          {\n" + //
            "            \"ProductID\": 345664,\n" + //
            "            \"SKU\": \"0406654603\",\n" + //
            "            \"Product Name\": \"Cloak\",\n" + //
            "            \"Description\": {\n" + //
            "              \"Colour\": \"Black\",\n" + //
            "              \"Width\": 30,\n" + //
            "              \"Height\": 20,\n" + //
            "              \"Depth\": 210,\n" + //
            "              \"Weight\": 2\n" + //
            "            },\n" + //
            "            \"Price\": 107.99,\n" + //
            "            \"Quantity\": 1\n" + //
            "          }\n" + //
            "        ]\n" + //
            "      }\n" + //
            "    ]\n" + //
            "  }\n" + //
            "}\n"; //
   
   static String json2 = "{\n" + //
            "  \"FirstName\": \"Fred\",\n" + //
            "  \"Surname\": \"Smith\",\n" + //
            "  \"Age\": 28,\n" + //
            "  \"Address\": {\n" + //
            "    \"Street\": \"Hursley Park\",\n" + //
            "    \"City\": \"Winchester\",\n" + //
            "    \"Postcode\": \"SO21 2JN\"\n" + //
            "  },\n" + //
            "  \"Phone\": [\n" + //
            "    {\n" + //
            "      \"type\": \"home\",\n" + //
            "      \"number\": \"0203 544 1234\"\n" + //
            "    },\n" + //
            "    {\n" + //
            "      \"type\": \"office\",\n" + //
            "      \"number\": \"01962 001234\"\n" + //
            "    },\n" + //
            "    {\n" + //
            "      \"type\": \"office\",\n" + //
            "      \"number\": \"01962 001235\"\n" + //
            "    },\n" + //
            "    {\n" + //
            "      \"type\": \"mobile\",\n" + //
            "      \"number\": \"077 7700 1234\"\n" + //
            "    }\n" + //
            "  ],\n" + //
            "  \"Email\": [\n" + //
            "    {\n" + //
            "      \"type\": \"work\",\n" + //
            "      \"address\": [\"fred.smith@my-work.com\", \"fsmith@my-work.com\"]\n" + //
            "    },\n" + //
            "    {\n" + //
            "      \"type\": \"home\",\n" + //
            "      \"address\": [\"freddy@my-social.com\", \"frederic.smith@very-serious.com\"]\n" + //
            "    }\n" + //
            "  ],\n" + //
            "  \"Other\": {\n" + //
            "    \"Over 18 ?\": true,\n" + //
            "    \"Misc\": null,\n" + //
            "    \"Alternative.Address\": {\n" + //
            "      \"Street\": \"Brick Lane\",\n" + //
            "      \"City\": \"London\",\n" + //
            "      \"Postcode\": \"E1 6RF\"\n" + //
            "    }\n" + //
            "  }\n" + //
            "}\n"; //


   static JsonNodeFactory factory = JsonNodeFactory.instance;
   static ObjectMapper mapper = new ObjectMapper();
   static JsonNode jsonObj=null;
   static JsonNode jsonObj2=null;
   static {
      try {
         jsonObj = mapper.readTree(json);
         jsonObj2 = mapper.readTree(json2);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   @Rule
   public ExpectedException exception = ExpectedException.none();

   @BeforeClass
   public static void beforeAll() throws IOException {
   }

   @AfterClass
   public static void afterAll() {
   }
   
   @Test
   public void testNewStuff() throws Exception {
      ArrayNode expectArray = JsonNodeFactory.instance.arrayNode();
      ObjectNode expectObject = JsonNodeFactory.instance.objectNode();
      // from https://docs.jsonata.org/simple
      simpleTest("Surname","Smith",jsonObj2);
      simpleTest("Age",28,jsonObj2);
      simpleTest("Address.City","Winchester",jsonObj2);
      simpleTest("Account.`Account Name`","Firefly",jsonObj);
      test("Other.Misc",NullNode.instance,null,jsonObj2);
      test("Other.Nothing",null,null,jsonObj2);
      test("Other.`Over 18 ?`",BooleanNode.TRUE,null,jsonObj2);
      expectObject = (ObjectNode)mapper.readTree("{ \"type\": \"home\", \"number\": \"0203 544 1234\" }");
      simpleTest("Phone[0]",expectObject,jsonObj2);
      expectObject = (ObjectNode)mapper.readTree("{ \"type\": \"office\", \"number\": \"01962 001234\" }");
      simpleTest("Phone[1]",expectObject,jsonObj2);
      expectObject = (ObjectNode)mapper.readTree("{ \"type\": \"mobile\", \"number\": \"077 7700 1234\" }");
      simpleTest("Phone[-1]",expectObject,jsonObj2);
      expectObject = (ObjectNode)mapper.readTree("{ \"type\": \"office\", \"number\": \"01962 001235\" }");
      simpleTest("Phone[-2]",expectObject,jsonObj2);
      test("Phone[8]",null,null,jsonObj2);
      simpleTest("Phone[0].number","0203 544 1234",jsonObj2);
      expectArray.removeAll();
      expectArray.add("0203 544 1234");
      expectArray.add("01962 001234");
      expectArray.add("01962 001235");
      expectArray.add("077 7700 1234");
      simpleTest("Phone.number",expectArray,jsonObj2);
      simpleTest("Phone.number[0]",expectArray,jsonObj2);
      simpleTest("(Phone.number)[0]","0203 544 1234",jsonObj2);
      expectArray.removeAll();
      expectArray.add((ObjectNode)mapper.readTree("{ \"type\": \"home\", \"number\": \"0203 544 1234\" }"));
      expectArray.add((ObjectNode)mapper.readTree("{ \"type\": \"office\", \"number\": \"01962 001234\" }"));
      simpleTest("Phone[[0..1]]",expectArray,jsonObj2);
      expectArray.removeAll();
      expectArray.add((ObjectNode)mapper.readTree("{ \"ref\": [ 1,2 ] }"));
      expectArray.add((ObjectNode)mapper.readTree("{ \"ref\": [ 3,4 ] }"));
      expectObject = (ObjectNode)mapper.readTree("{ \"ref\": [ 1,2 ] }");
// TODO: fix this      simpleTest("$[0]",expectObject,expectArray);
      
      simpleTest("Account.Order[0].OrderID","order103",jsonObj);
      expectArray.removeAll();
      expectArray.add("order103");
      expectArray.add("order104");
      simpleTest("Account.Order.OrderID",expectArray,jsonObj);
      expectArray.removeAll();
      expectArray.add("ORDER103");
      expectArray.add("ORDER104");
      simpleTest("Account.Order.OrderID.$uppercase()",expectArray,jsonObj);
      simpleTest("Account.Order.Product[858236 in ProductID].SKU","0406634348",jsonObj);
      expectArray.removeAll();
      expectArray.add("0406654608");
      expectArray.add("040657863");
      simpleTest("Account.Order.Product[0].SKU",expectArray,jsonObj);
      expectObject = (ObjectNode)mapper.readTree("{\"Product Name\":\"Trilby hat\",\"ProductID\":858236,\"Price\":21.67}");
      simpleTest("$sift(Account.Order.Product[1][ProductID=858236],function($v, $k) {$substring($k,0,1)=\"P\"})",expectObject,jsonObj);
      expectArray.removeAll();
      expectArray.add(mapper.readTree("{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"Price\":34.45}"));
      expectArray.add(mapper.readTree("{\"Product Name\":\"Trilby hat\",\"ProductID\":858236,\"Price\":21.67}"));
      expectArray.add(mapper.readTree("{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"Price\":34.45}"));
      expectArray.add(mapper.readTree("{\"ProductID\":345664,\"Product Name\":\"Cloak\",\"Price\":107.99}"));
      // TODO: below fails for some reason
      // simpleTest("Account.Order.Product.$sift(function($v, $k) {$substring($k,0,1)=\"P\"})","[{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"Price\":34.45},{\"Product Name\":\"Trilby hat\",\"ProductID\":858236,\"Price\":21.67},{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"Price\":34.45},{\"ProductID\":345664,\"Product Name\":\"Cloak\",\"Price\":107.99}]",jsonObj);
   }

   @Test
   public void testNullNode() throws Exception {
      test("null", (JsonNode) NullNode.instance, null, null);
      test("$exists(whatever)?22:null", (JsonNode) NullNode.instance, null,
         null);
   }

   @Test
   public void testCustomerScenario() throws Exception {
      String event = "{\"observedProperties\":[[{\"name\":\"VIS12sec\",\"value\":22000},{\"name\":\"VIS60sec\",\"value\":21000},{\"name\":\"VIS-MAX600sec\",\"value\":20600},{\"name\":\"VIS-MIN600sec\",\"value\":20080},{\"name\":\"VIS-AVG600sec\",\"value\":20020},{\"name\":\"VIS-STD600sec\",\"value\":1},{\"name\":\"VIS-MISSED600sec\",\"value\":2}]],\"phenomenonTime\":\"2018-10-01T06:10:53.539Z\",\"label\":\"visibility\",\"sensorId\":\"H-S-MNS-DOMA-ZCHT\",\"gatewayId\":\"FGL2106213P\",\"rawMessage\":\"\\u0002\\r\\nXEAZM0EPW37ZM0352005200520052005200000000PW0300500050006000500050////00\\r\\nXEAZM0EPW37ZM0352005200520052005200000000PW0300500050006000500050////00\\r\\nXEAZM0EPW37ZM0352005200520052005200000000PW0300500050006000500050////00\\r\\n\\u0003\",\"__kinetic__\":{\"dcm_received_at\":\"1538374253554\",\"dcm_sent_at\":\"1538374257960\"}}";
      test("observedProperties[name='VIS-MISSED600sec'].value", "2", null,
         event);

   }

   @Test
   public void testArrayToString() throws Exception {
      String arrstr = "$string([1,2,3,4,5])";
      test(arrstr, "\"[1,2,3,4,5]\"", null, null);
   }

   @Test
   public void testStrings() throws Exception {

      String[] strs = new String[] {

         "a", "helloworld",

         // field names with spaces
         "a b", "hello world",

         // strings consisting only of spaces (permitted according to JSONata
         // spec)
         " ", "   ",

         // empty string
         "",

         // one for each escaped char
         "\\\"", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t", "\\uaA2d",

         // one with all the escaped chars combined
         "\\\" \\/ \\b \\f \\n \\r \\t  \\uaA2d"
      };

      for (String str : strs) {

         // should work with single quotes
         simpleTest("'" + str + "'", "\"" + str + "\"");

         // and double quotes
         simpleTest("\"" + str + "\"", "\"" + str + "\"");

         // backquotes cannot be used to define strings
         simpleTest("`" + str + "`", null); // (this is treated as an
                                            // unresolvable path)

      }

   }

   @Test
   public void testLiterals() throws Exception {

      String expr = "22";
      Expressions expression = Expressions.parse(expr);
      JsonNode result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.NUMBER);
      assertTrue(result.isIntegralNumber());
      assertTrue(expr, result.asInt() == 22);

      expr = "22.34";
      expression = Expressions.parse(expr);
      result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.NUMBER);
      assertTrue(result.isFloatingPointNumber());
      assertTrue(expr, result.asDouble() == 22.34);

      expr = " \"xxx\\\"zzz \" ";
      expression = Expressions.parse(expr);
      result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.STRING);
      assertTrue(result.isTextual());
      assertTrue(expr, result.asText().equals("xxx\"zzz "));

      // input: xxx\"zzz
      // output: xxx"zzz
      expression = Expressions.parse(" \"xxx\\\"zzz \" ");
      Assert.assertEquals("xxx\"zzz ", expression.evaluate(null).asText());

      expression = Expressions.parse(" \"xxx'zzz \" ");
      Assert.assertEquals("xxx'zzz ", expression.evaluate(null).asText());

      expression = Expressions.parse(" 'xxx\\\"zzz ' ");
      Assert.assertEquals("xxx\"zzz ", expression.evaluate(null).asText());

      expression = Expressions.parse(" 'xxx\"zzz ' ");
      Assert.assertEquals("xxx\"zzz ", expression.evaluate(null).asText());

      expression = Expressions.parse(" 'xxx\\'zzz ' ");
      Assert.assertEquals("xxx'zzz ", expression.evaluate(null).asText());

      expression = Expressions.parse(" 'xxx\\nzzz ' ");
      Assert.assertEquals("xxx\nzzz ", expression.evaluate(null).asText());

      expression = Expressions.parse(" \"xxx\\uaaaazzz \" ");
      Assert.assertEquals("xxx\uaaaazzz ", expression.evaluate(null).asText());

      expression = Expressions.parse(" 'xxx\\uaaaazzz ' ");
      Assert.assertEquals("xxx\uaaaazzz ", expression.evaluate(null).asText());

      simpleTest("\"hello\"", "\"hello\"");

   }

   @Test
   public void testNegation() throws Exception {
      Assert.assertEquals(new LongNode(1),
         Expressions.parse("1").evaluate(null));
      Assert.assertEquals(new LongNode(-1),
         Expressions.parse(" - 1").evaluate(null));
      Assert.assertEquals(new LongNode(-1),
         Expressions.parse("-1").evaluate(null));
      Assert.assertEquals(new DoubleNode(1.1),
         Expressions.parse("1.1").evaluate(null));
      Assert.assertEquals(new DoubleNode(-1.99),
         Expressions.parse("0.0-1.99").evaluate(null));
      Assert.assertEquals(new DoubleNode(-1.99),
         Expressions.parse("-1.99").evaluate(null));
      Assert.assertEquals(new DoubleNode(-1.99),
         Expressions.parse("-  1.99").evaluate(null));
      Assert.assertEquals(new DoubleNode(-1.19),
         Expressions.parse(" - 1.19").evaluate(null));
      try {
         Expressions.parse(" - true").evaluate(null);
         Assert.fail("Expected exception was not thrown");
      } catch (EvaluateException ex) {
         Assert.assertEquals("Cannot negate a non-numeric value",
            ex.getMessage());
      }
      try {
         Expressions.parse(" - \"hello\"").evaluate(null);
         Assert.fail("Expected exception was not thrown");
      } catch (EvaluateException ex) {
         Assert.assertEquals("Cannot negate a non-numeric value",
            ex.getMessage());
      }
   }

   @Test
   public void testArithmeticOperators() throws Exception {

      String expr = "$number(\"22\")";
      Expressions expression = Expressions.parse(expr);
      JsonNode result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.NUMBER);
      assertTrue(result.isIntegralNumber());
      assertTrue(expr, result.asInt() == 22);

      expr = "$number(\"22.34\")";
      expression = Expressions.parse(expr);
      result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.NUMBER);
      assertTrue(result.isFloatingPointNumber());
      assertTrue(expr, result.asDouble() == 22.34);

      expr = "22.34";
      expression = Expressions.parse(expr);
      result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.NUMBER);
      assertTrue(result.isFloatingPointNumber());
      assertTrue(expr, result.asDouble() == 22.34);

      expression = Expressions.parse("1 + 1");
      assertTrue("1+1==2", expression.evaluate(null).asInt() == 2);

      expression = Expressions.parse("1 - 1");
      assertTrue("1-1==0", expression.evaluate(null).asInt() == 0);

      expression = Expressions.parse("1 -1");
      assertTrue("1-1==0", expression.evaluate(null).asInt() == 0);

      expression = Expressions.parse("1+2*4");
      assertTrue("1+2*4==9", expression.evaluate(null).asInt() == 9);

      expression = Expressions.parse("(1+2)*4");
      assertTrue("(1+2)*4==9", expression.evaluate(null).asInt() == 12);

      expression = Expressions.parse("4%3");
      assertTrue("4%3==1", expression.evaluate(null).asInt() == 4 % 3);

      expression = Expressions.parse("4 / 3");
      assertTrue("4 / 3 ==1", expression.evaluate(null).asInt() == 4 / 3);

      expression = Expressions.parse("4.0 / 3");
      assertTrue("4.0 / 3 ==1.333333",
         expression.evaluate(null).asDouble() == 4.0D / 3.0);

   }

   @Test
   public void testStringOperators() throws Exception {

      String expr = "$substring(\"abcdefghijk\", 3, 6) = \"defghi\"";
      Expressions expression = Expressions.parse(expr);
      JsonNode result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.BOOLEAN);
      assertTrue(result.isBoolean());
      assertTrue(expr, result.asBoolean());

      expr = "$string(22)";
      expression = Expressions.parse(expr);
      result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.STRING);
      assertTrue(result.isTextual());
      assertTrue(expr, result.asInt() == 22);

      expr = "\"22\"";
      expression = Expressions.parse(expr);
      result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.STRING);
      assertTrue(result.isTextual());
      assertTrue(expr, result.asInt() == 22);

      expr = "$string($number(\"22\"))";
      expression = Expressions.parse(expr);
      result = expression.evaluate(null);
      assertTrue(result.getNodeType() == JsonNodeType.STRING);
      assertTrue(result.isTextual());
      assertTrue(expr, result.asInt() == 22);

      expr = "$substring(\"abcdefghijk\", 3) = \"defghijk\"";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean());

      // should also be able to use single quotes for strings
      expr = "$substring('abcdefghijk', 3) = 'defghijk'";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean());

      expression = Expressions.parse("\"abcdefghijk\" & \"lmnop\"");
      assertTrue("\"abcdefghijk\"+\"lmnop\" ==\"abcdefghijklmnop\"",
         expression.evaluate(null).asText().equals("abcdefghijklmnop"));

      expression = Expressions.parse("\"abcdefghijk\" & 1");
      assertTrue("\"abcdefghijk\"+1 ==\"abcdefghijk1\"",
         expression.evaluate(null).asText().equals("abcdefghijk1"));

      expression = Expressions.parse("2&\"abcdefghijk\"");
      assertTrue("2+\"abcdefghijk\" ==\"2abcdefghijk\"",
         expression.evaluate(null).asText().equals("2abcdefghijk"));

   }

   @Test
   public void eventAccessNoQuotes() throws Exception {

      JsonNode event = mapper.readTree("{\"a\":2, \"a_b\":3}");

      Expressions expression = Expressions.parse("a");
      assertTrue("a==2", expression.evaluate(event).asInt() == 2);

      expression = Expressions.parse("a* 34.2");
      assertTrue("a * 34.2", expression.evaluate(event).asDouble() == 68.4);

      expression = Expressions.parse("a_b");
      assertTrue("a_b", expression.evaluate(event).asInt() == 3);

      expression = Expressions.parse("a_b*34.2");
      assertTrue("a_b * 34.2 ",
         expression.evaluate(event).asDouble() == 102.60000000000001);

   }

   /**
    * See 185467: Expression language implementation allows associative array
    * syntax... These chars can be used in JSON field names, but they must be
    * escaped - http://json.org/
    * @throws Exception the expression fails to parse or evaluate correctly
    */
   @Test
   public void eventAccessWithQuotes() throws Exception {

      String[] fieldNames = new String[] {

         "a", "helloworld",

         // field names with spaces
         "a b", "hello world",

         // field names consisting only of spaces (permitted according to
         // JSONata spec)
         " ", "   ",

         // empty field name (permitted according to JSONata spec)
         "",

         // one for each escaped char
         "\\\"", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t", "\\uaA2d",

         // one with all the escaped chars combined
         "\\\" \\/ \\b \\f \\n \\r \\t  \\uaA2d"
      };

      for (String fieldName : fieldNames) {
         JsonNode event = mapper.readTree("{\"" + fieldName + "\":22}");

         // back quotes
         {
            Expressions expression = Expressions.parse("`" + fieldName + "`");
            Assert.assertEquals("`" + fieldName + "`", 22,
               expression.evaluate(event).asInt());
         }

      }

   }

   @Test
   public void appAccessNoQuotes() throws Exception {

      JsonNode intface = mapper.readTree("{\"a\":2}");

      Expressions expression = Expressions.parse("a");
      assertTrue("a==2", expression.evaluate(intface).asInt() == 2);

      expression = Expressions.parse("a * 2.4");
      assertTrue("a * 2.4==4.8",
         expression.evaluate(intface).asDouble() == 4.8);

      expression = Expressions.parse("`a` * 2.4"); // See 185467: Expression
                                                   // language implementation
                                                   // allows associative array
                                                   // syntax...
      assertTrue("`a` * 2.4==4.8",
         expression.evaluate(intface).asDouble() == 4.8);

      intface = mapper.readTree("{\"temperatureCCount\":10}");
      String exp = "((temperatureCCount >= 10) ? 1 : (temperatureCCount + 1))";
      expression = Expressions.parse(exp);
      assertTrue(exp, expression.evaluate(intface).asInt() == 1);

   }

   @Test
   public void appAccessWithQuotes() throws Exception {

      String[] fieldNames = new String[] {

         "a", "helloworld",

         // field names with spaces
         "a b", "hello world",

         // field names consisting only of spaces (permitted according to
         // JSONata spec)
         " ", "   ",

         // empty field name (permitted according to JSONata spec)
         "",

         // one for each escaped char
         "\\\"", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t", "\\uaA2d",

         // one with all the escaped chars combined
         "\\\" \\/ \\b \\f \\n \\r \\t  \\uaA2d"
      };

      for (String fieldName : fieldNames) {
         JsonNode state = mapper.readTree("{\"" + fieldName + "\":22}");

         // back quotes
         {
            Expressions expression = Expressions.parse("`" + fieldName + "`");
            Assert.assertEquals("`" + fieldName + "`", 22,
               expression.evaluate(state).asInt());
         }
      }

   }

   @Test
   public void testArrays() throws Exception {

      JsonNode event = mapper.readTree("{\"array\":[2,[3],4]}");
      JsonNode appInterface = mapper
         .readTree("{\"array\":[2,{\"k\": 3},4],\"i\":1}");

      // Test referencing an array itself (rather than one of its elements)
      {
         Expressions expression = Expressions.parse("array");
         JsonNode _result = expression.evaluate(event);
         assertTrue("array", _result.isArray());
         ArrayNode result = (ArrayNode) _result;
         assertTrue("array", result.size() == 3);
         assertTrue("array", result.get(0).asInt() == 2);
         assertTrue("array", result.get(1).isArray());
         ArrayNode nested = (ArrayNode) result.get(1);
         assertTrue("array", nested.size() == 1);
         assertTrue("array", nested.get(0).asInt() == 3);
         assertTrue("array", result.get(2).asInt() == 4);
      }

      // Test out-of-bounds behaviour ( should just return null )
      {
         simpleTest("[0,1,2,3,4][5]", null);
         simpleTest("[0,1,2,3,4][-5]", "0");
         simpleTest("[0,1,2,3,4][-6]", null);
      }

      // non-numeric indexes
      {
         simpleTestExpectException("[0,1,2,3,4][\"hello\"]",
            NonNumericArrayIndexException.MSG);
         simpleTestExpectException("[0,1,2,3,4][[\"hello\"]]",
            NonNumericArrayIndexException.MSG);
         simpleTest("[0,1,2,3,4][true]", "[0,1,2,3,4]");
         simpleTestExpectException("[0,1,2,3,4][[true]]",
            NonNumericArrayIndexException.MSG);
         simpleTestExpectException("[0,1,2,3,4][{\"hello\":\"world\"}]",
            NonNumericArrayIndexException.MSG);
         simpleTestExpectException("[0,1,2,3,4][[{\"hello\":\"world\"}]]",
            NonNumericArrayIndexException.MSG);
         simpleTest("[0,1,2,3,4][false]", null);
         simpleTestExpectException("[0,1,2,3,4][[false]]",
            NonNumericArrayIndexException.MSG);
         simpleTestExpectException("[0,1,2,3,4][[[false]]]",
            NonNumericArrayIndexException.MSG);
      }

      {
         Expressions expression = Expressions.parse("array[0]");
         assertTrue("array[0]==2", expression.evaluate(event).asInt() == 2);
      }

      {
         Expressions expression = Expressions.parse("array[1][0]");
         assertTrue("array[0][0]==3", expression.evaluate(event).asInt() == 3);
      }

      {
         Expressions expression = Expressions.parse("array[2]");
         assertTrue("array[0]==4", expression.evaluate(event).asInt() == 4);
      }

      {
         Expressions expression = Expressions.parse("array[1].k");
         assertTrue("array[1].k==3",
            expression.evaluate(appInterface).asInt() == 3);
      }

      // non-arrays treated as singletons
      {

         simpleTest("true[0]", "true");
         simpleTest("true[1]", null);
         simpleTest("{\"arr\":true}.arr[0]", "true");
         simpleTest("false[0]", "false");
         simpleTest("1[0]", "1");
         simpleTest("\"hello\"[0]", "\"hello\"");
         Assert.assertEquals(BooleanNode.TRUE, Expressions.parse("arr[0]")
            .evaluate(mapper.readTree("{\"arr\":true}")));
      }

      {
         Assert.assertEquals(BooleanNode.TRUE, Expressions.parse("arr[0][0]")
            .evaluate(mapper.readTree("{\"arr\":[true]}")));
      }
      // array indexes must be an number, an expression that evaluates to a
      // number, or a predicate
      // The string "hello" is none of these.
      try {
         Expressions.parse("arr[\"hello\"]")
            .evaluate(mapper.readTree("{\"arr\":[0]}"));
         Assert.fail("Expected exception was not thrown");
      } catch (EvaluateException ex) {
         Assert.assertEquals(NonNumericArrayIndexException.MSG,
            ex.getMessage());
      }

      // check we can reference indexes in arrays constructed using array
      // constructor
      {
         Assert.assertEquals(new LongNode(2),
            Expressions.parse("[1,2,3,4,5][1]").evaluate(null));
      }

      // check we can reference indexes in arrays constructed using sequence
      // constructor
      {
         Assert.assertEquals(new LongNode(6),
            Expressions.parse("[1..10][5]").evaluate(null));
      }

      // negative array indexes start from end of array
      {
         Assert.assertEquals(new LongNode(10),
            Expressions.parse("[1..10][-1]").evaluate(null));
         Assert.assertEquals(new LongNode(2),
            Expressions.parse("[1,2,3][-2]").evaluate(null));
         Assert.assertEquals(new LongNode(1),
            Expressions.parse("[1,2,3][-3]").evaluate(null));
         // and check out-of-bounds behaviour is correct for negative indexes
         // too

         Assert.assertNull(Expressions.parse("[1,2,3][-4]").evaluate(null));

      }

      // positive non-integer array indexes are rounded down
      {
         Assert.assertEquals(new LongNode(2),
            Expressions.parse("[1,2,3][1.1]").evaluate(null));
      }
      {
         Assert.assertEquals(new LongNode(2),
            Expressions.parse("[1,2,3][1.9]").evaluate(null));
      }

      // negative array indexes are also rounded down
      {
         Assert.assertEquals(new LongNode(2),
            Expressions.parse("[1,2,3][-1.2]").evaluate(null));
      }
      {
         Assert.assertEquals(new LongNode(2),
            Expressions.parse("[1,2,3][-1.8]").evaluate(null));
      }

      // selecting a subarray using another array as an index
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[4,5]")),
            Expressions.parse(" [1,2,3,4,5 ][[3 ,4 ]	]").evaluate(null));
      }

      // duplicates in index array should cause duplicated in resulting array
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[4,4,5]")),
            Expressions.parse("[1,2,3,4,5][[-2,-2, -1]]").evaluate(null));
      }

      // ordering of index array should not affect ordering of resulting array
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[4,5,5]")),
            Expressions.parse(" [1,2,3,4,5 ][[-1,4,3]	]").evaluate(null));
      }

      // array indexes should still be rounded down
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[4,5]")),
            Expressions.parse("[1,2,3,4,5][ [3.1,	4.9]]").evaluate(null));
      }

      // should still be able to use negative array indexes
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[4,5]")),
            Expressions.parse("[1,2,3,4,5][[-1, -2]]").evaluate(null));
      }
      // ordering of index array should not affect ordering of resulting array
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[4,5]")),
            Expressions.parse("[1,2,3,4,5][[-2, -1]]").evaluate(null));
      }
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[1,3,3,4,5,5]")),
            Expressions.parse("[1,2,3,4,5][[-1,3,2, - 2.1, 0, -1]]")
               .evaluate(null));
      }

      // and negative array indexes should still be rounded down
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[3,4]")),
            Expressions.parse("[1,2,3,4,5 ]	[[-1.1 ,-2.9] ]").evaluate(null));
      }

      // should get an exception if non-numeric array index used
      try {
         Expressions.parse("[1,2,3,4,5 ]	[[-1.1 ,\"hello\"] ]").evaluate(null);
         Assert.fail("Expected exception was not thrown");
      } catch (EvaluateException ex) {
         Assert.assertEquals("non-numeric value used as array index",
            ex.getMessage());
      }

      try {
         Expressions.parse("$shuffle()").evaluate(null);
         Assert.fail("Expected exception was not thrown");
      } catch (EvaluateException ex) {
         Assert.assertEquals(ShuffleFunction.ERR_ARG1BADTYPE,
            ex.getMessage());
      }

      simpleTest("$shuffle(\"a\")","[\"a\"]");
      simpleTest("$shuffle(1)","[1]");

      // out of bounds indexes in array-type array indexes are just ignored ( no
      // exception )
      Assert.assertEquals(
         ensureAllIntegralsAreLongs(mapper.readTree("[1,2,3]")),
         Expressions.parse("[0,1,2,3][[1..10]]").evaluate(null));
      Assert.assertEquals(ensureAllIntegralsAreLongs(mapper.readTree("[1,2]")),
         Expressions.parse("[1,2,3,4,5 ]	[[6, 1, -5,5] ]").evaluate(null));

      simpleTest("$reverse([1,2,3,4,5])", "[5, 4, 3, 2, 1]");
      simpleTest("$reverse([1..5])", "[5, 4, 3, 2, 1]");
      System.err.println("* " + "$shuffle([1..5])="
         + Expressions.parse("$shuffle([1..5])").evaluate(null));
      simpleTest("$zip([1,2],4,{\"a\":1,\"b\":2})",
         "[[1, 4, {\"a\":1,\"b\":2}]]");
      simpleTest("$zip([])","[]");
      simpleTest("$zip({})","[[{}]]");

      simpleTest("$sort(a.b)", null);
      simpleTest("$sort(\"a\")","[\"a\"]");
      simpleTest("$sort([1,5,3,4,2])", "[1, 2, 3, 4, 5]");
      simpleTest("$sort([1,5,3,4,2],function($l,$r){$l>$r})",
         "[1, 2, 3, 4, 5]");

      try {
         Expressions.parse("$sort()").evaluate(null);
         Assert.fail("Expected exception was not thrown");
      } catch (EvaluateException ex) {
         Assert.assertEquals(SortFunction.ERR_ARG1BADTYPE,
            ex.getMessage());
      }

      simpleTest("($x:=function($l,$r){$l>$r};$sort([1,5,3,4,2],$x))",
         "[1, 2, 3, 4, 5]");
   }

   @Test
   public void testVariableAssignment() throws Exception {
      simpleTest("$test:=10/2", "5");
      simpleTest("($x:=10;$y:=2;$x/$y)", "5");
   }

   @Test
   public void testFunctionDecl() throws Exception {
      simpleTest("function($l, $w, $h){ $l * $w * $h }(10, 10, 5)", "500");
      simpleTest("$string(1)", "\"1\"");
      simpleTest("$map([1..5], $string)",
         "[\"1\", \"2\", \"3\", \"4\", \"5\"]");
      simpleTest(
         "$map([1..3],function($v,$k,$a){$string($v) & \" \" & $string($k)})",
         "[\"1 0\", \"2 1\",\"3 2\"]");
      simpleTest(
         "$map([{\"a\":1,\"value\":2},{\"b\":3,\"value\":4}],function($obj1){$each($obj1,function($v,$k){$k&\"=\"&$v})})",
         "[[\"a=1\", \"value=2\"], [\"b=3\", \"value=4\"]]");
      simpleTest("($x:=function($l){$l*2};$map([1,5,3,4,2],$x))",
         "[2, 10, 6, 8, 4]");
      simpleTest("($x:=function($l){$l>2};$filter([1,5,3,4,2],$x))",
         "[5, 3, 4]");
      simpleTest("($x:=function($l){$l%2=1};$map([1,5,3,4,2],$x))",
         "[true, true, true, false, false]");
      simpleTest("($x:=function($l){$l%2=1};$filter([1,5,3,4,2],$x))",
         "[1, 5, 3]");
      simpleTest("$reduce([1..5],function($i,$j){$i*$j})","120");
      simpleTest("$reduce([1..5],function($i,$j){$i*$j},30)","3600");
      simpleTest("($x:=function($i,$j){$i*$j};$reduce([1..5],$x,30))","3600");
   }

   @Test
   public void testObjectFunctions() throws Exception {
      simpleTest("$keys(a.b)",null);
      simpleTest("$keys({\"a\":1,\"value\":2})", "[\"a\", \"value\"]");
		simpleTest("$lookup({\"a\":1,\"value\":2}, \"a\")", "1");
      {
         Expressions expression = Expressions.parse("$lookup({\"a\":1},1)");
         try {
            expression.evaluate(null);
            Assert.fail("Did not throw an expected exception");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               LookupFunction.ERR_ARG2BADTYPE,
               ex.getMessage());
         }
      }
      simpleTest("$spread({\"a\":1,\"value\":2})", "[{\"a\":1},{\"value\":2}]");
      simpleTest(
         "$spread([{\"a\":1,\"value\":2},{\"b\":{\"value\":{\"d\":5},\"c\":5}},{\"a\":2}])",
         "[{\"a\":1},{\"value\":2},{\"b\":{\"value\":{\"d\":5},\"c\":5}},{\"a\":2}]");
      simpleTest("$spread({})",null);
      simpleTest("$spread([{}])","[]");
      simpleTest("$spread([])",null);
      // TODO: simpleTest("$spread(\"a\")","\"a\"");
      {
         try {
            Expressions.parse("$spread(\"a\")").evaluate(null);
            Assert.fail("Did not throw an expected exception");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               SpreadFunction.ERR_ARG1BADTYPE,
               ex.getMessage());
         }
      }
      // TODO: simpleTest("$spread(1)","1");
      {
         try {
            Expressions.parse("$spread(1)").evaluate(null);
            Assert.fail("Did not throw an expected exception");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               SpreadFunction.ERR_ARG1BADTYPE,
               ex.getMessage());
         }
      }
      // TODO: simpleTest("$spread([1])","[1]");
      {
         try {
            Expressions.parse("$spread([1])").evaluate(null);
            Assert.fail("Did not throw an expected exception");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               SpreadFunction.ERR_ARG1_MUST_BE_ARRAY_OF_OBJECTS,
               ex.getMessage());
         }
      }
      
      simpleTest(
         "$merge([{\"a\":1,\"value\":2},{\"b\":{\"value\":{\"d\":5},\"c\":5}},{\"a\":2}])",
         "{\"a\":2, \"value\":2, \"b\":{\"value\":{\"d\":5},\"c\":5}}");
      simpleTest("$merge(a.b)",null);
      
      {
         Expressions expression = Expressions.parse("$merge()");
         try {
            expression.evaluate(null);
            Assert.fail("Did not throw an expected exception");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               MergeFunction.ERR_ARG1BADTYPE,
               ex.getMessage());
         }
      }
      {
         Expressions expression = Expressions.parse("$merge({\"hello\":1},2)");
         try {
            expression.evaluate(null);
            Assert.fail("Did not throw an expected exception");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               MergeFunction.ERR_ARG2BADTYPE,
               ex.getMessage());
         }
      }
   }

   @Test
   public void testArrayFlattening() throws Exception {
      simpleTest("[ {\"a\": [1,2]}, {\"a\":[3,4]} ].a", "[1,2,3,4]");
   }

   @Test
   public void exceptions() throws Exception {

      // An evaluation exception
      Expressions expression = Expressions
         .parse("$substring(\"abcdefghijk\", 3, 6, 7)");
      try {
         JsonNode result = expression.evaluate(null);
         assertTrue(result.getNodeType() == JsonNodeType.STRING);
         assertTrue(result.isTextual());
         assertTrue(result.asText().equals("def"));
         assertTrue("Should not get here - exception should be thrown", false);
      } catch (EvaluateException e) {
         assertTrue(
            e.getMessage().equals(SubstringFunction.ERR_ARG4BADTYPE));
      }

      // A parsing exception
      try {
         expression = Expressions.parse(" 3 + ");
         // TODO: "3 +" and "3 *" should throw a parse error but they don't --
         // figure out why
         // assertTrue("Should not get here - exception should be thrown",
         // false);
      } catch (ParseException e) {
         // System.out.println(e.getMessage());
         assertTrue(e.getMessage().equals(
            "line 1:5 at [@2,5:4='<EOF>',<-1>,1:5]: mismatched input '<EOF>' expecting {'(', 'true', 'false', STRING, 'null', '[', '{', '$$', NUMBER, 'function', '-', VAR_ID, ID}\n"));
      }

      try {
         expression = Expressions.parse("$stat");
         // assertTrue("Should not get here - exception should be thrown",
         // false);
      } catch (ParseException e) {
         String msg = e.getMessage();
         assertTrue("was " + msg, msg.equals(
            "line 1:5 at [@1,5:4='<EOF>',<-1>,1:5]: no viable alternative at input '$stat'\n"));
      }

      // variable references that do not start with $state or $event are an
      // error
      try {
         expression = Expressions.parse("$dddd");
         // assertTrue("Should not get here - exception should be thrown",
         // false);
      } catch (ParseException e) {
         String msg = e.getMessage();
         assertTrue("was " + msg, msg.equals(
            "line 1:5 at [@1,5:4='<EOF>',<-1>,1:5]: no viable alternative at input '$dddd'\n"));
      }

      // use of a bad hex digit in a unicode escape
      try {
         expression = Expressions.parse("'\\u000x'");
         assertTrue("Should not get here - exception should be thrown", false);
      } catch (ParseException e) {
      }

      // missing hex digit in unicode escape
      try {
         expression = Expressions.parse("'\\u000'");
         assertTrue("Should not get here - exception should be thrown", false);
      } catch (ParseException e) {
      }

   }

   @Test
   public void testBooleanOperators() throws Exception {

      String expr = "1 = 1 ? 1 : 0";
      Expressions expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asInt() == 1);

      expr = "1 = 2 ? 1 : 0";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asInt() == 0);

      expr = "\"a\" = \"a\"";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == true);

      expr = "\"a\" = \"b\"";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == false);

      expr = "$not (\"a\" = \"b\")";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == true);

      expr = "$not (\"a\" = \"b\")";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == true);

      expr = "\"a\" != \"b\"";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == true);

      expr = "\"a\" != 1";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == true);

      expr = "(1=1) and (1=0)";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == false);

      expr = "(1=1) or (1=0)";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == true);

      expr = "true or (1=0)";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(null).asBoolean() == true);

      JsonNode event = mapper.readTree("{\"array\":[2,[3],4]}");

      expr = "2 in array";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(event).asBoolean() == true);

      expr = "$not (2 in array)";
      expression = Expressions.parse(expr);
      assertTrue(expr, expression.evaluate(event).asBoolean() == false);

      simpleTest("\"hello\"=\"hello\"", "true");
      simpleTest("\"hello\"=\"ello\"", "false");
      simpleTest("true=true", "true");
      simpleTest("true=false", "false");
      simpleTest("false=false", "true");

      simpleTest("\"hello\"!=\"hello\"", "false");
      simpleTest("\"hello\"!=\"ello\"", "true");
      simpleTest("true!=true", "false");
      simpleTest("true!=false", "true");
      simpleTest("false!=false", "false");

      // JSONata never considers arrays to be equal (this may change)
      simpleTest("[1]=[1]", "false");
      simpleTest("[1]=[1,2]", "false");
      simpleTest("[true]=[true]", "false");
      simpleTest("[\"a\"]=[\"a\"]", "false");
      simpleTest("[2] in [1,[2]]", "false");

      simpleTest("[1]!=[1]", "true");
      simpleTest("[1]!=[1,2]", "true");
      simpleTest("[true]!=[true]", "true");
      simpleTest("[\"a\"]!=[\"a\"]", "true");

      // JSONata never considers objects to be equal (this may change)
      simpleTest("{}={}", "false");
      simpleTest("{\"a\":1}={\"a\":1}", "false");

      simpleTest("{}!={}", "true");
      simpleTest("{\"a\":1}!={\"a\":1}", "true");

      // membership
      simpleTest("1 in [1,2]", "true");
      simpleTest("1.0 in [1,2]", "true");
      simpleTest("2 in [1,2]", "true");
      simpleTest("2 in [1,[2]]", "false"); // primitives are not equivalent to
                                           // singleton arrays in this context
      simpleTest("[1,2] in 1", "false");
      simpleTest("1.1 in [1.1,2]", "true");
      simpleTest("1.1 in [1.2,2]", "false");
      simpleTest("1 in 1", "true"); // RHS treated as singleton array
      simpleTest("1 in 0", "false");

   }

   @Test
   public void testCompOperators() throws Exception {

      Expressions expression = Expressions.parse("1 < 1");
      assertTrue("1 < 1", expression.evaluate(null).asBoolean() == false);

      expression = Expressions.parse("1 <= 1");
      assertTrue("1 <= 1", expression.evaluate(null).asBoolean() == true);

      expression = Expressions.parse("1 > 1");
      assertTrue("1 <= 1", expression.evaluate(null).asBoolean() == false);

      expression = Expressions.parse("2 >= 1");
      assertTrue("1 <= 1", expression.evaluate(null).asBoolean() == true);

      expression = Expressions.parse("\"a\" < \"b\"");
      assertTrue("1 <= 1", expression.evaluate(null).asBoolean() == true);

      expression = Expressions.parse("\"a\" > \"b\"");
      assertTrue("1 <= 1", expression.evaluate(null).asBoolean() == false);

      expression = Expressions.parse("\"a\" <= \"b\"");
      assertTrue("1 <= 1", expression.evaluate(null).asBoolean() == true);

      expression = Expressions.parse("\"a\" <= \"a\"");
      assertTrue("1 <= 1", expression.evaluate(null).asBoolean() == true);

      expression = Expressions.parse("\"a\" >= \"b\"");
      assertTrue("\"a\" >= \"b\"",
         expression.evaluate(null).asBoolean() == false);

      expression = Expressions.parse("\"a\" >= \"a\"");
      assertTrue("\"a\" >= \"a\"",
         expression.evaluate(null).asBoolean() == true);

   }

   /**
    * Test that for a conditional ternary operator expr 'a ? b : c' where
    * evaluation of 'c' results in an Evaluation exception.
    * 
    * This is to check that we are lazily evaluating the 'b' and 'c'
    * expressions.
    * 
    * @throws ParseException if the expression can not parse correctly
    * @throws EvaluateException if the expression fail its evaluation
    */
   @Test
   public void testConditionalLazyEval()
      throws ParseException, EvaluateException {
      Expressions expression = Expressions.parse(
         "(t < 0) ? \"Tempertature is less than 0\" : \"This will cause a runtime exception and should not appear \" + ($substring(temperatureStatus, temperatureStatus, 0, 1))");

      ObjectNode event = mapper.createObjectNode();
      event.set("t", new FloatNode(-42));

      ObjectNode state = mapper.createObjectNode();
      state.set("temperatureStatus", new TextNode("hot"));

      JsonNode result = expression.evaluate(event);

      assertTrue("should have result 'Tempertature is less than 0'",
         result.asText().equals("Tempertature is less than 0"));

      // now test that when we eval to false, that we explode...
      event.set("t", new FloatNode(42));

      exception.expect(EvaluateException.class);
      expression.evaluate(event);
   }

   // Regression test for 184798: No support or handling for null JSON data type
   // in expression language
   @Test
   public void testNullEquality() throws Exception {
      ObjectNode event = mapper.createObjectNode();
      event.put("asdsad", NullNode.getInstance());
      {
         Expressions expression = Expressions.parse("asdsad=\"null\"");
         Assert.assertEquals(BooleanNode.FALSE, expression.evaluate(event));
      }
      {
         Expressions expression = Expressions.parse("asdsad!=\"null\"");
         Assert.assertEquals(BooleanNode.TRUE, expression.evaluate(event));
      }

      {
         Expressions expression = Expressions.parse("\"null\"=asdsad");
         Assert.assertEquals(BooleanNode.FALSE, expression.evaluate(event));
      }
      {
         Expressions expression = Expressions.parse("\"null\"!=asdsad");
         Assert.assertEquals(BooleanNode.TRUE, expression.evaluate(event));
      }

      {
         Expressions expression = Expressions.parse("asdsad=asdsad");
         Assert.assertEquals(BooleanNode.TRUE, expression.evaluate(event));
      }
      {
         Expressions expression = Expressions.parse("asdsad!=asdsad");
         Assert.assertEquals(BooleanNode.FALSE, expression.evaluate(event));
      }
   }

   // @Test
   // public void test(){
   // Assert.assertTrue( new TextNode("true").asBoolean() );
   // Assert.assertFalse( new TextNode("false").asBoolean() );
   // Assert.assertFalse( new TextNode("T").asBoolean() );
   // Assert.assertFalse( new TextNode("H").asBoolean() );
   //
   // Assert.assertTrue( new IntNode(1).asBoolean() );
   // Assert.assertFalse( new IntNode(0).asBoolean() );
   // Assert.assertTrue( new IntNode(22).asBoolean() );
   // }

   // Regression test for 184798: No support or handling for null JSON data type
   // in expression language
   @Test
   public void testNullComparison() throws Exception {
      final String[] ops = new String[] {
         ">", "<", ">=", "<="
      };

      ObjectNode event = mapper.createObjectNode();
      event.put("isnull", NullNode.getInstance());

      for (String op : ops) {
         try {

            String expression = "isnull" + op + "1";
            Expressions.parse(expression).evaluate(event);
            Assert.fail("Expected EvaluateException was not thrown for "
               + expression + " where LHS value is null");

            expression = "1" + op + "isnull";
            Expressions.parse(expression).evaluate(event);
            Assert.fail("Expected EvaluateException was not thrown for "
               + expression + " where RHS value is null");

         } catch (EvaluateException ex) {
            Assert.assertEquals(
               "The expressions either side of operator \"" + op
                  + "\" must evaluate to numeric or string values",
               ex.getMessage());
         }
      }
   }

   // Regression test for 184798: No support or handling for null JSON data type
   // in expression language
   @Test
   public void testExistsFunction() throws Exception {

      // verify behaviour when just referencing a property that does not exist
      try {
         ObjectNode event = mapper.createObjectNode();
         Expressions.parse("notset").evaluate(event);
      } catch (EvaluateException ex) {
         Assert.assertEquals("The property 'notset' does not exist",
            ex.getMessage());
      }

      // verify behaviour when just referencing a property that is explicitly
      // set to *java* null (not NullNode singleton)
      try {
         ObjectNode event = mapper.createObjectNode();
         event.put("notset", (ObjectNode) null);
         Expressions.parse("notset").evaluate(event);
      } catch (EvaluateException ex) {
         Assert.assertEquals("The property 'notset' does not exist",
            ex.getMessage());
      }

      // check $exists() throws when incorrect number of args
      try {
         Expressions.parse("$exists(foo, bar)").evaluate(null);
      } catch (EvaluateException ex) {
         Assert.assertEquals(ExistsFunction.ERR_ARG2BADTYPE,
            ex.getMessage());
      }
      
      try {
         Expressions.parse("$exists(a.b,\"a\")").evaluate(null);
      } catch (EvaluateException ex) {
         Assert.assertEquals(ExistsFunction.ERR_ARG2BADTYPE,
            ex.getMessage());
      }
      {
         ObjectNode event = mapper.createObjectNode();

         // make sure $exists(prop) returns false when prop is not set
         Assert.assertEquals(BooleanNode.FALSE,
            Expressions.parse("$exists(foo)").evaluate(event));

         // make sure $exists(prop) returns true regardless of the type of the
         // value prop is assigned
         // V NOTE: $exists(null) returns true according to
         // http://try.jsonata.org/
         for (JsonNode valueType : new JsonNode[] {
            NullNode.getInstance(), new IntNode(22), BooleanNode.TRUE,
            new LongNode(22L), new TextNode("true"), new IntNode(0),
            BooleanNode.FALSE, new LongNode(0), new TextNode("false"),
            new TextNode("22")
         }) {
            event.put("foo", valueType);
            Assert.assertEquals(BooleanNode.TRUE,
               Expressions.parse("$exists(foo)").evaluate(event));
         }
      }
   }

   @Test
   public void testArrayConstructor() throws Exception {

      // constructing an array using number primitives
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[1,2,3,4,5]")),
            Expressions.parse("[1,2,3,4,5]").evaluate(null));

      }

      // constructing an array using string primitives
      {
         Assert.assertEquals(mapper.readTree("[\"hello\", \"world\"]"),
            Expressions.parse("[\"hello\",  \"world\"	]").evaluate(null));
      }

      // constructing an array using boolean primitives
      {
         Assert.assertEquals(mapper.readTree("[true, false, true]"),
            Expressions.parse("[true, false, true]").evaluate(null));
      }

      // constructing an array using mixed primitives
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(
               mapper.readTree("[true, \"hello\", 22]")),
            Expressions.parse("[ true, \"hello\", 22]").evaluate(null));
      }

      // constructing an array of arrays
      {
         Assert.assertEquals(
            mapper.readTree("[[true, [false]], [false, true]]"), Expressions
               .parse("[[true, [false ]], [false, true]  ]").evaluate(null));
      }

      // empty array
      {
         Assert.assertEquals(mapper.readTree("[]"),
            Expressions.parse("[]").evaluate(null));
      }

      simpleTest("{\"a\":[0,1,2,3,4]}.a", "[0,1,2,3,4]");
      simpleTest("[{\"a\":[0,1,2,4]},{\"a\":[0,1,2,4]}].a",
         "[0,1,2,4,0,1,2,4]");
      // test("[{\"a\":[0,1,2,4]},{\"a\":[0,1,2,4]}].[a]",
      // "[[0,1,2,4],[0,1,2,4]]");

      simpleTest(
         "[{\"City\":\"Winchester\"}, {\"Alternative.Address\":{\"City\":\"London\"}}.`Alternative.Address`].City",
         "[\"Winchester\", \"London\"]");

   }

   @Test
   public void testArraySeqConstructor() throws Exception {
      // simple
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[1,2]")),
            Expressions.parse(" [ 1 ..	2 ]	").evaluate(null));
      }

      // if start > end, an empty array should be produced
      {
         Assert.assertEquals(ensureAllIntegralsAreLongs(mapper.readTree("[]")),
            Expressions.parse(" [20..1]	").evaluate(null));
      }

      // should be able to use expressions to generate start/end indexes
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[2,3,4,5,6,7,8]")),
            Expressions.parse(" [1 +1	.. (2+ 2)*2]	").evaluate(null));
      }

      // attempt to use non-integral index should parse, but throw an evaluation
      // exception
      {
         Expressions e = Expressions.parse(" [0.1..2]	");
         try {
            e.evaluate(null);
            Assert.fail("Expected exception was not thrown");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               "The left side of the range operator (..) must evaluate to an integer",
               ex.getMessage());
         }
      }

      // attempt to use non-integral index should parse, but throw an evaluation
      // exception
      {
         Expressions e = Expressions.parse(" [1..\"hello\"]	");
         try {
            e.evaluate(null);
            Assert.fail("Expected exception was not thrown");
         } catch (EvaluateException ex) {
            Assert.assertEquals(
               "The right side of the range operator (..) must evaluate to an integer",
               ex.getMessage());
         }
      }
   }

   @Test
   public void testAppendFunction() throws Exception {

      // simple
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[1,2,3,4]")),
            Expressions.parse("$append([1,2], [3,4])").evaluate(null));
      }
      // null references
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[null,1,2]")),
            Expressions.parse("$append(null, [1,2])").evaluate(null));
      }

      // non-array inputs should be treated as singletons
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[1,2,3]")),
            Expressions.parse("$append([1,2], 3)").evaluate(null));
      }
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[2,3, 4]")),
            Expressions.parse("$append(2, [3..4])").evaluate(null));
      }
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree("[2,3]")),
            Expressions.parse("$append(2, 3)").evaluate(null));
      }

      // should get exception when attempting to invoke with wrong number of
      // params
      {
         Expressions e = Expressions.parse("$append(1,2,3)");
         try {
            e.evaluate(null);
            Assert.fail("Expected exception was not thrown");
         } catch (EvaluateException ex) {
            Assert.assertEquals(AppendFunction.ERR_ARG3BADTYPE,
               ex.getMessage());
         }
      }

      // appending an empty array should have no effect
      {
         simpleTest("$append([], [1,2])", "[1,2]");
         simpleTest("$append([1,2], [])", "[1,2]");
      }

      // appending *no match* should have no effect
      {
         simpleTest("$append([][0], [1,2])", "[1,2]");
         simpleTest("$append([1,2], [][0])", "[1,2]");
      }
      
      // bad arguments
      Expressions e = Expressions.parse("$append(null)");
      try {
         e.evaluate(null);
         Assert.fail("Expected exception was not thrown");
      } catch (EvaluateException ex) {
         Assert.assertEquals(AppendFunction.ERR_ARG2BADTYPE,ex.getMessage());
      }
      
      // context variable
      // may become "[null]" once jsonata is updated
      simpleTest("a.b.c~>$append(null)","null"); 
      simpleTest("$append(\"abc\",a.b.c)","\"abc\"");
      simpleTest("$append(a.b.c,\"abc\")","\"abc\"");
      simpleTest("$append(a.b.c, a.b.c)",null);
      simpleTest("null~>$append(null)","[null,null]");

   }

   @Test
   public void testCountFunction() throws Exception {
      // simple test
      {
         Assert.assertEquals(new LongNode(2),
            Expressions.parse("$count([1..2])").evaluate(null));
      }

      // empty array should return 0
      {
         Assert.assertEquals(new LongNode(0),
            Expressions.parse("$count([])").evaluate(null));
      }

      // any non-array input should always return 1
      {
         Assert.assertEquals(new LongNode(1),
            Expressions.parse("$count(1)").evaluate(null));
         Assert.assertEquals(new LongNode(1),
            Expressions.parse("$count(\"hello\")").evaluate(null));
         Assert.assertEquals(new LongNode(1),
            Expressions.parse("$count({})").evaluate(null));
         Assert.assertEquals(new LongNode(1), Expressions
            .parse("$count({\"hello\":1, \"world\":2})").evaluate(null));
         Assert.assertEquals(new LongNode(1),
            Expressions.parse("$count(true)").evaluate(null));
         Assert.assertEquals(new LongNode(1),
            Expressions.parse("$count(false)").evaluate(null));

         // $count(undefined)=0
         Assert.assertEquals(new LongNode(0),
            Expressions.parse("$count(nothing)").evaluate(null));

      }

      // should get exception when attempting to invoke with wrong number of
      // params
      {
         Expressions e = Expressions.parse("$count(1,2)");
         try {
            e.evaluate(null);
            Assert.fail("Expected exception was not thrown");
         } catch (EvaluateException ex) {
            Assert.assertEquals(CountFunction.ERR_ARG2BADTYPE,
               ex.getMessage());
         }
      }
   }

   @Test
   public void testObjectConstructors() throws Exception {
      // empty object
      {
         Assert.assertEquals(mapper.readTree("{}"),
            Expressions.parse("{}").evaluate(null));
      }
      // single field
      {
         Assert.assertEquals(mapper.readTree("{\"hello\": \"world\"}"),
            Expressions.parse("{\"hello\": \"world\"}").evaluate(null));
      }

      // check we can use single quotes too
      {
         Assert.assertEquals(mapper.readTree("{\"hello\": \"world\"}"),
            Expressions.parse("{'hello': 'world'}").evaluate(null));
      }
      // check field names and values are escaped properly
      {
         Assert.assertEquals(
            mapper.readTree(
               "{\"he'llo\": \"wor'ld\", \"ho\\\"la\":\"wor\\\"ld\"}"),
            Expressions
               .parse("{'he\\'llo': 'wor\\'ld', 'ho\"la':\"wor\\\"ld\"}")
               .evaluate(null));
      }

      // multiple fields
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(mapper.readTree(
               "{\"hello\": \"world\", \"tom\": 22, \"bool\": true}")),
            Expressions
               .parse("{\"hello\": \"world\", \"tom\":22, 'bool': true}")
               .evaluate(null));
      }

      // nested fields
      {
         Assert.assertEquals(
            ensureAllIntegralsAreLongs(
               mapper.readTree("{\"this\":{\"is\":{\"nested\":[1,2,3,4]}}}")),
            Expressions.parse("{'this':{'is':{'nested':[1,2,3,4]}}}")
               .evaluate(null));
      }

      // invalid object syntax
      try {
         Expressions.parse("{'this':'that'");
         Assert.fail("Expected exception was not thrown");
      } catch (ParseException ex) {
      }

      // cannot use backquotes for field names
      try {
         Expressions.parse("{`hello`:\"world\"}");
         Assert.fail("Expected exception was not thrown");
      } catch (ParseException ex) {
      }

      // using expressions to determine field names
      // test("{\"a\"&\"b\":\"c\"}", "{\"ab\": \"c\"}");

   }

   @Test
   public void testSumFunction() throws Exception {
      // simple test
      {
         Assert.assertEquals(new DoubleNode(22 + 44.2 + -2.1 + -2),
            Expressions.parse("$sum([22, 44.2, -2.1, -2])").evaluate(null));
      }

      // sum of empty array = 0
      {
         Assert.assertEquals(new LongNode(0),
            Expressions.parse("$sum([])").evaluate(null));
      }

      // single (non-array) numeric parameter
      {
         Assert.assertEquals(new DoubleNode(22.2),
            Expressions.parse("$sum(22.2)").evaluate(null));
         Assert.assertEquals(new DoubleNode(-22.2),
            Expressions.parse("$sum(-22.2)").evaluate(null));
         Assert.assertEquals(new LongNode(22),
            Expressions.parse("$sum(22)").evaluate(null));
         Assert.assertEquals(new LongNode(-22),
            Expressions.parse("$sum(-22)").evaluate(null));
      }

      // should get a LongNode if sum only includes integrals
      {
         Assert.assertEquals(new LongNode(62),
            Expressions.parse("$sum([22, 44, -2, -2])").evaluate(null));
      }

      // should get a DoubleNode if sum includes any floats

      {
         Assert.assertEquals(new DoubleNode(62),
            Expressions.parse("$sum([22, 44.0, -2, -2])").evaluate(null));
      }

      // expect exception when input array includes non-numerics
      {
         for (String param : new String[] {
            "true", "false", "\"hello\"", "{}", "[[]]", "[true]", "[false]",
            "[\"hello\"]", "[{}]", "[2,\"hello\"]" // Note: "[0,1,2][3]" returns null
         }) {
            Expressions e = Expressions.parse("$sum(" + param + ")");
            try {
               e.evaluate(null);
               Assert.fail("Expected exception was not thrown");
            } catch (EvaluateException ex) {
               Assert.assertEquals(SumFunction.ERR_ARG1ARRTYPE, ex.getMessage());
            }
         }
      }

      // expect exception when wrong number of arguments are used
      {
         Expressions e = Expressions.parse("$sum(1,2)");
         try {
            e.evaluate(null);
            Assert.fail("Expected exception was not thrown");
         } catch (EvaluateException ex) {
            Assert.assertEquals(SumFunction.ERR_ARG2BADTYPE, ex.getMessage());
         }
      }

   }

   @Test
   public void testAverageFunction() throws Exception {
      {
         Assert.assertEquals(new DoubleNode(3.0),
            Expressions.parse("$average([1,2,3,4,5])").evaluate(null));
      }
      {
         Assert.assertEquals(new DoubleNode(3.3), Expressions
            .parse("$average([1.1,2.2,3.3,4.4,5.5])").evaluate(null));
      }
      {
         // have to do it this way because of floating-point math rounding
         // errors
         Assert.assertTrue(
            (-0.58) - Expressions.parse("$average([-1.2,2.3,-3.2,4.6,-5.4])")
               .evaluate(null).doubleValue() < 0.000001);
      }
      {
         Assert.assertEquals(BooleanNode.TRUE,
            Expressions.parse(
               "$average([1,2,3,4,5])=($sum([1,2,3,4,5])/$count([1,2,3,4,5]))")
               .evaluate(null));
      }
      {
         Assert.assertEquals(BooleanNode.TRUE, Expressions.parse(
            "$average([-1.2,2.3,-3.2,4.6,-5.4])=($sum([-1.2,2.3,-3.2,4.6,-5.4])/$count([-1.2,2.3,-3.2,4.6,-5.4]))")
            .evaluate(null));
      }

      // single (non-array) numeric parameter
      // regardless of the type of input, $average should always return as a
      // DoubleNode
      {
         Assert.assertEquals(new DoubleNode(22.2),
            Expressions.parse("$average(22.2)").evaluate(null));
         Assert.assertEquals(new DoubleNode(-22.2),
            Expressions.parse("$average(-22.2)").evaluate(null));
         Assert.assertEquals(new DoubleNode(22),
            Expressions.parse("$average(22)").evaluate(null));
         Assert.assertEquals(new DoubleNode(-22),
            Expressions.parse("$average(-22)").evaluate(null));
      }

      // expect exception when input array is empty or includes non-numerics
      {
         for (String param : new String[] {
            "true", "false", "\"hello\"", "{}", "[]", "[[1]]", "[[]]", "[true]",
            "[false]", "[\"hello\"]", "[{}]", "[2,\"hello\"]" // Note, "[0,1,2][3]" is null
         }) {
            Expressions e = Expressions.parse("$average(" + param + ")");
            try {
               e.evaluate(null);
               Assert.fail("Expected exception was not thrown");
            } catch (EvaluateException ex) {
               Assert.assertEquals(AverageFunction.ERR_ARG_TYPE,
                  ex.getMessage());
            }
         }
         {
            Expressions e = Expressions.parse("$average()");
            try {
               e.evaluate(null);
               Assert.fail("Expected exception was not thrown");
            }  catch (EvaluateException ex) {
               Assert.assertEquals(AverageFunction.ERR_ARG1BADTYPE,
                  ex.getMessage());
            }
         }
         {
            Expressions e = Expressions.parse("a.b.c~>$average([1,2,3])");
            try {
               e.evaluate(null);
               Assert.fail("Expected exception was not thrown");
            }  catch (EvaluateException ex) {
               Assert.assertEquals(AverageFunction.ERR_ARG2BADTYPE,
                  ex.getMessage());
            }
         }
         simpleTest("[1,2,3]~>$average()","2.0");
         simpleTest("a.b.c~>$average()",null);
      }

      // expect exception when wrong number of arguments are used
      {
         Expressions e = Expressions.parse("$average(1,2)");
         try {
            e.evaluate(null);
            Assert.fail("Expected exception was not thrown");
         } catch (EvaluateException ex) {
            Assert.assertEquals(AverageFunction.ERR_ARG2BADTYPE, ex.getMessage());
         }
      }
   }

   @Test
   public void testBasicSelection() throws Exception {

      // array of objects
      {
         // [{"a":1}, {"a":2}, {"a":3}, {"a":4}, {"a":5}, {"a":[6,7]}]
         final String input = "[" + "		{\"a\":1}," + "		{\"a\":2},"
            + "		{\"a\":3}," + "		{\"a\":4}," + "		{\"a\":5},"
            + "		{\"a\":[6,7]}" + "]";

         simpleTest(input + ".a", "[ 1, 2, 3, 4, 5, 6, 7]");
         simpleTest(input + ".a.b", null);
         simpleTest("(" + input + ".a).b", null);
         simpleTest(input + ".a[0]", "[ 1, 2, 3, 4, 5, 6]");
         simpleTestExpectException(input + ".a[\"\"]",
            NonNumericArrayIndexException.MSG);
         simpleTestExpectException(input + ".a[\"hello\"]",
            NonNumericArrayIndexException.MSG);
         simpleTest(input + ".a[true]", "[ 1, 2, 3, 4, 5, 6, 7]");
         simpleTestExpectException(input + ".a[[true]]",
            NonNumericArrayIndexException.MSG);
         simpleTest(input + ".a[false]", null);
         simpleTestExpectException(input + ".a[[false]]",
            NonNumericArrayIndexException.MSG);
         simpleTestExpectException(input + ".a[[[false]]]",
            NonNumericArrayIndexException.MSG);
         simpleTest("(" + input + ").a", "[ 1, 2, 3, 4, 5, 6, 7]");
         simpleTest("($count(" + input + ".a))", "7");
         simpleTest("(" + input + ".a)", "[ 1, 2, 3, 4, 5, 6, 7]");

         simpleTest(input + ".a[0][0]", "[ 1, 2, 3, 4, 5, 6]");
         simpleTest(input + ".a[0][1]", null);
         simpleTest(input + ".a[1]", "7");
         simpleTest(input + ".a[1][0]", "7");
         simpleTest(input + ".a[1][1]", null);
         simpleTest(input + ".a[2]", null);
         simpleTest(input + ".a[2][0]", null);
         simpleTest(input + ".a[[0]]", "[ 1, 2, 3, 4, 5, 6]");
         simpleTest(input + ".a[[1]]", "7");
         simpleTest(input + ".a[[0,1]]", "[ 1, 2, 3, 4, 5, 6, 7]");
         simpleTest(input + ".a[[0,1,2]]", "[ 1, 2, 3, 4, 5, 6, 7]");
         simpleTest(input + ".a[[1,2]]", "7");
      }

      {
         // [{"a": [{"b":[0,1,2]}]}, {"a": [{"b":[3,4,5]}, {"b":[6,7,8]}]}]
         String input = "[{\"a\": [{\"b\":[0, 1, 2]}]}, {\"a\": [{\"b\":[3, 4, 5]}, {\"b\":[6,7,8]}]}]";
         simpleTest(input + ".a[0]", "[{\"b\":[0,1,2]}, {\"b\":[3,4,5]}]");
         simpleTest(input + ".a[1]", "{\"b\":[6,7,8]}");
         simpleTest(input + ".a[2]", null);
         simpleTest(input + ".a[0].b", "[0,1,2,3,4,5]");
         simpleTest(input + ".a[1].b", "[6,7,8]");
         simpleTest(input + ".a[2].b", null);
         simpleTest(input + ".a[0].b[0]", "[0,3]");
         simpleTest(input + ".a[0].b[1]", "[1,4]");
         simpleTest(input + ".a[0].b[2]", "[2,5]");
         simpleTest(input + ".a[0].b[3]", null);
         simpleTest(input + ".a[1].b[0]", "6");
         simpleTest(input + ".a[1].b[1]", "7");
         simpleTest(input + ".a[1].b[2]", "8");
         simpleTest(input + ".a[1].b[3]", null);
         simpleTest(input + ".a[2].b[0]", null);

         // verify effect of parentheses in various positions
         simpleTest("(" + input + ".a)[0]", "{\"b\":[0,1,2]}");
         simpleTest("(" + input + ".a)[1]", "{\"b\":[3,4,5]}");
         simpleTest("(" + input + ".a)[2]", "{\"b\":[6,7,8]}");
         simpleTest("(" + input + ".a)[0].b", "[0,1,2]");
         simpleTest("(" + input + ".a)[1].b", "[3,4,5]");
         simpleTest("(" + input + ".a)[2].b", "[6,7,8]");
         simpleTest("(" + input + ".a[0]).b", "[0,1,2,3,4,5]");
         simpleTest("(" + input + ".a[1]).b", "[6,7,8]");
         simpleTest("(" + input + ".a[2]).b", null);

         simpleTest("(" + input + ".a)[0].b[0]", "0");
         simpleTest("(" + input + ".a)[0].b[1]", "1");
         simpleTest("(" + input + ".a)[0].b[2]", "2");
         simpleTest("(" + input + ".a[0]).b[0]", "[0,3]");
         simpleTest("(" + input + ".a[1]).b[0]", "6");
         simpleTest("(" + input + ".a[2]).b[0]", null);
         simpleTest("(" + input + ".a[0]).b[1]", "[1,4]");
         simpleTest("(" + input + ".a[1]).b[1]", "7");
         simpleTest("(" + input + ".a[2]).b[1]", null);
         simpleTest("(" + input + ".a[0]).b[2]", "[2,5]");
         simpleTest("(" + input + ".a[1]).b[2]", "8");

         // test(input+".b", null);
      }
      // array of array of objects
      {
         // [[{"a":1}, {"a":2}, {"a":3}], [{"a":4}, {"a":5}], [{"a":6}]]
         final String input = "[" + "		[{\"a\":1}, {\"a\":2}, {\"a\":3}], "
            + "		[{\"a\":4}, {\"a\":5}]," + "		[{\"a\":6}]," + "		[]"
            + "]";

         simpleTest(input + "[0]", "[{\"a\":1}, {\"a\":2}, {\"a\":3}]");
         simpleTest(input + "[0][0]", "{\"a\":1}");
         simpleTest(input + ".a", "[1,2,3,4,5,6]");
         simpleTest(input + ".a[0]", "[1,4,6]");
         simpleTest(input + ".a[0][0]", "[1,4,6]"); // this result seems strange
                                                    // to me, but it's the
                                                    // behaviour of
                                                    // try.jsonata.org
         simpleTest(input + ".a[1]", "[2,5]");
         simpleTest(input + ".a[2]", "3");
         simpleTest(input + ".a[3]", null);
         simpleTest(input + ".a[[0]]", "[1,4,6]");
         simpleTest(input + ".a[[1]]", "[2,5]");
         simpleTest(input + ".a[[2]]", "3");
         simpleTest(input + ".a[[3]]", null);
         simpleTest(input + ".a[[0,1]]", "[1,2,4,5,6]");
         simpleTest(input + ".a[[1,0]]", "[1,2,4,5,6]");
         simpleTest(input + ".a[[0,1,2]]", "[1,2,3,4,5,6]");
         simpleTest(input + ".a[[0,1,2,3]]", "[1,2,3,4,5,6]");
         simpleTest(input + ".a[[1,2,3]]", "[2,3,5]");
         simpleTest(input + ".a[[3,2,1]]", "[2,3,5]");
         simpleTest(input + ".a[[1,2,3,2,1]]", "[2,2,3,3,5,5]");
      }

      // array of objects each with an array of objects
      {
         // [{"a": [{"b":1}, {"b":2}]}, {"a": [{"b":3}]}, {"a":[{"b":4},
         // {"b":5}]}, {"a":[]}, {"b":[{"b":6}]}, {"c":[{"b":7}]}, 8, {"b":9}]
         final String input = "[" + "	{\"a\": [" + "		{\"b\":1}, "
            + "		{\"b\":2}" + "	]}, " + "	{\"a\": [" + "		{\"b\":3}"
            + "	]}, " + "	{\"a\":[" + "		{\"b\":4}, " + "		{\"b\":5}"
            + "	]},"

            // and throw in some different types of values to check they do not
            // interfere with selection
            // (belt&braces - we do not actually allow definition of
            // heterogeneous arrays like this in IM schemas)
            + " {\"a\":[]}," + " {\"b\":[" + "		{\"b\":6}" + " ]},"
            + " {\"c\":[" + "		{\"b\":7}" + " ]}," + " 8," + " {\"b\":9},"
            + " {\"a\":[{\"x\":22}]}" + "]";

         simpleTest(input + "[6]", "8");
         simpleTest(input + ".a",
            "[{\"b\":1}, {\"b\":2}, {\"b\":3}, {\"b\":4}, {\"b\":5}, {\"x\":22}]");
         simpleTest(input + ".a.b", "[1,2,3,4,5]");
         simpleTest(input + ".a.x", "22");

         simpleTest(input + ".a[0]",
            "[{\"b\":1}, {\"b\":3}, {\"b\":4}, {\"x\":22} ]");
         simpleTest(input + ".a[1]", "[{\"b\":2}, {\"b\":5} ]");
         simpleTest(input + ".a[2]", null);

         simpleTest(input + ".a[0][0]",
            "[{\"b\":1}, {\"b\":3}, {\"b\":4}, {\"x\":22} ]"); // this result
                                                               // seems strange
                                                               // to me, but
                                                               // it's the
                                                               // behaviour of
                                                               // try.jsonata.org
         simpleTest("(" + input + ".a[0])[0]", "{\"b\":1}");
         simpleTest(input + ".a[0].b", "[1,3,4]");
         simpleTest("(" + input + ".a[0]).b", "[1,3,4]");

         simpleTest(input + ".a[[0]]",
            "[{\"b\":1}, {\"b\":3}, {\"b\":4}, {\"x\":22} ]");
         simpleTest(input + ".a[[1]]", "[{\"b\":2}, {\"b\":5}]");

         simpleTest(input + ".a[1]", "[{\"b\":2}, {\"b\":5} ]");

      }

      // check it can select down multiple levels of arrays
      {
         simpleTest(
            "[{\"a\":0}, [{\"a\":1},{\"a\":2}], [{\"a\":3},{\"a\":4}, [{\"a\":5}]]].a",
            "[0,1,2,3,4,5]");
         simpleTest(
            "[{\"a\":0}, [{\"a\":1},{\"a\":2}], [{\"a\":3},{\"a\":4}, [{\"a\":5}]]].a[0]",
            "[0,1,3]");
         simpleTest(
            "[{\"a\":0}, [{\"a\":1},{\"a\":2}], [{\"a\":3},{\"a\":4}, [{\"a\":5}]]].a[1]",
            "[2,4]");
         simpleTest(
            "[{\"a\":0}, [{\"a\":1},{\"a\":2}], [{\"a\":3},{\"a\":4}, [{\"a\":5}]]].a[2]",
            "5");
         simpleTest(
            "[{\"a\":0}, [{\"a\":1},{\"a\":2}], [{\"a\":3},{\"a\":4}, [{\"a\":{\"a\":[{\"a\":5}]}}]]].a.a.a",
            "5");
      }

      // check works with arrays constructed from expressions
      simpleTest("[{\"a\": 1+1}, {\"a\":3}].a", "[2, 3]");

      // check objects lacking referenced fields are ommitted (and don't get
      // added to result as null)
      simpleTest("[{\"a\":3}, {}].a", "3");

      // check arrays with mixed datatypes
      // [{"a": 1}, {"a":"string"}, {"a": 1.2}, {"a": true}, {"a":{}},
      // {"a":[]}].a
      simpleTest(
         "[{\"a\": 1}, {\"a\":\"string\"}, {\"a\": 1.2}, {\"a\": true}, {\"a\":{}}, {\"a\":[]}].a",
         "[1, \"string\", 1.2, true, {}]");

      // check works against state JSON
      test("Phone.ns", "[\"h11\", \"h12\", \"h21\", \"h22\"]", null,
         "{ \"Phone\": " + "    [ "
            + "       {\"t\":\"h\", \"ns\":[\"h11\", \"h12\"]},"
            + "       {\"t\":\"h\"},"
            + "       {\"t\":\"h\", \"ns\":[\"h21\", \"h22\"]}" + "    ]"
            + "}");

      test("Phone.ns[0]", "[\"h11\", \"h21\"]]", null, "{ \"Phone\": "
         + "    [ " + "       {\"t\":\"h\", \"ns\":[\"h11\", \"h12\"]},"
         + "       {\"t\":\"h\"},"
         + "       {\"t\":\"h\", \"ns\":[\"h21\", \"h22\"]}" + "    ]" + "}");

      // selector used as input to aggregation functions
      {

         test("$sum(spaces.occupied)", "2", null,
            "{\"spaces\":[{\"occupied\":1},{\"occupied\":0},{\"occupied\":0},{\"occupied\":1}]}");

      }

      // selecting member names that include spaces and special chars
      {
         String n = "  \\\" \\/ \\b \\f \\n \\r \\t  \\uaA2d ";
         String input = "[{\"" + n + "\":1}, {\"" + n + "\":2}]";
         simpleTest(input + ".`" + n + "`", "[ 1, 2]");
      }

      // selecting empty member names
      simpleTest("[{\"\":1}, {\"\":2}].``", "[1,2]");

   }

   @Test
   public void testComplexSelection() throws Exception {
      simpleTest("[{\"a\":1}, {\"a\":2}][a=2]", "{\"a\":2}");
      simpleTest("[{\"a\":{\"b\":2}}, {\"a\":{\"b\":3}}][a.b=2]",
         "{\"a\":{\"b\":2}}");
      simpleTest("[{\"a\":1}, {\"a\":2}][a=1+1]", "{\"a\":2}");

      simpleTest("[{\"a\":true}, {\"a\":false}][a and true]", "{\"a\":true}");
      simpleTest("[{\"a\":true}, {\"a\":false}][a or true]",
         "[{\"a\":true}, {\"a\":false}]");

      // paths can be on both sides of the binary statement
      simpleTest("[{\"a\":1, \"b\":1}, {\"a\":2, \"b\":3}][a=b]",
         "{\"a\":1, \"b\":1}");

      // what about when the path doesn't exist?
      simpleTest("[{\"b\":1}][a=2]", null);
      simpleTest("[{\"b\":1}][a in [2]]", null);
      simpleTest("[{\"b\":1}][a or true]", null);
      simpleTest("[{\"b\":1}][a and true]", null);

      // check singleton array / value equivlance used in the context of
      // predicates
      // test("[{\"a\":[1]}][a=1]", "{\"a\":[1]}"); // (even tho [1]!=1
      simpleTest("[{\"a\":[1]}][a=[1]]", null); // because [1]!=[1]

      simpleTest("[{\"b\":1}][true]", "{\"b\":1}");
      simpleTest("[{\"a\":[1]}][1 in a]", "{\"a\":[1]}");

      simpleTest("[{\"a\":1, \"b\":[2,1]}, {\"a\":2, \"b\":[1,3]}][a in b]",
         "{\"a\":1,\"b\":[2,1]}");
      simpleTest("[{\"a\":1, \"b\":[1]}, {\"a\":2, \"b\":[1,4]}][a+a in b]",
         "{\"a\":2,\"b\":[1,4]}");

      // using predicates on the output of basic selectors (selection semantics
      // should NOT be applied)
      simpleTest("[{\"a\":{\"b\":1}}].a[b=1]", "{\"b\":1}");
      simpleTest("([{\"a\":{\"b\":1}}].a)[b=1]", "{\"b\":1}");

      {
         // [{"a":[{"b":0},{"b":1}]}, {"a":[{"b":1}, {"b":2}]}]
         String input = "[{\"a\":[{\"b\":0},{\"b\":1}]}, {\"a\":[{\"b\":1}, {\"b\":2}]}]";

         // semantics should be equivalent when input is normal array or return
         // of a selector
         simpleTest(input + ".a", "[{\"b\":0},{\"b\":1},{\"b\":1},{\"b\":2}]");
         simpleTest(input + ".a[b=0]", "{\"b\":0}");
         simpleTest(input + ".a[b=1]", "[{\"b\":1}, {\"b\":1}]");
         simpleTest(input + ".a[b=2]", "{\"b\":2}");
         simpleTest(input + ".a[b=3]", null);
         simpleTest(input + ".a[0][b=0]", "{\"b\":0}");

         simpleTest("(" + input + ".a)[b=0]", "{\"b\":0}");
         simpleTest("(" + input + ".a)[b=1]", "[{\"b\":1}, {\"b\":1}]");
         simpleTest("(" + input + ".a)[b=2]", "{\"b\":2}");
         simpleTest("(" + input + ".a)[b=3]", null);
         simpleTest("(" + input + ".a[0])[b=0]", "{\"b\":0}");

      }

      // using array indexes in a predicate path
      {
         String input = "[{\"a\":[0,1]}, {\"a\":[0,2]}]";
         simpleTest(input + "[a[0]=0]", "[{\"a\":[0,1]},{\"a\":[0,2]}]");
         simpleTest(input + "[a[0]=1]", null);
         simpleTest(input + "[a[1]=0]", null);
         simpleTest(input + "[a[1]=1]", "{\"a\":[0,1]}");
         simpleTest(input + "[a[1]=2]", "{\"a\":[0,2]}");

      }
      {
         String input = "[{\"a\":[{\"b\":1}]}, {\"a\":[{\"b\":1},{\"b\":2},{\"b\":3}]}]";
         simpleTest(input + "[a[0].b=1]",
            "[{\"a\":[{\"b\":1}]},{\"a\":[{\"b\":1},{\"b\":2},{\"b\":3}]}]");
         simpleTest(input + "[a[0].b=2]", null);
         simpleTest(input + "[a[1].b=1]", null);
         simpleTest(input + "[a[1].b=2]",
            "{\"a\":[{\"b\":1},{\"b\":2},{\"b\":3}]}");
      }

      // using predicates inside predicates - this is why we need a context
      // stack (rather than just a single value)!
      {
         String input = "[{\"x\":2}, {\"x\":3}] [x=([{\"a\":101, \"b\":2}, {\"a\":102, \"b\":3}]";
         simpleTest(input + "[a=100]).b]", null);
         simpleTest(input + "[a=101]).b]", "{\"x\":2}");
         simpleTest(input + "[a=102]).b]", "{\"x\":3}");
      }

      simpleTest("[{\"a\":[{\"b\":0}]}, {\"a\":[{\"b\":1}]}][a.b=1]",
         "{\"a\":[{\"b\":1}]}");
      simpleTest(
         "[{\"a\":[{\"b\":0}]}, {\"a\":[{\"b\":1}]}, {\"a\":[{\"b\":2}]}][a.b>=1]",
         "[{\"a\":[{\"b\":1}]},{\"a\":[{\"b\":2}]}]");
   }

}