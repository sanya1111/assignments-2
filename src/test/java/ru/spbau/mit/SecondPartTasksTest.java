package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static junitx.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SecondPartTasksTest {
    private static final String RES_DIR = "src/test/resources";

    private static final List<String> getFiles() {
        try {
            return Files
                    .walk(Paths.get(RES_DIR))
                    .map(x -> x.toString())
                    .filter(x -> !x.equals(RES_DIR))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private static final List<String> cutAllFilesSorted(List<String> files) {
        return files.stream()
                .map(x -> Paths.get(x).getFileName().toString())
                .sorted()
                .collect(Collectors.toList());
    }

    @Test
    public void testFindQuotes() {
        final List<String> files = getFiles();
        assertEquals(Arrays.asList("Hello_world"),
                cutAllFilesSorted(SecondPartTasks.findQuotes(files, "random")));
        assertEquals(Arrays.asList("Day that never comes"),
                cutAllFilesSorted(SecondPartTasks.findQuotes(files, "yeaahh")));
        assertEquals(Arrays.asList("Day that never comes", "Hello_world"),
                cutAllFilesSorted(SecondPartTasks.findQuotes(files, "word")));
    }

    private static final double EPS = 0.01;

    @Test
    public void testPiDividedBy4() {
        assertEquals(Math.PI / 4, SecondPartTasks.piDividedBy4(), EPS);
    }

    private static final List<String> PUSHKIN = Arrays.asList("aaaaaaaaaaaaaaaaaa", "bbbbbbbbbbbbbbbb", "cccccc");
    private static final List<String> LERMONTOV = Arrays.asList("a", "b", "c");
    private static final List<String> TWO_PAC = Arrays.asList("f*ck", "yeah",
            "biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii***");

    private static final Map<String, List<String>> MAP_AUTHOR_RUSSIAN = new HashMap<String, List<String>>() {
        {
            put("PUSHKIN", PUSHKIN);
            put("LERMONTOV", LERMONTOV);
        }
    };

    private static final Map<String, List<String>> MAP_AUTHOR_ALL = new HashMap<String, List<String>>() {
        {
            put("PUSHKIN", PUSHKIN);
            put("LERMONTOV", LERMONTOV);
            put("2Pac", TWO_PAC);
        }
    };

    @Test
    public void testFindPrinter() {
        assertEquals("PUSHKIN", SecondPartTasks.findPrinter(MAP_AUTHOR_RUSSIAN));
        assertEquals("2Pac", SecondPartTasks.findPrinter(MAP_AUTHOR_ALL));
    }

    private static final Map<String, Integer> PYTEROCHKA = new HashMap<String, Integer>() {
        {
            put("SYR", 100);
            put("MYASO", 100500);
            put("VODKO", ((int) 3L));
        }
    };

    private static final Map<String, Integer> MAGNIT = new HashMap<String, Integer>() {
        {
            put("SYR", 5);
            put("MYASO", 100500);
            put("VODKO", ((int) 10L));
            put("VINE PREMIUM", ((int) 1L));
        }
    };

    private static final Map<String, Integer> PRODUCTY_PLUS_MINUS = new HashMap<String, Integer>() {
        {
            put("OTSTOY", 100500);
        }
    };

    private static final List<Map<String, Integer>> PREMIUM_LIST = Arrays.asList(PYTEROCHKA, MAGNIT);
    private static final List<Map<String, Integer>> CHEAP_LIST = Arrays.asList(PYTEROCHKA, MAGNIT, PRODUCTY_PLUS_MINUS);


    @Test
    public void testCalculateGlobalOrder() {
        Assert.assertEquals(new HashMap<String, Integer>() {
            {
                put("OTSTOY", 100500);
                put("VINE PREMIUM", ((int) 1L));
                put("VODKO", ((int) 13L));
                put("MYASO", 201000);
                put("SYR", 105);
            }
        }, SecondPartTasks.calculateGlobalOrder(CHEAP_LIST));

        Assert.assertEquals(new HashMap<String, Integer>() {
            {
                put("VINE PREMIUM", ((int) 1L));
                put("VODKO", ((int) 13L));
                put("MYASO", 201000);
                put("SYR", 105);
            }
        }, SecondPartTasks.calculateGlobalOrder(PREMIUM_LIST));
    }


}