package com.xmfunny.funnydb.flink.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 数据类型转换工具类
 */
public class DataTypeUtils {
    public static Float toFloat(final Object value, final String fieldName) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof String) {
            return Float.parseFloat((String) value);
        }

        throw new IllegalTypeConversionException("Cannot convert value [" + value + "] of type " + value.getClass() + " to Float for field " + fieldName);
    }

    public static Long toLong(final Object value, final String fieldName) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            return Long.parseLong((String) value);
        }

        throw new IllegalTypeConversionException("Cannot convert value [" + value + "] of type " + value.getClass() + " to Long for field " + fieldName);
    }

    public static Integer toInteger(final Object value, final String fieldName) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            try {
                return Math.toIntExact(((Number) value).longValue());
            } catch (ArithmeticException ae) {
                throw new IllegalTypeConversionException("Cannot convert value [" + value + "] of type " + value.getClass() + " to Integer for field " + fieldName
                        + " as it causes an arithmetic overflow (the value is too large, e.g.)", ae);
            }
        }

        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }

        throw new IllegalTypeConversionException("Cannot convert value [" + value + "] of type " + value.getClass() + " to Integer for field " + fieldName);
    }

    public static Double toDouble(final Object value, final String fieldName) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            return Double.parseDouble((String) value);
        }

        throw new IllegalTypeConversionException("Cannot convert value [" + value + "] of type " + value.getClass() + " to Double for field " + fieldName);
    }

    public static Boolean toBoolean(final Object value, final String fieldName) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            final String string = (String) value;
            if (string.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (string.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
        }

        throw new IllegalTypeConversionException("Cannot convert value [" + value + "] of type " + value.getClass() + " to Boolean for field " + fieldName);
    }

    public static String toString(final Object value) {
        return toString(value, StandardCharsets.UTF_8);
    }

    public static String toString(final Object value, final Charset charset) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        if (value instanceof byte[]) {
            return new String((byte[]) value, charset);
        }

        if (value instanceof Byte[]) {
            Byte[] src = (Byte[]) value;
            byte[] dest = new byte[src.length];
            for (int i = 0; i < src.length; i++) {
                dest[i] = src[i];
            }
            return new String(dest, charset);
        }
        if (value instanceof Object[]) {
            Object[] o = (Object[]) value;
            if (o.length > 0) {

                byte[] dest = new byte[o.length];
                for (int i = 0; i < o.length; i++) {
                    dest[i] = (byte) o[i];
                }
                return new String(dest, charset);
            } else {
                return ""; // Empty array = empty string
            }
        }

        return value.toString();
    }
}
