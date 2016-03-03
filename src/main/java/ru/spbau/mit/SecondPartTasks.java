package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
    private static final Random RANDOM = new Random();

    public static double piDividedBy4() {
        return DoubleStream
                .generate(() -> Math.pow(RANDOM.nextDouble() - RADIUS, 2)
                        + Math.pow(RANDOM.nextDouble() - RADIUS, 2))
                .limit(NTRYES)
                .map(x -> x <= Math.pow(RADIUS, 2) ? 1 : 0)
                .average()
                .getAsDouble();
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
