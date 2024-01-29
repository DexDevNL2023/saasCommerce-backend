package io.dexproject.achatservice.generic.security.crud.mapper.impl;

import io.dexproject.achatservice.generic.mapper.impl.GenericMapperImpl;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Droit;
import io.dexproject.achatservice.generic.security.crud.mapper.DroitMapper;
import org.mapstruct.Mapper;

@Mapper
public class DroitMapperImplImpl extends GenericMapperImpl<DroitRequest, DroitReponse, Droit> implements DroitMapper {
    protected DroitMapperImplImpl(Class<Droit> entityClass, Class<DroitReponse> dtoClass, GenericRepository<Droit> repository) {
        super(entityClass, dtoClass, repository);
    }

    @Override
    protected Droit newInstance() {
        return new Droit();
    }
}
