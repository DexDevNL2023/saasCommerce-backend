package io.dexproject.achatservice.security.crud.mapper.impl;

import io.dexproject.achatservice.generic.mapper.impl.GenericMapperImpl;
import io.dexproject.achatservice.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.security.crud.entities.Droit;
import io.dexproject.achatservice.security.crud.mapper.DroitMapper;
import io.dexproject.achatservice.security.crud.repositories.DroitRepository;
import org.mapstruct.Mapper;

@Mapper
public class DroitMapperImplImpl extends GenericMapperImpl<DroitRequest, DroitReponse, Droit> implements DroitMapper {
    protected DroitMapperImplImpl(Class<Droit> entityClass, Class<DroitReponse> dtoClass, DroitRepository repository) {
        super(entityClass, dtoClass, repository);
    }

    @Override
    protected Droit newInstance() {
        return new Droit();
    }
}
