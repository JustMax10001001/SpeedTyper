package com.justsoft.speedtyper.repositories;

import java.util.List;

public interface CRUDRepository<ID, T> {

    List<T> getAll();

    T save(T value);

    T getById(ID id);

    T delete(T value);

    T deleteById(ID id);
}
