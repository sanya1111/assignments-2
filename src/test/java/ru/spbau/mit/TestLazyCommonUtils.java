package ru.spbau.mit;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;

public final class TestLazyCommonUtils {
    private TestLazyCommonUtils(){
    }

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
        private int iteration = 0;
        private int testMode;
        private Object retValue;

        private static final int MODULO = 10;

        private LazySameChecker(int testMode, Object retValue) {
            this.testMode = testMode;
            this.retValue = retValue;
        }

        @Override
        public synchronized Object get() {
            if (testMode == 0 && iteration > 0) {
                fail();
            }
            iteration++;
            if (testMode <= 1 || iteration % MODULO == 0) {
                return retValue;
            }
            return null;
        }
    }

    public static <T> void checkLazy(
                Function<Supplier<T>, Lazy<T>> lazyMaker) {
        TestLazyCommonUtils.LazyChecker<T> checker = new TestLazyCommonUtils.LazyChecker<>();
        Lazy<T> lazy = lazyMaker.apply(checker);
        checker.setValid();
        assertEquals(lazy.get(), null);
    }

    private static final int NRVALUE = 10000;

    private static Set<Object> putTasksToExecutorServiceAndCalculate(
            final Lazy<Object> lazy, ExecutorService service) {
        final Set<Object> set = new HashSet<>();
        final List<Future<Object>> futures = new ArrayList<Future<Object>>();
        for (int i = 0; i < NRVALUE; i++) {
            futures.add(service.submit(() -> lazy.get()));
        }
        futures.forEach(f -> {
            try {
                set.add(f.get());
            } catch (Exception e) {
            }
        });
        return set;
    }

    public static void check(
            Function<Supplier<Object>, Lazy<Object>> lazyMaker, int threadsNum, int testMode, Object value) {
        ExecutorService service = Executors.newFixedThreadPool(threadsNum);
        LazySameChecker checker = new LazySameChecker(testMode, value);
        final Lazy<Object> lazy = lazyMaker.apply(checker);
        Set<Object> set = putTasksToExecutorServiceAndCalculate(lazy, service);
        Assert.assertEquals(set.size(), 1);
        if (testMode < 2) {
            assert (value == set.iterator().next());
        }
    }


}
