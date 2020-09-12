package com.justsoft.speedtyper.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Stats {

    public static <T> double calculateMedian(Collection<T> elements, Function<T, Double> valueGetter) {
        List<Double> values = elements.stream().map(valueGetter).sorted().collect(Collectors.toList());
        int n = values.size();
        if (n % 2 == 0) {
            return (values.get(n / 2 - 1) + values.get(n / 2)) / 2d;
        } else {
            return values.get(n / 2);
        }
    }
}
