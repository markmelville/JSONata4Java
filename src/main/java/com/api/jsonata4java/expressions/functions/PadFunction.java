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

import com.api.jsonata4java.expressions.EvaluateRuntimeException;
import com.api.jsonata4java.expressions.ExpressionsVisitor;
import com.api.jsonata4java.expressions.generated.MappingExpressionParser.Function_callContext;
import com.api.jsonata4java.expressions.utils.Constants;
import com.api.jsonata4java.expressions.utils.FunctionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;

/**
 * From http://docs.jsonata.org/string-functions.html:
 * 
 * $pad(str, width [, char])
 * 
 * Returns a copy of the string str with extra padding, if necessary, so that
 * its total number of characters is at least the absolute value of the width
 * parameter. If width is a positive number, then the string is padded to the
 * right; if negative, it is padded to the left. The optional char argument
 * specifies the padding character(s) to use. If not specified, it defaults to
 * the space character.
 * 
 * Examples
 * 
 * $pad("foo", 5)=="foo " $pad("foo", -5)==" foo" $pad("foo", -5, "#") ==
 * "##foo" $formatBase(35, 2) ~&GT; $pad(-8, '0')=="00100011"
 *
 */
public class PadFunction extends FunctionBase implements Function {

	public static String ERR_BAD_CONTEXT = String.format(Constants.ERR_MSG_BAD_CONTEXT, Constants.FUNCTION_PAD);
	public static String ERR_ARG1BADTYPE = String.format(Constants.ERR_MSG_ARG1_BAD_TYPE, Constants.FUNCTION_PAD);
	public static String ERR_ARG2BADTYPE = String.format(Constants.ERR_MSG_ARG2_BAD_TYPE, Constants.FUNCTION_PAD);
	public static String ERR_ARG3BADTYPE = String.format(Constants.ERR_MSG_ARG3_BAD_TYPE, Constants.FUNCTION_PAD);
	public static String ERR_ARG4BADTYPE = String.format(Constants.ERR_MSG_ARG4_BAD_TYPE, Constants.FUNCTION_PAD);

	public JsonNode invoke(ExpressionsVisitor expressionVisitor, Function_callContext ctx) {
		// Create the variable to return
		JsonNode result = null;

		// Retrieve the number of arguments
		JsonNode argString = JsonNodeFactory.instance.nullNode();
		boolean useContext = FunctionUtils.useContextVariable(ctx, getSignature());
		int argCount = getArgumentCount(ctx);
		if (useContext) {
			argString = FunctionUtils.getContextVariable(expressionVisitor);
			argCount++;
		}

		// Make sure that we have the right number of arguments
		if (argCount == 2 || argCount == 3) {
			if (!useContext) {
				argString = FunctionUtils.getValuesListExpression(expressionVisitor, ctx, 0);
			}
			final JsonNode argWidth = FunctionUtils.getValuesListExpression(expressionVisitor, ctx, useContext ? 0 : 1);

			// Make sure that the first argument is a string
			if (argString != null) {
				if (argString.isTextual()) {
					// Read the string from the argument
					final String str = argString.textValue();

					// Now read the width argument
					int width = 0;
					if (argWidth != null && argWidth.isNumber()) {
						width = argWidth.asInt();
					} else {
						throw new EvaluateRuntimeException(ERR_ARG2BADTYPE);
					}

					// Check to see if we have an optional padding character and read
					// it if we do
					String padStr = " ";
					if (argCount == 3) {
						final JsonNode argChar = FunctionUtils.getValuesListExpression(expressionVisitor, ctx,
								useContext ? 1 : 2);
						if (argChar != null && argChar.isTextual()) {
							padStr = argChar.asText();
						} else {
							throw new EvaluateRuntimeException(ERR_ARG3BADTYPE);
						}
					}

					// Generate the result.. padding to the left or right depending
					// on the width argument
					if (width < 0) {
						result = new TextNode(StringUtils.leftPad(str, -width, padStr));
					} else {
						result = new TextNode(StringUtils.rightPad(str, width, padStr));
					}
				} else {
					throw new EvaluateRuntimeException(ERR_ARG1BADTYPE);
				}
			}
		} else {
			throw new EvaluateRuntimeException(
					argCount == 0 ? ERR_ARG1BADTYPE : argCount == 1 ? ERR_ARG1BADTYPE : ERR_ARG4BADTYPE);
		}

		return result;
	}

	@Override
	public String getSignature() {
		// accepts a string (or context variable), a number, an optional string, returns
		// a string
		return "<s-ns?:s>";
	}
}
