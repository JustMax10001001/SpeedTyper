package com.justsoft.speedtyper.repositories.results;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.CRUDRepository;

public interface ResultRepository extends CRUDRepository<Integer, TypingResult> {

    ResultRepository instance = new ResultsRepositoryImpl();

    static ResultRepository getInstance() {
        return instance;
    }
}
