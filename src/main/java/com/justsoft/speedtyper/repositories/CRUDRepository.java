package com.justsoft.speedtyper.repositories;

import java.util.List;

public interface CRUDRepository<K, V> {

    List<V> getAll();

    V save(V value);

    V getById(K id);

    V delete(V value);

    V deleteById(K id);
}
