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
import static com.api.jsonata4java.expressions.utils.Constants.ERR_MSG_BAD_CONTEXT;
import static com.api.jsonata4java.text.expressions.utils.Utils.preEvalTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.api.jsonata4java.expressions.functions.TypedFunction;

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
public class JavaFunction2ArgTests
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
			{ "$strFmt('%s rules','Jsonata')", "\"Jsonata rules\"", null },
			{ "'%s rules'~>$strFmt('Java')", "\"Java rules\"", null },
			{ "{}~>$strFmt('Java')", null, String.format(ERR_MSG_BAD_CONTEXT, "$strFmt") },

			{ "$mapOf('k','v')", "{\"k\":[\"v\"]}}", null },

		});
	}

	@Test
	public void runTest() throws Exception {
		preEvalTest(expression,
			e -> {
				e.assign("strFmt",
					define(this::stringFormat, String.class, Object.class, String.class));
				e.assign("mapOf",
					define(this::mapOf, String.class, String.class, Map.class));
			},
			expectedResultJsonString,
			expectedRuntimeExceptionMessage,
			null);
	}

	private String stringFormat(String base, Object arg) {
		return base == null ? null : String.format(base, arg);
	}

	private Map<String,Object> mapOf(String key, String value) {
		return Collections.singletonMap(key,Arrays.asList(value));
	}
}