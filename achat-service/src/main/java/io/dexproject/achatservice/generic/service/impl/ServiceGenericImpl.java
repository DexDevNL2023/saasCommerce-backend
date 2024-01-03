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
import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

public class ServiceGenericImpl<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> implements ServiceGeneric<D, R, E> {

  protected final GenericRepository<E> repository;
  private final GenericMapper<D, R, E> mapper;

  public ServiceGenericImpl(GenericRepository<E> repository, GenericMapper<D, R, E> mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  /**
   * @param dto
   * @return R
   * @throws ResourceNotFoundException
   */
  @Override
  public R save(D dto) throws ResourceNotFoundException {
    try {
      return mapper.toDto(repository.save(mapper.toEntity(dto)));
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
  public List<R> saveAll(List<D> dtos) throws ResourceNotFoundException {
    try {
      List<R> list = new ArrayList<>();
      dtos.forEach(dto -> list.add(save(dto)));
      return list;
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
  public void delete(Long id) throws SuppressionException {
    try {
      if (!exist(id)) throw new SuppressionException("La ressource avec l'id " + id + " n'existe pas");
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
  public void deleteAll(List<Long> ids) throws SuppressionException {
    try {
      ids.forEach(this::delete);
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
  public Boolean exist(Long id) throws ResourceNotFoundException {
    try {
      return repository.existsById(id);
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
  public R getOne(Long id) throws ResourceNotFoundException {
    try {
      return mapper.toDto(getById(id));
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
  public E getById(Long id) throws ResourceNotFoundException {
    try {
      return repository.findById(id).orElseThrow(
        () -> new RuntimeException("La ressource avec l'id " + id + " n'existe pas")
      );
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @return List<R>
   * @throws ResourceNotFoundException
   */
  @Override
  public List<R> getAll() throws ResourceNotFoundException {
    try {
      return repository.findAll().stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());
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
  public R update(D dto, Long id) throws ResourceNotFoundException {
    try {
      if (equalsToDto(dto, id)) throw new RuntimeException("La ressource avec les données suivante : " + dto.toString() + " existe déjà");
      E exist = getById(id);
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
  public Boolean equalsToDto(D dto, Long id) throws ResourceNotFoundException {
    try {
      E ressource = getById(id);
      return !ressource.getId().equals(id) || !ressource.equals(dto);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }
}
