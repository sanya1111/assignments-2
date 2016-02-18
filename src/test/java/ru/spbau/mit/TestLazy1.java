package ru.spbau.mit;

import org.junit.Test;

import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestLazy1 {

    @Test
    public void testSimple() {
        TestLazyCommon.check(LazyFactory::createLazy1, Executors.newFixedThreadPool(1), 0, new Object());
    }

    @Test
    public void testNull() {
        TestLazyCommon.check(LazyFactory::createLazy1, Executors.newFixedThreadPool(1), 0, null);
    }

    @Test
    public void testLazy() {
        TestLazyCommon.checkLazy(LazyFactory::createLazy1);
    }

}
