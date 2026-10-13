package com.nisum.bci.mapper;

public interface EntityMapper<D, E> {
    E toEntity(D dto);
}
