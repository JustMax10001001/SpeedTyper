package com.justsoft.speedtyper.services;

import com.justsoft.speedtyper.model.BaseModel;
import com.justsoft.speedtyper.repositories.CRUDRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractBaseModelMapService<T extends BaseModel> implements CRUDRepository<Integer, T> {

    private final Map<Integer, T> map = new HashMap<>();

    @Override
    public List<T> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public T save(T value) {
        if (value.getId() == null){
            value.setId(map.keySet().size());
        }
        map.put(value.getId(), value);
        return value;
    }

    @Override
    public T getById(Integer integer) {
        return map.get(integer);
    }

    @Override
    public T delete(BaseModel value) {
        return map.remove(value.getId());
    }

    @Override
    public T deleteById(Integer integer) {
        return map.remove(integer);
    }
}
