package io.dexproject.achatservice.generic.controller;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ControllerGeneric<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> {
  ResponseEntity<R> save(D dto);
  ResponseEntity<List<R>> saveAll(List<D> dtos);
  ResponseEntity<String> deleteById(Long id);
  ResponseEntity<String> deleteAll(List<Long> ids);
  ResponseEntity<R> getOne(Long id);
  ResponseEntity<E> getById(Long id);
  ResponseEntity<List<R>> getAll();
  ResponseEntity<Page<R>> getByPage(Pageable pageable);
  ResponseEntity<R> update(D dto, Long id);
}
