package io.dexproject.achatservice.generic.mapper;

import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractGenericMapper<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity> implements GenericMapper<D, R, E> {

    protected final GenericRepository<E> repository;

    protected AbstractGenericMapper(GenericRepository<E> repository) {
        this.repository = repository;
    }

    protected abstract E newInstance();

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
}
