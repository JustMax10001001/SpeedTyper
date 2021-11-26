package com.justsoft.speedtyper.util;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class Loops {
    private Loops() { }

    public static void repeat(int times, IntConsumer toRepeat) {
        IntStream.range(0, times).forEach(toRepeat);
    }

    public static void repeat(int times, Runnable toRepeat) {
        IntStream.range(0, times).forEach(i -> toRepeat.run());
    }
}
