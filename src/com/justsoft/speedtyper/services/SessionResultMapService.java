package com.justsoft.speedtyper.services;

import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;

public class SessionResultMapService extends AbstractBaseModelMapService<TypingSessionResult> implements SessionResultsRepository {

    @Override
    public TypingSessionResult save(TypingSessionResult value) {
        return super.save(value);
    }

    @Override
    public TypingSessionResult delete(TypingSessionResult value) {
        return super.delete(value);
    }
}
