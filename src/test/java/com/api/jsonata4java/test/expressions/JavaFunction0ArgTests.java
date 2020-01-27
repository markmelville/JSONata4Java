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
import static com.api.jsonata4java.expressions.utils.Constants.ERR_MSG_TOO_MANY_ARGS;
import static com.api.jsonata4java.text.expressions.utils.Utils.preEvalTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class JavaFunction0ArgTests
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
			{ "$noOp()", "", null },
			{ "$noOp(123)", null, String.format(ERR_MSG_TOO_MANY_ARGS, "$noOp") },

			{ "$getIds()", "[\"123\",\"456\"]", null },
			{ "$getIdsNTS()", "[\"123\",456]", null },
			{ "$getMap()", "{\"A\":123,\"B\":456}", null },
			{ "$uri()", "\"http://try.jsonata.org/\"", null },

			// primitives
			{ "$epochMillis()", "1579816822717", null },
			{ "$getNumber()", "8675309", null },
			{ "$getYear()", "2020", null },
			{ "$getAge()", "88", null },
			{ "$isTest()", "true", null },
			{ "$getDelimiter()", "\",\"", null },
			{ "$getPi()", "3.141592653589793", null },
			{ "$getThird()", "0.33333334", null },

			// null primitives
			{ "$nullLong()", null, null },

			// primitive arrays
			{ "$longArray()", "[1579816822717,1579816823717]", null },
			{ "$intArray()", "[123456,234567]", null },
			{ "$shortArray()", "[1985,1955,2015,1885]", null },
			{ "$byteArray()", "[4,8,15,16,23,42]", null },
			{ "$booleanArray()", "[true,false,true]", null },
			{ "$charArray()", "[\"R\",\"O\",\"U\",\"S\"]", null },
			{ "$floatArray()", "[1.1,1.2,1.3]", null },
			{ "$doubleArray()", "[3.34,3.81]", null },

			//dates
			{ "$inst()", "\"2020-01-23T22:00:22.717Z\"", null },
			{ "$offset()", "\"2020-01-23T22:00:22.717Z\"", null },

		});
	}

	/**
	 * This test of 0-arg functions uses a variety of return types
	 * to ensure that any possible java output can be converted to a corresponding JSON construct.
	 */
	@Test
	public void runTest() throws Exception {
		preEvalTest(expression,
			e -> {
				// a Runnable can be turned into a Supplier
				// by wrapping it in a lambda that returns any value
				e.assign("noOp", define(() -> null, Void.class));
				e.assign("getIds",define(this::getIds, List.class));
				// inline lambdas only able to use raw generic types, can return heterogeneous lists
				e.assign("getIdsNTS",define(() -> Arrays.asList("123",456), List.class));
				e.assign("getMap",define(this::getMap, Map.class));
				e.assign("uri",define(this::uri, URI.class));

				// primitives
				e.assign("epochMillis",define(this::epochMillis, Long.class));
				e.assign("getNumber",define(this::getNumber, Integer.class));
				e.assign("getNumber",define(this::getNumber, Integer.class));
				e.assign("getYear",define(this::getYear, Short.class));
				e.assign("getAge",define(this::getAge, Byte.class));
				e.assign("isTest",define(this::isTest, Boolean.class));
				e.assign("getDelimiter",define(this::getDelimiter, Character.class));
				e.assign("getPi",define(this::getPi, Double.class));
				e.assign("getThird",define(this::getThird, Float.class));

				// primitives using inline lambda functions
				// must use the Object variant of the type, and thus can return null
				e.assign("nullLong", define(() -> null, Long.class));
				e.assign("nullInt", define(() -> null, Integer.class));
				e.assign("nullShort", define(() -> null, Short.class));
				e.assign("nullByte", define(() -> null, Byte.class));
				e.assign("nullBoolean", define(() -> null, Boolean.class));
				e.assign("nullChar", define(() -> null, Character.class));
				e.assign("nullFloat", define(() -> null, Float.class));
				e.assign("nullDouble", define(() -> null, Double.class));

				// primitive arrays
				e.assign("longArray", define(this::longArray, long[].class));
				e.assign("intArray", define(this::intArray, int[].class));
				e.assign("shortArray", define(this::shortArray, short[].class));
				e.assign("byteArray", define(this::byteArray, byte[].class));
				e.assign("booleanArray", define(this::booleanArray, boolean[].class));
				e.assign("charArray", define(this::charArray, char[].class));
				e.assign("floatArray", define(this::floatArray, float[].class));
				e.assign("doubleArray", define(this::doubleArray, double[].class));

				// dates
				e.assign("inst",
					define(() -> Instant.ofEpochMilli(1579816822717L), Instant.class));
				e.assign("offset",
					define(() -> Instant.ofEpochMilli(1579816822717L).atOffset(ZoneOffset.ofHours(-6)), OffsetDateTime.class));
			},
			expectedResultJsonString,
			expectedRuntimeExceptionMessage,
			null);
	}

	private List<String> getIds()
	{
		return Arrays.asList("123","456");
	}

	private Map<String,Integer> getMap() {
		HashMap<String,Integer> map = new HashMap<>();
		map.put("A",123);
		map.put("B",456);
		return map;
	}

	private URI uri() {
		try
		{
			return new URI("http://try.jsonata.org/");
		}
		catch (URISyntaxException e)
		{
			return null;
		}
	}

	private long epochMillis() {
		// hard coded to allow the test to know the expected output
		// we're not testing the implementation
		return 1579816822717L;
	}

	private int getNumber() {
		return 8675309;
	}

	private short getYear() {
		return 2020;
	}

	private byte getAge() {
		return 88;
	}

	private boolean isTest() {
		return true;
	}

	private char getDelimiter() {
		return ',';
	}

	private double getPi() {
		return Math.PI;
	}

	private float getThird() {
		return 1.0f/3.0f;
	}

	private long[] longArray() {
		return new long[] {1579816822717L,1579816823717L};
	}
	private int[] intArray() {
		return new int[] {123456,234567};
	}
	private short[] shortArray() {
		return new short[] {1985,1955,2015,1885};
	}
	private byte[] byteArray() {
		return new byte[] {4,8,15,16,23,42};
	}
	private boolean[] booleanArray() {
		return new boolean[] {true,false,true};
	}
	private char[] charArray() {
		return new char[] {'R','O','U','S'};
	}
	private float[] floatArray() {
		return new float[] {1.1f,1.2f,1.3f};
	}
	private double[] doubleArray() {
		return new double[] {3.34,3.81};
	}
}
