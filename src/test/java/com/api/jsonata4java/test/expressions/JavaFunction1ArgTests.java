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

import static com.api.jsonata4java.expressions.functions.JsonNodeTypedFunction.define;
import static com.api.jsonata4java.expressions.utils.Constants.ERR_MSG_ARG1_BAD_TYPE;
import static com.api.jsonata4java.expressions.utils.Constants.ERR_MSG_RETURN_BAD_TYPE;
import static com.api.jsonata4java.expressions.utils.Constants.ERR_MSG_TOO_MANY_ARGS;
import static com.api.jsonata4java.text.expressions.utils.Utils.preEvalTest;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * For simplicity, these tests don't rely on $state/$event/$instance access;
 * instead providing the "input" inlined with the expression itself (e.g. ["a",
 * "b"][0]=="a"). Separate test cases verify that variable access works as
 * expected.
 */
@RunWith(Parameterized.class)
public class JavaFunction1ArgTests
{

	@Parameter(0)
	public String expression;

	@Parameter(1)
	public String expectedResultJsonString;

	@Parameter(2)
	public String expectedRuntimeExceptionMessage;

	@Parameters(name = "{index}: {0} -> {1} ({2})")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ "$consume('Foo')", null, null },
			{ "$consume()", null, String.format(ERR_MSG_ARG1_BAD_TYPE, "$consume") },
			{ "$consume('Foo',23)", null, String.format(ERR_MSG_TOO_MANY_ARGS, "$consume") },
			{ "$consume(123)", null, String.format(ERR_MSG_ARG1_BAD_TYPE, "$consume") },

			{ "$getBytes('Foo')", "[70,111,111]", null },

			{ "$firstStringValue({'k':'v'})", "\"v\"", null },
			{ "$firstStringValue({'data':[1,2,3]})", null, String.format(ERR_MSG_RETURN_BAD_TYPE, "$firstStringValue") },

			// based on the input, the output can dynamically use the value of an Object return value
			{ "$firstValue({'data':0})", "0", null },
			{ "$firstValue({'data':'test'})", "\"test\"", null },
			{ "$firstValue({'data':[1,2,3]})[0]", "1", null },
			{ "$firstValue({'data':{'k':'v'}}).k", "\"v\"", null },

			{ "$firstTwo(['one','two','three'])", "[\"one\",\"two\"]", null },
			{ "$firstChars(['one','two','three'])", "[\"o\",\"t\",\"t\"]", null },
			// here the list are typed to string but in can adapt
			{ "$firstTwo([1,2,3])", "[1,2]", null },
			// unless there is a String specific operation attempted...
			// these errors due to type-non-safety that are thrown in the implementation of a function
			// are indistinguishable from a successful execution but the return type doesn't match
			{ "$firstChars([1,2,3])", null, String.format(ERR_MSG_RETURN_BAD_TYPE, "$firstChars") },

			// dates
			{ "$inst('2020-01-01T12:00:00.000Z')", "1577880000", null },
			{ "$offset('2020-01-01T12:00:00.000-06:00')", "\"-06:00\"", null },

			// infinite loop when this is wired up.
			//{ "$hof(function(){1})", null, null}
		});
	}

	/**
	 * This test of 1-arg functions uses a variety of input types
	 * to ensure that any possible JSON input is correctly deserialized to an appropriate java type
	 * or throws an error accordingly
	 */
	@Test
	public void runTest() throws Exception {
		preEvalTest(expression,
			e -> {
				// a Consumer can be turned into a Function
				// by wrapping it in a lambda that returns any value
				e.assign("consume", define(str -> null, String.class, Void.class));
				e.assign("getBytes",
					define(this::getBytes, String.class, byte[].class));
				e.assign("firstStringValue",
					define(this::firstStringValue, Map.class, String.class));
				e.assign("firstValue",
					define(this::firstValue, Map.class, Object.class));
				e.assign("firstTwo",
					define(this::firstTwo, List.class, List.class));
				e.assign("firstChars",
					define(this::firstChars, List.class, List.class));
				e.assign("inst",
					define(this::inst, Instant.class, Long.class));
				e.assign("offset",
					define(this::offset, OffsetDateTime.class, ZoneOffset.class));
				//e.assign("hof",define(this::hof, Supplier.class, Long.class));
			},
			expectedResultJsonString,
			expectedRuntimeExceptionMessage,
			null);
	}

	private byte[] getBytes(String str) {
		return str.getBytes();
	}

	/**
	 * This one tests when the map value is not the same as the generic type (a string)
	 * that the invalid cast on the return type is caught and displayed correctly
	 */
	private String firstStringValue(Map<String,String> map) {
		return map.values().iterator().next();
	}

	/**
	 * but the above scenario can be avoided by using Object return type
	 * the result is able to be used dynamically in the expression
	 */
	private Object firstValue(Map<String,String> map) {
		return map.values().iterator().next();
	}

	private List<String> firstTwo(List<String> list) {
		return list.subList(0,2);
	}

	private List<String> firstChars(List<String> list) {
		return list.stream().map(s -> String.valueOf(s.charAt(0))).collect(Collectors.toList());
	}

	private long inst(Instant instant) {
		return instant.getEpochSecond();
	}

	private ZoneOffset offset(OffsetDateTime dateTime) {
		return dateTime.getOffset();
	}

	private long hof(Supplier<Long> supplier) {
		return supplier.get();
	}
}
