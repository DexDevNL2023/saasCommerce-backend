package io.dexproject.achatservice.security.crud.controllers;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.security.crud.dto.reponse.DroitReponse;
import io.dexproject.achatservice.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.security.crud.entities.Droit;

public interface DroitController extends ControllerGeneric<DroitRequest, DroitReponse, Droit> {
}
