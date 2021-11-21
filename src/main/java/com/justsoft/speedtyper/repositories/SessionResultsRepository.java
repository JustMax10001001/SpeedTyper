package com.justsoft.speedtyper.repositories;

import com.justsoft.speedtyper.model.entities.TypingSessionResult;
import com.justsoft.speedtyper.services.SessionResultJsonService;

public interface SessionResultsRepository extends CRUDRepository<Integer, TypingSessionResult> {

    static SessionResultsRepository getPreferredInstance() {
        return SessionResultJsonService.getInstance();
    }
}
