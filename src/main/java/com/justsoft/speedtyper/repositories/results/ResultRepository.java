package com.justsoft.speedtyper.repositories.results;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.CRUDRepository;

public abstract class ResultRepository implements CRUDRepository<Integer, TypingResult> {

    public static ResultRepository getInstance() {
        return ResultsRepositoryImpl.getInstance();
    }
}
