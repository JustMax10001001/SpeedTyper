package com.justsoft.speedtyper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Dictionary {

    private static final int DICTIONARY_CAPACITY = 3000;
    private final Object dictionaryLock = new Object();

    private final InputStream inputStream;

    private ArrayList<String> dictionary;
    private Random random;

    public Dictionary(InputStream source){
        this.inputStream = source;
    }

    public String getRandomDictionaryWord() {
        String word;
        synchronized (dictionaryLock) {
            word = dictionary.get(random.nextInt(dictionary.size()));
        }
        return word;
    }

    public void load() throws IOException {
        synchronized (dictionaryLock) {
            random = new Random(System.currentTimeMillis());
            dictionary = new ArrayList<>(DICTIONARY_CAPACITY);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    dictionary.add(line);
                }
            }
        }
    }
}
