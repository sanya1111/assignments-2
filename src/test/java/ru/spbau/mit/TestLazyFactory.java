package ru.spbau.mit;

import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

public class TestLazyFactory {

    public void testSimple(Function<Supplier<Object>, Lazy<Object>> factory, int threadsNum, int testMode) {
        TestLazyCommonUtils.check(factory, threadsNum, testMode, new Object());
    }

    public void testNull(Function<Supplier<Object>, Lazy<Object>> factory, int threadsNum, int testMode) {
        TestLazyCommonUtils.check(LazyFactory::createLazySingleThread, threadsNum, testMode, null);
    }

    public void testLazy(Function<Supplier<Object>, Lazy<Object>> factory) {
        TestLazyCommonUtils.checkLazy(factory);
    }

    public void singleThreadTest(Function<Supplier<Object>, Lazy<Object>> factory) {
        final int thread = 1;
        final int testMode = 0;

        testSimple(factory, thread, testMode);
        testNull(factory, thread, testMode);
        testLazy(factory);
    }

    private static final int MULTITHREAD_TEST_THREADS = 100;

    public void multiThreadTest(Function<Supplier<Object>, Lazy<Object>> factory, int testMode) {

        testSimple(factory, MULTITHREAD_TEST_THREADS, testMode);
        testNull(factory, MULTITHREAD_TEST_THREADS, testMode);
        testLazy(factory);
    }

    @Test
    public void testLazySingleThread() {
        singleThreadTest(LazyFactory::createLazySingleThread);
    }

    @Test
    public void testLazyMultiThreadSync() {
        final int testMode = 0;

        multiThreadTest(LazyFactory::createLazyMultiThreadSync, testMode);
    }

    @Test
    public void testLazyMultiThreadWaitFree() {
        final int testModeCommon = 1;
        final int testModeDiff = 2;

        multiThreadTest(LazyFactory::createLazyMultiThreadWaitFree, testModeCommon);
        TestLazyCommonUtils.check(LazyFactory::createLazyMultiThreadWaitFree,
                MULTITHREAD_TEST_THREADS, testModeDiff, new Object());
    }
}
