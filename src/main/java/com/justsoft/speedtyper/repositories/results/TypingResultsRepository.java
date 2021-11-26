package com.justsoft.speedtyper.repositories.results;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.CRUDRepository;

public interface TypingResultsRepository extends CRUDRepository<Integer, TypingResult> {

    TypingResultsRepository instance = new TypingResultsRepositoryImpl();

    static TypingResultsRepository getInstance() {
        return instance;
    }
}
