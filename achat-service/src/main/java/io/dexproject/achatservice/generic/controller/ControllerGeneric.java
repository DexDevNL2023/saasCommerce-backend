package io.dexproject.achatservice.generic.controller;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.SearchRequest;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ControllerGeneric<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity> {
  ResponseEntity<RessourceResponse> search(SearchRequest dto);

  ResponseEntity<RessourceResponse> save(D dto);

  ResponseEntity<RessourceResponse> saveAll(List<D> dtos);

  ResponseEntity<RessourceResponse> deleteById(Long id);

  ResponseEntity<RessourceResponse> deleteAll(List<Long> ids);

  ResponseEntity<RessourceResponse> getOne(Long id);

  ResponseEntity<RessourceResponse> getById(Long id);

  ResponseEntity<RessourceResponse> getAll(Boolean byPeriode);

  ResponseEntity<RessourceResponse> getByPage(Integer page, Integer size);

  ResponseEntity<RessourceResponse> update(D dto, Long id);

  ResponseEntity<RessourceResponse> reIndex();
}
