package com.justsoft.speedtyper.repositories;

import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.services.SessionResultJsonService;
import com.justsoft.speedtyper.services.SessionResultMapService;

public interface SessionResultsRepository extends CRUDRepository<Integer, TypingSessionResult> {

    static SessionResultsRepository getPreferredInstance() {
        return SessionResultJsonService.getInstance();
    }
}
