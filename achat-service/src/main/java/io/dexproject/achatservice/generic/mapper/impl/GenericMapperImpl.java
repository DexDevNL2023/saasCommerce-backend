package io.dexproject.achatservice.generic.mapper.impl;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.mapper.GenericMapperUtils;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class GenericMapperImpl<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity> implements GenericMapper<D, R, E> {

    protected abstract E newInstance();
    private final Class<E> entityClass;
    private final Class<R> dtoClass;
    private final GenericRepository<E> repository;

    protected GenericMapperImpl(Class<E> entityClass, Class<R> dtoClass, GenericRepository<E> repository) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.repository = repository;
    }

    @Override
    public E toEntity(D dto) {
        return GenericMapperUtils.map(dto, entityClass);
    }

    @Override
    public List<E> toEntity(List<D> dtos) {
        return GenericMapperUtils.mapAll(dtos, entityClass);
    }

    @Override
    public R toDto(E entity) {
        return GenericMapperUtils.map(entity, dtoClass);
    }

    @Override
    public List<R> toDto(List<E> entities) {
        return GenericMapperUtils.mapAll(entities, dtoClass);
    }
    
    @Override
    public final E byId(Long id) {
        if (id == null) {
            return null;
        }
        E entity = newInstance();
        Optional<E> find = repository.findById(id);
        if (find.isPresent()) {
            E dto = find.get();
            return map(entity, dto);
        }
        return null;
    }

    @Override
    public final List<E> byId(List<Long> ids) {
        if (ids.isEmpty()) {
            return null;
        }
        return ids.stream()
                .map(this::byId)
                .collect(Collectors.toList());
    }

    @Override
    public final Long toId(E entity) {
        if (entity == null) {
            return null;
        }
        return entity.getId();
    }

    @Override
    public final List<Long> toId(List<E> entities) {
        if (entities.isEmpty()) {
            return null;
        }
        return entities.stream()
                .map(this::toId)
                .collect(Collectors.toList());
    }

    @Override
    public E map(E entity, Object from) {
        return GenericMapperUtils.map(from, entity);
    }
}
