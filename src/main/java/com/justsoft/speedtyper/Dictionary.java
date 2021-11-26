package com.justsoft.speedtyper;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class Dictionary {

    private static final int DICTIONARY_CAPACITY = 3000;
    private final Object _lck = new Object();

    private final InputStream inputStream;

    private ArrayList<String> dictionary;
    private Random random;

    public Dictionary(InputStream source){
        this.inputStream = source;
    }

    public String getRandomDictionaryWord() {
        String word;
        synchronized (_lck) {
            word = dictionary.get(random.nextInt(dictionary.size()));
        }
        return word;
    }

    public void load() throws IOException {
        synchronized (_lck) {
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
