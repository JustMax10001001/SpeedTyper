package com.justsoft.speedtyper.services;

import com.justsoft.speedtyper.model.entities.BaseEntityRecord;
import com.justsoft.speedtyper.repositories.CRUDRepository;
import com.justsoft.speedtyper.util.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractBaseModelMapService<T extends BaseEntityRecord> implements CRUDRepository<Integer, T> {

    private final Map<Integer, T> map = new HashMap<>();

    @Override
    public List<T> getAll() {
        return new ArrayList<>(map.values());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T save(T value) {
        if (value.id() == 0){
            int maxKey = map.keySet().stream().mapToInt(i->i).max().orElse(1);

            value = Objects.notNull((T) value.withId(maxKey + 1));
        }
        map.put(value.id(), value);
        return value;
    }

    @Override
    public T getById(Integer integer) {
        return map.get(integer);
    }

    @Override
    public T delete(BaseEntityRecord value) {
        return map.remove(value.id());
    }

    @Override
    public T deleteById(Integer integer) {
        return map.remove(integer);
    }
}
