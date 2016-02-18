package ru.spbau.mit;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;

public class TestLazyCommon {

    private static final class LazyChecker<T> implements Supplier<T> {
        private boolean valid = false;

        @Override
        public T get() {
            if (!valid) {
                fail();
            }
            valid = false;
            return null;
        }

        public void setValid() {
            valid = true;
        }
    }

    private static final class LazySameChecker implements Supplier<Object> {
        private int in = 0;
        private int role;
        private Object retValue;

        @Override
        public synchronized Object get() {
            if (role == 0 && in > 0) {
                fail();
            }
            in++;
            if (role <= 1 || in % 10 == 0) {
                return retValue;
            }
            return null;
        }

        LazySameChecker(int role, Object retValue) {
            this.role = role;
            this.retValue = retValue;
        }
    }

    public static <T> void checkLazy(Function<Supplier<T>, Lazy<T>> lazyMaker) {
        TestLazyCommon.LazyChecker<T> checker = new TestLazyCommon.LazyChecker<>();
        Lazy<T> lazy = lazyMaker.apply(checker);
        checker.setValid();
        assertEquals(lazy.get(), null);
    }

    private static final int NRVALUE = 10000;

    private static <T> Set<Object> putTasksToExecutorServiceAndCalculate(final Lazy<T> lazy, ExecutorService service) {
        final Set<Object> set = new HashSet<>();
        final List<Future<T>> futures = new ArrayList<Future<T>>();
        for (int i = 0; i < NRVALUE; i++) {
            futures.add(service.submit(() -> lazy.get()));
        }
        futures.forEach(f -> {
            try {
                set.add(lazy.get());
            } catch (Exception e) {
            }
        });
        return set;
    }

    public static <T> void check(Function<Supplier<Object>, Lazy<T>> lazyMaker, ExecutorService service, int role, Object value) {
        LazySameChecker checker = new LazySameChecker(role, value);
        final Lazy<T> lazy = lazyMaker.apply(checker);
        Set<Object> set = putTasksToExecutorServiceAndCalculate(lazy, service);
        Assert.assertEquals(set.size(), 1);
        if (role < 2)
            assert (value == set.iterator().next());
    }


}
