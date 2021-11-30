package com.justsoft.speedtyper.services.results;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.results.ResultRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

class ResultServiceImpl extends ResultService {
    private final Random idGenerator = new SecureRandom();

    private final ResultRepository resultRepository;

    private static volatile ResultService instance;
    private static final Object instanceLock = new Object();

    public static ResultService getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new ResultServiceImpl(ResultRepository.getInstance());
                }
            }
        }

        return instance;
    }

    private ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public List<TypingResult> getAllResults() {
        return this.resultRepository.getAll();
    }

    @Override
    public TypingResult saveResult(TypingResult result) {
        result = ensureId(result);

        return this.resultRepository.save(result);
    }

    @Override
    public TypingResult getResultById(int id) {
        return this.resultRepository.getById(id);
    }

    private TypingResult ensureId(TypingResult result) {
        if (result.id() != 0) {
            return result;
        }

        return result.withId(generateId());
    }

    private int generateId() {
        var id = this.idGenerator.nextInt(Integer.MAX_VALUE);

        if (id == 0) {
            return generateId();
        }

        return id;
    }
}
