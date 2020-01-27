package com.api.jsonata4java.expressions.utils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.scenario.effect.Offset;

public class ObjectUtils
{
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new MapType();
    private static final TypeReference<List<Object>> ARRAY_TYPE = new ArrayType();

    public static <T> JsonNode convertObjectToJsonNode(T obj)
    {
        if (obj == null) {
            return null;
        }
        if (obj instanceof JsonNode) {
            return (JsonNode)obj;
        }
        if (obj instanceof String) {
            return JsonNodeFactory.instance.textNode((String)obj);
        }
        if (obj instanceof Boolean) {
            return JsonNodeFactory.instance.booleanNode((Boolean)obj);
        }
        if (obj instanceof Long) {
            return JsonNodeFactory.instance.numberNode((Long)obj);
        }
        if (obj instanceof Integer) {
            return JsonNodeFactory.instance.numberNode(((Integer)obj).longValue());
        }
        if (obj instanceof Short) {
            return JsonNodeFactory.instance.numberNode(((Short)obj).longValue());
        }
        if (obj instanceof Byte) {
            return JsonNodeFactory.instance.numberNode(((Byte)obj).longValue());
        }
        if (obj instanceof Double) {
            return JsonNodeFactory.instance.numberNode((Double)obj);
        }
        if (obj instanceof Float) {
            try
            {
                // mappers parse numbers to double nodes, so it's the expected behavior
                // trying to create a DoubleNode manually from a float alters the precision.
                return mapper.readTree(obj.toString());
            }
            catch (IOException e)
            {
                // this fallback will create a FloatNode
                return JsonNodeFactory.instance.numberNode((Float) obj);
            }
        }
        if (obj instanceof Iterable) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (Object o : (Iterable<?>)obj)
            {
                arrayNode.add(convertObjectToJsonNode(o));
            }
            return arrayNode;
        }
        if (obj instanceof Map) {
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            for (Map.Entry e : ((Map<?,?>)obj).entrySet())
            {
                objectNode.set(e.getKey().toString(), convertObjectToJsonNode(e.getValue()));
            }
            return objectNode;
        }
        if (obj instanceof boolean[]) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (boolean b : (boolean[])obj)
            {
                arrayNode.add(JsonNodeFactory.instance.booleanNode(b));
            }
            return arrayNode;
        }
        if (obj instanceof byte[]) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (long num : (byte[])obj)
            {
                arrayNode.add(JsonNodeFactory.instance.numberNode(num));
            }
            return arrayNode;
        }
        if (obj instanceof short[]) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (long num : (short[])obj)
            {
                arrayNode.add(JsonNodeFactory.instance.numberNode(num));
            }
            return arrayNode;
        }
        if (obj instanceof int[]) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (long num : (int[])obj)
            {
                arrayNode.add(JsonNodeFactory.instance.numberNode(num));
            }
            return arrayNode;
        }
        if (obj instanceof long[]) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (long num : (long[])obj)
            {
                arrayNode.add(JsonNodeFactory.instance.numberNode(num));
            }
            return arrayNode;
        }
        if (obj instanceof double[]) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (double d : (double[])obj)
            {
                arrayNode.add(JsonNodeFactory.instance.numberNode(d));
            }
            return arrayNode;
        }
        // should catch char[] and float[]
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (int i = 0; i < length; i ++) {
                arrayNode.add(convertObjectToJsonNode(Array.get(obj, i)));
            }
            return arrayNode;
        }
        return JsonNodeFactory.instance.textNode(obj.toString());
    }

    public static <T> T convertJsonNodeToObject(JsonNode jsonNode, Class<T> type)
    {
        if (jsonNode == null) {
            return null;
        }

        if (jsonNode.isTextual()) {
            String text = jsonNode.asText();
            T date = tryParseTimeTypes(text, type);
            if (date != null) {
                return type.cast(date);
            }
            return type.cast(jsonNode.asText());
        }
        if (jsonNode.isObject())
        {
            return type.cast(mapper.convertValue(jsonNode, MAP_TYPE));
        }
        if (jsonNode.isArray())
        {
            return type.cast(mapper.convertValue(jsonNode, ARRAY_TYPE));
        }
        if (jsonNode.isBoolean()) {
            return type.cast(jsonNode.asBoolean());
        }
        if (jsonNode.isFloat() || jsonNode.isDouble()) {
            return type.cast(jsonNode.asDouble());
        }
        if (jsonNode.canConvertToLong()) {
            return type.cast(jsonNode.asLong());
        }
        if (jsonNode.canConvertToInt()) {
            return type.cast(jsonNode.asInt());
        }
        return null;
    }

    private static <T> T tryParseTimeTypes(String text, Class<T> type) {
        if (text.length() < 50) {
            if (Instant.class.isAssignableFrom(type) && text.endsWith("Z"))
            {
                try
                {
                    return type.cast(Instant.parse(text));
                }
                catch (Exception e)
                {
                }
            }
            if (OffsetDateTime.class.isAssignableFrom(type)) {
                try
                {
                    return type.cast(OffsetDateTime.parse(text));
                }
                catch (Exception e)
                {
                }
            }
            //LocalDate, LocalTime, Duration, LocalDateTime, ZonedDateTime
            //ZoneOffset,
        }
        return null;
    }

    private static class MapType extends TypeReference<Map<String, Object>>
    {

    }

    private static class ArrayType extends TypeReference<List<Object>>
    {

    }
}
