package com.justsoft.speedtyper.services;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.TypingResultsRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

class ResultServiceImpl implements ResultService {
    private final Random idGenerator = new SecureRandom();

    private final TypingResultsRepository resultsRepository;

    public ResultServiceImpl(TypingResultsRepository resultsRepository) {
        this.resultsRepository = resultsRepository;
    }

    @Override
    public List<TypingResult> getAllResults() {
        return this.resultsRepository.getAll();
    }

    @Override
    public TypingResult saveResult(TypingResult result) {
        result = ensureId(result);

        return this.resultsRepository.save(result);
    }

    @Override
    public TypingResult getResultById(int id) {
        return this.resultsRepository.getById(id);
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
