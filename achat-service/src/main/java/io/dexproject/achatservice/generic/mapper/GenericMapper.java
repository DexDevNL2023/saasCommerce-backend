package io.dexproject.achatservice.generic.mapper;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponseDto;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequestDto;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

public interface GenericMapper<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> {
    E toEntity(D dto);
    R toDto(E entity);
    List<E> toEntity(List<D> dtos);
    List<R> toDto(List<E> entities);
    @Named("map")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    E map(@MappingTarget Class<? extends E> entity, Object from);
}
