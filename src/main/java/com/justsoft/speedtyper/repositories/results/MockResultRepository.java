package com.justsoft.speedtyper.repositories.results;

import com.justsoft.speedtyper.model.entities.TypingResult;

import java.time.LocalDate;
import java.util.*;

class MockResultRepository extends ResultRepository {
    private final Map<Integer, TypingResult> resultMap = Collections.synchronizedMap(new HashMap<>());

    public MockResultRepository() {
        Arrays.stream(new TypingResult[]{
                new TypingResult(1, 240, 40, 2, LocalDate.of(2020, 2, 5), 60),
                new TypingResult(2, 200, 30, 5, LocalDate.of(2020, 2, 7), 60),
                new TypingResult(3, 220, 32, 1, LocalDate.of(2020, 2, 8), 60),
                new TypingResult(4, 280, 50, 0, LocalDate.of(2020, 2, 10), 60)
        }).forEach(this::save);
    }

    @Override
    public List<TypingResult> getAll() {
        return new ArrayList<>(this.resultMap.values());
    }

    @Override
    public TypingResult save(TypingResult value) {
        return this.resultMap.put(value.id(), value);
    }

    @Override
    public TypingResult getById(Integer id) {
        return this.resultMap.get(id);
    }

    @Override
    public TypingResult delete(TypingResult value) {
        return this.resultMap.remove(value.id());
    }

    @Override
    public TypingResult deleteById(Integer id) {
        return this.resultMap.remove(id);
    }
}
