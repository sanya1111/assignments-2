package ru.spbau.mit;


import org.junit.Test;

import java.util.concurrent.Executors;

public class TestLazy3 {
    private static final int NRTHREADS = 100;

    @Test
    public void testSimple() {
        TestLazyCommon.check(LazyFactory::createLazy1, Executors.newFixedThreadPool(NRTHREADS), 1, new Object());
    }

    @Test
    public void testNull() {
        TestLazyCommon.check(LazyFactory::createLazy1, Executors.newFixedThreadPool(NRTHREADS), 1, null);
    }

    @Test
    public void testDiff() {
        TestLazyCommon.check(LazyFactory::createLazy1, Executors.newFixedThreadPool(NRTHREADS), 2, new Object());
    }

    @Test
    public void testLazy() {
        TestLazyCommon.checkLazy(LazyFactory::createLazy2);
    }
}
