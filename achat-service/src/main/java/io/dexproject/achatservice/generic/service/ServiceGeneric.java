package io.dexproject.achatservice.generic.service;

import java.util.List;

import io.dexproject.achatservice.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceGeneric<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> {
  R save(D dto) throws ResourceNotFoundException;
  List<R> saveAll(List<D> dtos) throws ResourceNotFoundException;
  void delete(Long id) throws SuppressionException;
  void deleteAll(List<Long> ids) throws SuppressionException;
  Boolean exist(Long id) throws ResourceNotFoundException;
  R getOne(Long id) throws ResourceNotFoundException;
  E getById(Long id) throws ResourceNotFoundException;
  List<R> getAll() throws ResourceNotFoundException;
  Page<R> getByPage(Pageable pageable) throws ResourceNotFoundException;
  R update(D dto, Long id) throws ResourceNotFoundException;
  Boolean equalsToDto(D dto, Long id) throws ResourceNotFoundException;
}
