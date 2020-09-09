package com.justsoft.speedtyper.services;

import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;

public class SessionResultMapService extends AbstractBaseModelMapService<TypingSessionResult> implements SessionResultsRepository {

    private static volatile SessionResultMapService instance;

    private SessionResultMapService() { }

    public static SessionResultMapService getInstance() {
        if (instance == null) {
            synchronized (SessionResultMapService.class) {
                if (instance == null)
                    instance = new SessionResultMapService();
            }
        }
        return instance;
    }

    @Override
    public TypingSessionResult save(TypingSessionResult value) {
        return super.save(value);
    }

    @Override
    public TypingSessionResult delete(TypingSessionResult value) {
        return super.delete(value);
    }
}
