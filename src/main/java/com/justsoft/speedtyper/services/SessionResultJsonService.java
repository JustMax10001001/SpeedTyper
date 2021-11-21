package com.justsoft.speedtyper.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.justsoft.speedtyper.model.entities.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.util.concurrent.DaemonThreadFactory;
import com.justsoft.speedtyper.util.typeadapters.LocalDateTimeAdapter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class SessionResultJsonService implements SessionResultsRepository {

    private final Map<Integer, TypingSessionResult> resultMap = new ConcurrentHashMap<>();
    private final Gson gson;

    @Override
    public List<TypingSessionResult> getAll() {
        checkForIoAndWait();
        return new ArrayList<>(resultMap.values());
    }

    @Override
    public TypingSessionResult save(TypingSessionResult value) {
        checkForIoAndWait();
        if (value.id() == 0) {
            int maxKey = resultMap.keySet().stream().max(Comparator.comparingInt(i -> i)).orElse(1);
            value = value.withId(maxKey + 1);
        }
        resultMap.put(value.id(), value);
        executeIoInBackground(this::saveSessionResults);
        return value;
    }

    @Override
    public TypingSessionResult getById(Integer integer) {
        checkForIoAndWait();
        return resultMap.get(integer);
    }

    @Override
    public TypingSessionResult delete(TypingSessionResult value) {
        checkForIoAndWait();
        TypingSessionResult removed = resultMap.remove(value.id());
        executeIoInBackground(this::saveSessionResults);
        return removed;
    }

    @Override
    public TypingSessionResult deleteById(Integer integer) {
        checkForIoAndWait();
        TypingSessionResult removed = resultMap.remove(integer);
        executeIoInBackground(this::saveSessionResults);
        return removed;
    }

    /* SINGLETON */

    private static volatile SessionResultJsonService instance;

    private SessionResultJsonService() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT);
        this.gson = gsonBuilder.create();
        executeIoInBackground(this::loadSessionResults);
    }

    public static SessionResultJsonService getInstance() {
        if (instance == null) {
            synchronized (SessionResultJsonService.class) {
                if (instance == null)
                    instance = new SessionResultJsonService();
            }
        }
        return instance;
    }

    /* PERSISTENCE */

    private final ExecutorService backgroundIoExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
    private Future<?> backgroundExecutorFuture = null;

    private void loadSessionResults() {
        Type resultType = new TypeToken<Set<TypingSessionResult>>() {
        }.getType();
        try (FileInputStream stream = new FileInputStream("results.json")) {
            Set<TypingSessionResult> results = gson.fromJson(new InputStreamReader(stream), resultType);
            if (results == null) {
                results = new HashSet<>();
            }

            results.forEach(result -> resultMap.put(result.id(), result));
        } catch (FileNotFoundException e) {
            try {
                if (!new File("results.json").createNewFile()) {
                    throw new IOException("Create file returned false!");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeIoInBackground(Runnable runnable) {
        backgroundExecutorFuture = backgroundIoExecutor.submit(runnable);
    }

    private void saveSessionResults() {
        try (FileWriter fileWriter = new FileWriter("results.json")) {
            gson.toJson(resultMap.values(), fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkForIoAndWait() {
        if (isIoBusy())
            waitForIo();
    }

    private void waitForIo() {
        try {
            backgroundExecutorFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean isIoBusy() {
        return backgroundExecutorFuture == null || backgroundExecutorFuture.isDone();
    }
}
