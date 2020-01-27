package com.api.jsonata4java;


import com.api.jsonata4java.expressions.EvaluateException;
import com.fasterxml.jackson.databind.JsonNode;

public interface Evaluatable
{
    /**
     * Generates a JSON output by applying a JSON input.
     *
     * @param rootContext JSON object to use as input
     * @return the evaluation result
     * @throws EvaluateException
     */
    JsonNode evaluate(JsonNode rootContext) throws EvaluateException;
}
