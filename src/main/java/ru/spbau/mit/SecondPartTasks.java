package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public final class SecondPartTasks {

    private SecondPartTasks() {
    }

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths.stream().filter(x -> {
            try {
                return Files
                        .lines(Paths.get(x))
                        .anyMatch(y -> y.contains(sequence));
            } catch (IOException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать,
    // какова вероятность попасть в мишень.
    private static final int NTRYES = 1000000;
    private static final double RADIUS = 0.5;
    private static final int ADDING_SIZE = 4;
    private static final int COUNTING_SIZE = 5;

    public static double piDividedBy4() {
        return new Random()
                .doubles(0, 1)
                .boxed().limit(NTRYES)
                .map(x -> new ArrayList<Double>(Arrays.asList(0.0, 0.0, 0.0, x)))
                .reduce(
                        new ArrayList<Double>(Arrays.asList(0.0, 0.0, 0.0)),
                        (x, y) -> {
                            if (y.size() == ADDING_SIZE) {
                                x.add(y.get(ADDING_SIZE - 1));
                            }
                            if (x.size() == COUNTING_SIZE) {
                                x.set(1, x.get(1) + 1);
                                double a = x.get(COUNTING_SIZE - 2) - RADIUS;
                                double b = x.get(COUNTING_SIZE - 1) - RADIUS;
                                if (a * a + b * b <= RADIUS * RADIUS) {
                                    x.set(0, x.get(0) + 1);
                                }
                                x.remove(COUNTING_SIZE - 1);
                                x.remove(COUNTING_SIZE - 2);
                            }
                            x.set(0, x.get(0) + y.get(0));
                            x.set(1, x.get(1) + y.get(1));
                            if (x.get(1) != 0) {
                                x.set(2, x.get(0) / x.get(1));
                            }
                            return x;
                        }
                ).get(2);
    }


    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions.entrySet().stream()
                .max(Comparator
                        .comparing(x -> x.getValue().stream()
                                .map(y -> y.length())
                                .reduce(0, (a, b) -> a + b)))
                .get().getKey();
    }

    // Вы крупный поставщик продуктов.
    // Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .flatMap(x -> x.entrySet().stream())
                .collect(Collectors
                        .groupingBy(x -> x.getKey(), Collectors
                                .summingInt(x -> x.getValue())));
    }
}
