package com.justsoft.speedtyper.repositories;

import com.justsoft.speedtyper.model.entities.TypingResult;

public interface TypingResultsRepository extends CRUDRepository<Integer, TypingResult> {

    TypingResultsRepository instance = new TypingResultsRepositoryImpl();

    static TypingResultsRepository getInstance() {
        return instance;
    }
}
