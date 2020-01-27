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

package com.api.jsonata4java.expressions.functions;

import java.util.ArrayList;
import java.util.List;

import com.api.jsonata4java.Expression;
import com.api.jsonata4java.expressions.EvaluateRuntimeException;
import com.api.jsonata4java.expressions.ExpressionsVisitor;
import com.api.jsonata4java.expressions.generated.MappingExpressionParser.Function_callContext;
import com.api.jsonata4java.expressions.utils.Constants;
import com.api.jsonata4java.expressions.utils.FunctionUtils;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A Function that invokes an native Java function.
 *
 * This is to allow extensibility of expressions with functions implemented with Java
 * by calling {@link Expression#assign(String, TypedFunction)}.
 */
public class JavaFunction extends FunctionBase implements Function
{

	private final String varname;
	private final TypedFunction<JsonNode> tFunc;

	public JavaFunction(String varname, TypedFunction<JsonNode> function)
	{
		this.varname = varname;
		this.tFunc = function;
	}

	public JsonNode invoke(ExpressionsVisitor expressionVisitor, Function_callContext ctx) {

		// Retrieve the number of arguments
		boolean useContext = FunctionUtils.useContextVariable(ctx, getSignature());
		int argCount = getArgumentCount(ctx);
		if (useContext) {
			argCount++;
		}

		int argSize = tFunc.getArgTypes().size();
		// Make sure that we have the right number of arguments
		if (argCount > argSize) {
			throw new EvaluateRuntimeException(String.format(Constants.ERR_MSG_TOO_MANY_ARGS, varname));
		}
		// Make sure that we have the right number of arguments
		if (argCount < argSize) {
			throw new EvaluateRuntimeException(String.format(getMessageTemplate(argSize-argCount-1, useContext), varname));
		}

		// Populate args into an input list
		List<JsonNode> input = new ArrayList<>(argCount);
		if (useContext) {
			input.add(0, FunctionUtils.getContextVariable(expressionVisitor));
		}
		for (int i=useContext?1:0;i<argCount;i++) {
			int argIndex = useContext?i-1:i;
			input.add(i, FunctionUtils.getValuesListExpression(expressionVisitor, ctx, argIndex));
		}

		try {
			return tFunc.apply(input);
		}
		catch (TypedFunction.InvalidType ex) {
			byte arg = ex.getFailedArg();
			if (arg < 0) {
				throw new EvaluateRuntimeException(String.format(Constants.ERR_MSG_RETURN_BAD_TYPE, varname));
			} else {
				throw new EvaluateRuntimeException(String.format(getMessageTemplate(arg, useContext), varname));
			}
		}
	}

	@Override
	public String getSignature() {
		return "";
	}

	private String getMessageTemplate(int arg, boolean useContext) {
		String messageTemplate = "Unexpected arg error in function %s";
		if (arg == 0) {
			messageTemplate = useContext ? Constants.ERR_MSG_BAD_CONTEXT : Constants.ERR_MSG_ARG1_BAD_TYPE;
		} else if (arg == 1) {
			messageTemplate = Constants.ERR_MSG_ARG2_BAD_TYPE;
		}
		return messageTemplate;
	}
}
