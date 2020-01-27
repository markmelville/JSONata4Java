package com.api.jsonata4java.expressions.functions;

import java.util.Collection;
import java.util.List;

/**
 * Abstracts a function of many possible input arguments to 1 interface. In addition to an apply
 * method to invoke the function, it exposes information about the arg types and return type.
 *
 * Implementations choose a type of Object to use as a "medium" on the apply method, and are responsible to
 * convert between the functions typed args and the selected medium.
 */
public interface TypedFunction<A>
{
    A apply(List<A> input);

    Class<?> getReturnType();

    Collection<Class<?>> getArgTypes();

    public class InvalidType extends RuntimeException {

        private final byte failedArg;

        public InvalidType(byte failedArg)
        {
            this.failedArg = failedArg;
        }

        byte getFailedArg() {
            return failedArg;
        }
    }

}
