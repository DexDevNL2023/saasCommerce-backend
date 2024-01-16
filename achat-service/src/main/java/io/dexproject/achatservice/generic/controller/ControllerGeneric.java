package io.dexproject.achatservice.generic.controller;

import io.dexproject.achatservice.generic.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ControllerGeneric<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> {
  ResponseEntity<ResourceResponse> search(SearchRequestDTO dto);
  ResponseEntity<ResourceResponse> save(D dto);
  ResponseEntity<ResourceResponse> saveAll(List<D> dtos);
  ResponseEntity<ResourceResponse> deleteById(Long id);
  ResponseEntity<ResourceResponse> deleteAll(List<Long> ids);
  ResponseEntity<ResourceResponse> getOne(Long id);
  ResponseEntity<ResourceResponse> getById(Long id);
  ResponseEntity<ResourceResponse> getAll(Boolean byPeriode);
  ResponseEntity<ResourceResponse> getByPage(Integer page, Integer size);
  ResponseEntity<ResourceResponse> update(D dto, Long id);
  ResponseEntity<ResourceResponse> reIndex();
}
