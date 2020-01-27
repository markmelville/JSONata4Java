package com.api.jsonata4java.expressions.functions;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.api.jsonata4java.expressions.EvaluateRuntimeException;
import com.api.jsonata4java.expressions.utils.ObjectUtils;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A TypedFunction that exposes its arguments as, and returns, a JsonNode. This class is responsible
 * to convert between a JsonNode and the types of the underlying functions.
 */
public class JsonNodeTypedFunction<T,U,R> implements TypedFunction<JsonNode>
{
    private final BiFunction<T, U, R> function;
    private final Class<T> arg1Type;
    private final Class<U> arg2Type;
    private final Class<R> returnType;
    private final List<Class<?>> argTypes;

    private JsonNodeTypedFunction(BiFunction<T, U, R> function,
                                  Class<T> arg1Type,
                                  Class<U> arg2Type,
                                  Class<R> returnType) {

        this.function = function;
        this.arg1Type = arg1Type;
        this.arg2Type = arg2Type;
        this.returnType = returnType;
        this.argTypes = Stream.of(arg1Type, arg2Type)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public JsonNode apply(List<JsonNode> input) {
        int argCount = argTypes.size();
        int inputSize = input.size();
        if (inputSize != argCount) {
            throw new EvaluateRuntimeException("args counts do not match");
        }
        T arg1 = argCount > 0 ? convertArg(input.get(0), arg1Type, 0) : null;
        U arg2 = argCount > 1 ? convertArg(input.get(1), arg2Type, 1) : null;

        try {
            R returnValue = function.apply(arg1, arg2);
            return ObjectUtils.convertObjectToJsonNode(returnValue);
        }
        catch (ClassCastException ex) {
            throw new InvalidType((byte)-1);
        }
    }

    @Override
    public Class<?> getReturnType()
    {
        return returnType;
    }

    @Override
    public Collection<Class<?>> getArgTypes()
    {
        return argTypes;
    }

    private <I> I convertArg(JsonNode node, Class<I> type, int argNum) {
        try {
            return type == null ? null : ObjectUtils.convertJsonNodeToObject(node, type);
        }
        catch (ClassCastException ex) {
            throw new InvalidType((byte)argNum);
        }
    }

    /**
     * Creates a TypedFunction that takes 0 arguments, as defined by a Supplier<T>.
     *
     * @param supplier the Supplier to invoke
     * @param returnType the class of the function's return value
     * @param <T> the type of the return value
     * @return a TypedFunction
     */
    public static <T> TypedFunction<JsonNode> define(Supplier<T> supplier, Class<T> returnType) {
        return new JsonNodeTypedFunction<Void,Void,T>((a, b) -> supplier.get(), null, null, returnType);
    }

    /**
     * Creates a TypedFunction that takes 1 argument, as defined by a java.util.function.Function<T,R>.
     *
     * @param function the java.util.Function to invoke
     * @param arg1Type the class of the function's 1st argument
     * @param returnType the class of the function's return value
     * @param <T> the type of the 1st argument
     * @param <R> the type of the return value
     * @return a TypedFunction
     */
    public static <T, R> TypedFunction<JsonNode> define(java.util.function.Function<T, R> function,
                                       Class<T> arg1Type,
                                       Class<R> returnType) {
        return new JsonNodeTypedFunction<T,Void,R>((t, u) -> function.apply(t), arg1Type, null, returnType);
    }


    /**
     * Creates a TypedFunction that takes 2 arguments, as defined by a BiFunction<T,U,R>.
     *
     * @param function the BiFunction to invoke
     * @param arg1Type the class of the function's 1st argument
     * @param arg2Type the class of the function's 2nd argument
     * @param returnType the class of the function's return value
     * @param <T> the type of the 1st argument
     * @param <U> the type of the 2nd argument
     * @param <R> the type of the return value
     * @return a TypedFunction
     */
    public static <T, U, R> TypedFunction<JsonNode> define(BiFunction<T, U, R> function,
                                          Class<T> arg1Type,
                                          Class<U> arg2Type,
                                          Class<R> returnType) {
        return new JsonNodeTypedFunction<>(function, arg1Type, arg2Type, returnType);
    }
}
