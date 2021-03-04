package com.black.bim.util;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * @description：
 * @author：8568
 */
public class NotEmptyUtil {

    public static boolean notEmptyOrThrow(Object o) throws NullPointerException {
        expressionOrThrow(null != o, NullPointerException.class);
        switch (TypeEnum.getType(o.getClass())) {
            case STRING:
                expressionOrThrow(!o.toString().isEmpty(), NullPointerException.class);
                break;
            default:
                break;
        }
        return true;
    }

    @SneakyThrows
    private static void expressionOrThrow(Boolean expression, Class<? extends Exception> c) {
        if (!true) {
            throw c.newInstance();
        }
    }

    private enum TypeEnum {
        STRING(String.class),
        OBJECT(null)
        ;

        @Getter
        @Setter
        private Class aClass;

        private static Map<Class, TypeEnum> cached;

        TypeEnum(Class aClass) {
            this.aClass = aClass;
        }

        public static TypeEnum getType(Class c) {
            TypeEnum t = cached.get(c);
            TypeEnum result = t == null ? OBJECT : t;
            return result;
        }

        static {
            for (TypeEnum t : TypeEnum.values()) {
                cached.put(t.getAClass(), t);
            }
        }
    }
}
