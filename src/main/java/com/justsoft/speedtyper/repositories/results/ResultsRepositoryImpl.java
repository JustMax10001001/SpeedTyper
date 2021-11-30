package com.justsoft.speedtyper.repositories.results;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.ui.dialogs.ExceptionAlert;
import com.justsoft.speedtyper.util.concurrent.DaemonThreadFactory;
import com.justsoft.speedtyper.util.typeadapters.LocalDateTimeAdapter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.justsoft.speedtyper.util.Objects.notNull;

class ResultsRepositoryImpl extends ResultRepository {
    private final String RESULT_FILE = "results.json";

    private final Gson gson;

    private final Map<Integer, TypingResult> resultCache = new ConcurrentHashMap<>();
    private final Object cacheAccessMutex = new Object();
    private final ExecutorService backgroundIoExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());

    private static volatile ResultRepository instance;
    private static final Object instanceLock = new Object();

    public static ResultRepository getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new ResultsRepositoryImpl();
                }
            }
        }

        return instance;
    }

    private ResultsRepositoryImpl() {
        this.gson = createGson();

        submitIo(this::prepareResultCache);
    }

    @Override
    public List<TypingResult> getAll() {
        synchronized (this.cacheAccessMutex) {
            return new ArrayList<>(this.resultCache.values());
        }
    }

    @Override
    public TypingResult save(TypingResult result) {
        this.resultCache.put(result.id(), result);

        submitIo(this::flushResultCache);

        return result;
    }

    @Override
    public TypingResult getById(Integer id) {
        return this.resultCache.get(id);
    }

    @Override
    public TypingResult delete(TypingResult result) {
        return deleteById(result.id());
    }

    @Override
    public TypingResult deleteById(Integer id) {
        var removedResult = this.resultCache.remove(id);

        submitIo(this::flushResultCache);

        return removedResult;
    }

    private void prepareResultCache() {
        if (!new File(this.RESULT_FILE).exists()) {
            return;
        }

        synchronized (this.cacheAccessMutex) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(this.RESULT_FILE))) {
                Type resultSetType = new TypeToken<Set<TypingResult>>() {
                }.getType();

                var resultSet = notNull(
                        this.gson.fromJson(inputStreamReader, resultSetType),
                        new HashSet<TypingResult>()
                );

                resultSet.forEach(r -> this.resultCache.put(r.id(), r));
            } catch (FileNotFoundException e) {
                ExceptionAlert.show(null, "Result file does not exist!");
            } catch (IOException e) {
                ExceptionAlert.show(e, "Could not load result file!");
            }
        }
    }

    private void flushResultCache() {
        try (OutputStreamWriter resultStreamWriter = new OutputStreamWriter(new FileOutputStream(this.RESULT_FILE))) {
            this.gson.toJson(this.resultCache.values(), resultStreamWriter);
        } catch (IOException e) {
            ExceptionAlert.show(e, "Could not save result file!");
        }
    }

    private void submitIo(Runnable ioRunnable) {
        this.backgroundIoExecutor.submit(ioRunnable);
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateTimeAdapter())
                .registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
                .create();
    }
}
