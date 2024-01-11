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
  List<R> search(String text, List<String> fields, int limit) throws ResourceNotFoundException;
  R save(D dto, Class<E> clazz) throws ResourceNotFoundException;
  List<R> saveAll(List<D> dtos, Class<E> clazz) throws ResourceNotFoundException;
  void delete(Long id, Class<E> clazz) throws SuppressionException;
  void deleteAll(List<Long> ids, Class<E> clazz) throws SuppressionException;
  Boolean exist(Long id, Class<E> clazz) throws ResourceNotFoundException;
  R getOne(Long id, Class<E> clazz) throws ResourceNotFoundException;
  E getById(Long id, Class<E> clazz) throws ResourceNotFoundException;
  List<R> getAll(Class<E> clazz) throws ResourceNotFoundException;
  Page<R> getByPage(Pageable pageable) throws ResourceNotFoundException;
  R update(D dto, Long id, Class<E> clazz) throws ResourceNotFoundException;
  Boolean equalsToDto(D dto, Long id, Class<E> clazz) throws ResourceNotFoundException;
}
