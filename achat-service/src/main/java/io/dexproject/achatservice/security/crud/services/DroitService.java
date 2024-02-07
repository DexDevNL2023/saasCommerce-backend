package io.dexproject.achatservice.security.crud.services;

import io.dexproject.achatservice.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.security.crud.entities.Droit;
import io.dexproject.achatservice.generic.service.ServiceGeneric;

public interface DroitService extends ServiceGeneric<DroitRequest, DroitReponse, Droit> {
}
