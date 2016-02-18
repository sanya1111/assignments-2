package ru.spbau.mit;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

public class LazyFactory {
    private static final Object NONE = new Object();

    private static class LazySimple<T> implements Lazy<T> {
        private Object result = NONE;
        private Supplier<? extends T> function;

        LazySimple(final Supplier<? extends T> function) {
            this.function = function;
        }

        @Override
        public T get() {
            if (result == NONE) {
                result = function.get();
                function = null;
            }
            return (T) result;
        }
    }

    private static class LazyMultiThreadSync<T> implements Lazy<T> {
        private volatile Object result = NONE;
        private Supplier<? extends T> function;

        LazyMultiThreadSync(Supplier<? extends T> function) {
            this.function = function;
        }

        @Override
        public T get() {
            if (result == NONE) {
                synchronized (this) {
                    if (result == NONE) {
                        result = function.get();
                        function = null;
                    }
                }
            }
            return (T) result;
        }
    }

    private static class LazyMultiThreadWaitFree<T> implements Lazy<T> {
        private static final AtomicReferenceFieldUpdater<LazyMultiThreadWaitFree, Object> UPDATER =
                AtomicReferenceFieldUpdater.newUpdater(LazyMultiThreadWaitFree.class, Object.class, "result");

        private Supplier<? extends T> function;
        private volatile Object result = NONE;

        LazyMultiThreadWaitFree(Supplier<? extends T> function) {
            this.function = function;
        }

        @Override
        public T get() {
            if (result == NONE) {
                Supplier<? extends T> local = function;
                if (local != null) {
                    if (UPDATER.compareAndSet(this, NONE, local.get())) {
                        function = null;
                    }
                }
            }
            return (T) result;
        }
    }

    public static <T> Lazy<T> createLazy1(final Supplier<T> calc) {
        return new LazySimple<T>(calc);
    }

    public static <T> Lazy<T> createLazy2(final Supplier<T> calc) {
        return new LazyMultiThreadSync<T>(calc);
    }

    public static <T> Lazy<T> createLazy3InnerClass(final Supplier<T> calc) {
        return new LazyMultiThreadWaitFree<T>(calc);
    }
}
