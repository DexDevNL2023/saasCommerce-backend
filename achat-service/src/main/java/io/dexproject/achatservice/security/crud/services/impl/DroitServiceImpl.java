package io.dexproject.achatservice.security.crud.services.impl;

import io.dexproject.achatservice.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.security.crud.entities.Droit;
import io.dexproject.achatservice.security.crud.mapper.DroitMapper;
import io.dexproject.achatservice.security.crud.repositories.DroitRepository;
import io.dexproject.achatservice.security.crud.services.DroitService;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DroitServiceImpl extends ServiceGenericImpl<DroitRequest, DroitReponse, Droit> implements DroitService {
    public DroitServiceImpl(JpaEntityInformation<Droit, Long> entityInformation, DroitRepository repository, DroitMapper mapper) {
        super(entityInformation, repository, mapper);
    }
}
