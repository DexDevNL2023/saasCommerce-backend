package io.dexproject.achatservice.generic.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.dexproject.achatservice.exceptions.InternalException;
import io.dexproject.achatservice.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import io.dexproject.achatservice.generic.filter.dao.FilterRepo;
import io.dexproject.achatservice.generic.filter.dto.Filter;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import io.dexproject.achatservice.generic.filter.dto.InternalOperator;
import io.dexproject.achatservice.generic.filter.dto.ValueType;
import io.dexproject.achatservice.generic.filter.dto.builder.FilterBuilder;
import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.validators.LogExecution;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Slf4j
public class ServiceGenericImpl<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> implements ServiceGeneric<D, R, E> {

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("author" , "name");
  protected final GenericRepository<E> repository;
  private final GenericMapper<D, R, E> mapper;

  public ServiceGenericImpl(GenericRepository<E> repository, GenericMapper<D, R, E> mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }


  /**
   * @param text
   * @param fields
   * @param limit
   * @return List<R>
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public List<R> search(String text, List<String> fields, int limit) throws ResourceNotFoundException {
    List<String> fieldsToSearchBy = fields.isEmpty() ? SEARCHABLE_FIELDS : fields;
    boolean containsInvalidField = fieldsToSearchBy.stream(). anyMatch(f -> !SEARCHABLE_FIELDS.contains(f));
    if(containsInvalidField) {
      throw new IllegalArgumentException();
    }
    return repository.searchBy(text, limit, fieldsToSearchBy.toArray(new String[0])).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  /**
   * @param dto
   * @return R
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public R save(D dto, Class<E> clazz) throws ResourceNotFoundException {
    try {
      E e = repository.save(mapper.toEntity(dto));
      return getOne(e.getId(), clazz);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param dtos
   * @return List<R>
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public List<R> saveAll(List<D> dtos, Class<E> clazz) throws ResourceNotFoundException {
    try {
      List<R> list = new ArrayList<>();
      dtos.forEach(dto -> list.add(save(dto, clazz)));
      return getAll(clazz);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param id
   * @throws SuppressionException
   */
  @Override
  @Transactional
  @LogExecution
  public void delete(Long id, Class<E> clazz) throws SuppressionException {
    try {
      if (!exist(id, clazz)) throw new SuppressionException("La ressource avec l'id " + id + " n'existe pas");
      repository.deleteById(id);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param ids
   * @throws SuppressionException
   */
  @Override
  @Transactional
  @LogExecution
  public void deleteAll(List<Long> ids, Class<E> clazz) throws SuppressionException {
    try {
      ids.forEach(id -> delete(id, clazz));
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param id
   * @return Boolean
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public Boolean exist(Long id, Class<E> clazz) throws ResourceNotFoundException {
    try {
      FilterWrap filterWrap = new FilterWrap();
      List<Filter> filters = Collections.singletonList(FilterBuilder
              .createFilter("not_existing_name")
              .value("test_name")
              .operator(InternalOperator.EQUALS)
              .type(ValueType.NUMERIC)
              .build()
      );
      filterWrap.setFilters(filters);
      return repository.existsById(filterWrap, clazz);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param id
   * @return R
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public R getOne(Long id, Class<E> clazz) throws ResourceNotFoundException {
    try {
      return mapper.toDto(getById(id, clazz));
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param id
   * @return E
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public E getById(Long id, Class<E> clazz) throws ResourceNotFoundException {
    try {
      FilterWrap filterWrap = new FilterWrap();
      return repository.filterOne(filterWrap, clazz);
    } catch (Exception e) {
      throw new InternalException("La ressource avec l'id " + id + " n'existe pas. Cause : "+e.getMessage());
    }
  }

  /**
   * @return List<R>
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public List<R> getAll(Class<E> clazz) throws ResourceNotFoundException {
    try {
      FilterWrap filterWrap = new FilterWrap();
      return repository.filter(filterWrap,clazz).stream().map(mapper::toDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param pageable
   * @return Page<R>
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public Page<R> getByPage(Pageable pageable) throws ResourceNotFoundException {
    try {
      Page<E> entityPage = repository.findAll(pageable);
      List<E> entities = entityPage.getContent();
      return new PageImpl<>(mapper.toDto(entities), pageable, entityPage.getTotalElements());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param dto
   * @param id
   * @return R
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public R update(D dto, Long id, Class<E> clazz) throws ResourceNotFoundException {
    try {
      if (equalsToDto(dto, id, clazz)) throw new RuntimeException("La ressource avec les données suivante : " + dto.toString() + " existe déjà");
      E exist = getById(id, clazz);
      dto.setId(exist.getId());
      return mapper.toDto(repository.save(mapper.toEntity(dto)));
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param dto
   * @param id
   * @return Boolean
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public Boolean equalsToDto(D dto, Long id, Class<E> clazz) throws ResourceNotFoundException {
    try {
      E ressource = getById(id, clazz);
      return !ressource.getId().equals(id) || !ressource.equals(dto);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }
}
