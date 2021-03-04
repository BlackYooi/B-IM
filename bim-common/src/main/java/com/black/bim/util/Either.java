package com.black.bim.util;

import com.black.bim.function.ExceptionFunction;
import io.vavr.Tuple2;

import java.util.Optional;
import java.util.function.Function;

/**
 * @description：
 * 对两种结果进行包装
 * @author：8568
 */
public class Either<L, R> {
    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> Left(L value) {
        return new Either(value, null);
    }

    public static <L, R> Either<L, R> Right(R value) {
        return new Either(null, value);
    }

    public static <L, R, T> Either<Tuple2<T, L>, R> LeftTuple2(T param, L value) {
        Tuple2<T, L> t = new Tuple2<>(param, value);
        return new Either(t, null);
    }

    public static <L, R, T> Either<L, Tuple2<T, R>> RightTuple2(T param, R value) {
        Tuple2<T, R> t = new Tuple2<>(param, value);
        return new Either(null, t);
    }

    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        if (isRight()) {
            return Optional.of(mapper.apply(right));
        }
        return Optional.empty();
    }

    public static <T,R> Function<T,R> wrap(ExceptionFunction<T,R> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }


    public static <T,R> Function<T, Either> lift(ExceptionFunction<T,R> function) {
        return t -> {
            try {
                return Either.Right(function.apply(t));
            } catch (Exception ex) {
                return Either.LeftTuple2(t, ex);
            }
        };
    }

    public static <T,R> Function<T, Either> liftTuple2(ExceptionFunction<T,R> function) {
        return t -> {
            try {
                return Either.RightTuple2(t ,function.apply(t));
            } catch (Exception ex) {
                return Either.Left(ex);
            }
        };
    }

    @Override
    public String toString() {
        if (isLeft()) {
            return "Left(" + left + ")";
        }
        return "Right(" + right + ")";
    }
}
