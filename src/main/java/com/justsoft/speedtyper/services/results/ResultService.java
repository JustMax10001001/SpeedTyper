package com.justsoft.speedtyper.services.results;

import com.justsoft.speedtyper.model.entities.TypingResult;

import java.util.List;

public abstract class ResultService {

    public abstract List<TypingResult> getAllResults();

    public abstract TypingResult saveResult(TypingResult result);

    public abstract TypingResult getResultById(int id);

    public static ResultService getInstance() {
        return ResultServiceImpl.getInstance();
    }
}