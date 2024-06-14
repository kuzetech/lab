package com.xmfunny.funnydb.flink.util;

public class IllegalTypeConversionException extends RuntimeException {

    public IllegalTypeConversionException(final String message) {
        super(message);
    }

    public IllegalTypeConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
