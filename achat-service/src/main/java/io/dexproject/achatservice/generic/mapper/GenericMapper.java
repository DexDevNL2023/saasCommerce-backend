package io.dexproject.achatservice.generic.mapper;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;

import java.util.List;

public interface GenericMapper<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity> {
    E toEntity(D dto);
    List<E> toEntity(List<D> dtos);
    R toDto(E entity);
    List<R> toDto(List<E> entities);
    E byId(Long id);
    List<E> byId(List<Long> ids);
    Long toId(E entity);
    List<Long> toId(List<E> entities);
    E map(E entity, Object from);
}
