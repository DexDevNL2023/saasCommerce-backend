package io.dexproject.achatservice.generic.security.crud.services;

public interface AutorisationService {
    void changeAutorisation(PermissionDTO permissionDTO);

    void changeIsDefaultDroit(DroitDTO droitDTO);

    void addDroit(SaveDroitDTO saveDroitDTO);
}


