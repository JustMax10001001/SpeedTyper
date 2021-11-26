package com.justsoft.speedtyper.services;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.TypingResultsRepository;

import java.util.List;

public interface ResultService {

    List<TypingResult> getAllResults();

    TypingResult saveResult(TypingResult result);

    TypingResult getResultById(int id);

    ResultService instance = new ResultServiceImpl(TypingResultsRepository.getInstance());

    static ResultService getInstance() {
        return instance;
    }
}