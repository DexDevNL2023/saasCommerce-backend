package io.dexproject.achatservice.generic.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.dexproject.achatservice.exceptions.InternalException;
import io.dexproject.achatservice.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import io.dexproject.achatservice.generic.filter.dao.FilterRepo;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.validators.LogExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

public class ServiceGenericImpl<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> implements ServiceGeneric<D, R, E> {

  protected final GenericRepository<E> repository;
  private final GenericMapper<D, R, E> mapper;
  protected final FilterRepo filterRepo;

  public ServiceGenericImpl(GenericRepository<E> repository, GenericMapper<D, R, E> mapper, FilterRepo filterRepo) {
    this.repository = repository;
    this.mapper = mapper;
    this.filterRepo = filterRepo;
  }

  /**
   * @param dto
   * @return R
   * @throws ResourceNotFoundException
   */
  @Override
  @LogExecution
  public R save(D dto, Class<E> clazz) throws ResourceNotFoundException {
    try {
      E e = repository.save(mapper.toEntity(dto));
      e.set
      return mapper.toDto(e);
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
      ids.forEach(this::delete, clazz);
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
  @LogExecution
  public Boolean exist(Long id, Class<E> clazz) throws ResourceNotFoundException {
    try {
      return repository.existsById(id, clazz);
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
      return filterRepo.filterOne(filterWrap, clazz);
    } catch (Exception e) {
      throw new InternalException("La ressource avec l'id " + id + " n'existe pas. Cause : "+e.getMessage());
    }
  }

  /**
   * @return List<R>
   * @throws ResourceNotFoundException
   */
  @Override
  @LogExecution
  public List<R> getAll(Class<E> clazz) throws ResourceNotFoundException {
    try {
      FilterWrap filterWrap = new FilterWrap();
      return filterRepo.filter(filterWrap,clazz).stream().map(mapper::toDto).collect(Collectors.toList());
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
      if (equalsToDto(dto, id)) throw new RuntimeException("La ressource avec les données suivante : " + dto.toString() + " existe déjà");
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
