package io.dexproject.achatservice.generic.mapper;

import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponseDto;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequestDto;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;

import java.util.Optional;

public abstract class GenericMapperImpl<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> implements GenericMapper<D, R, E> {

    protected final GenericRepository<E> repository;

    protected GenericMapperImpl(GenericRepository<E> repository) {
        this.repository = repository;
    }

    protected abstract E newInstance();

    @Override
    public final E map(Long id) {
        E entity = newInstance();
        if (id != null) {
            Optional<E> find = repository.findById(id);
            E dto = find.get();
            return map(entity, dto);
        }
        return null;
    }

    @Override
    public final Long map(E entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }
}
