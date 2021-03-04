package com.black.bim.function;

/**
 * @description：
 * 可能会抛出异常的函数
 * @author：8568
 */
@FunctionalInterface
public interface ExceptionFunction<T,R> {
    R apply(T t) throws Exception;
}
