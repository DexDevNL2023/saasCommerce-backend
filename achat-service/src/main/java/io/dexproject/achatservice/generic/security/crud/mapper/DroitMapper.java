package io.dexproject.achatservice.generic.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Droit;

public interface DroitMapper extends GenericMapper<DroitRequest, DroitReponse, Droit> {
}
