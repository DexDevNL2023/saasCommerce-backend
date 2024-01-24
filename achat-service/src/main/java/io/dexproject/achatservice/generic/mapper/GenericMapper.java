package io.dexproject.achatservice.generic.mapper;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponseDto;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequestDto;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = AbstractGenericMapper.class)
public interface GenericMapper<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> {
    E toEntity(D dto);
    R toDto(E entity);
    List<E> toEntity(List<D> dtos);
    List<R> toDto(List<E> entities);

    E byId(Long id);

    Long toId(E entity);
    @Named("map")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    E map(@MappingTarget E entity, Object from);
}
