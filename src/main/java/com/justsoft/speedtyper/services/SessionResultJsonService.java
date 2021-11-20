package com.justsoft.speedtyper.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.util.concurrent.DaemonThreadFactory;
import com.justsoft.speedtyper.util.typeadapters.LocalDateTimeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class SessionResultJsonService implements SessionResultsRepository {

    private final Map<Integer, TypingSessionResult> resultMap = new HashMap<>();
    private final Gson gson;

    @Override
    public List<TypingSessionResult> getAll() {
        checkForIoAndWait();
        return new ArrayList<>(resultMap.values());
    }

    @Override
    public TypingSessionResult save(TypingSessionResult value) {
        checkForIoAndWait();
        if (value.getId() == null) {
            int maxKey = 0;
            for (int key : resultMap.keySet()) {
                maxKey = Math.max(maxKey, key);
            }
            value.setId(maxKey + 1);
        }
        resultMap.put(value.getId(), value);
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
        TypingSessionResult removed = resultMap.remove(value.getId());
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

            results.forEach(result -> resultMap.put(result.getId(), result));
        } catch (FileNotFoundException e) {
            try {
                new File("results.json").createNewFile();
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
