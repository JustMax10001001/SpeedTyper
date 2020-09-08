package com.justsoft.speedtyper;

import com.justsoft.speedtyper.resources.Resources;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class Dictionary {

    private static final int DICTIONARY_CAPACITY = 3000;
    private final Object _lck = new Object();

    private File source;

    private ArrayList<String> dictionary;
    private Random random;

    public Dictionary(File source){
        this.source = source;
    }

    public String getRandomDictionaryWord() {
        String word;
        synchronized (_lck) {
            word = dictionary.get(random.nextInt(dictionary.size()));
        }
        return word;
    }

    public void load() throws URISyntaxException, IOException {
        synchronized (_lck) {
            random = new Random(System.currentTimeMillis());
            dictionary = new ArrayList<>(DICTIONARY_CAPACITY);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Resources.loadFile("dictionary.txt"))))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    dictionary.add(line);
                }
            }
        }
    }
}
