package io.dexproject.achatservice.generic.service;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import io.dexproject.achatservice.generic.entity.PagedResponse;
import io.dexproject.achatservice.generic.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.generic.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.validators.FieldValueExists;
import org.apache.lucene.index.IndexNotFoundException;

import java.util.List;

public interface ServiceGeneric<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> extends FieldValueExists {
  List<R> search(String text, List<String> fields, int limit) throws ResourceNotFoundException;
  R save(D dto) throws ResourceNotFoundException;
  List<R> saveAll(List<D> dtos) throws ResourceNotFoundException;
  void delete(Long id) throws SuppressionException;
  void deleteAll(List<Long> ids) throws SuppressionException;
  Boolean exist(Long id) throws ResourceNotFoundException;
  R getOne(Long id) throws ResourceNotFoundException;
  E getById(Long id) throws ResourceNotFoundException;
  List<R> getAll(Boolean byPeriode) throws ResourceNotFoundException;
  PagedResponse<R> getByPage(Integer page, Integer size) throws ResourceNotFoundException;
  R update(D dto, Long id) throws ResourceNotFoundException;
  Boolean equalsToDto(D dto, Long id) throws ResourceNotFoundException;
  void reIndex() throws IndexNotFoundException;
}
