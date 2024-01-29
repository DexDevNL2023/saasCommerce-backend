package io.dexproject.achatservice.generic.security.crud.services.impl;

import io.dexproject.achatservice.generic.mapper.impl.GenericMapperImpl;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Droit;
import io.dexproject.achatservice.generic.security.crud.services.DroitService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DroitServiceImpl extends ServiceGenericImpl<DroitRequest, DroitReponse, Droit> implements DroitService {
    public DroitServiceImpl(JpaEntityInformation<Droit, Long> entityInformation, GenericRepository<Droit> repository, GenericMapperImpl<DroitRequest, DroitReponse, Droit> mapper) {
        super(entityInformation, repository, mapper);
    }
}
