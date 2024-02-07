package io.dexproject.achatservice.generic.service.impl;

import io.dexproject.achatservice.generic.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.entity.audit.BaseEntity;
import io.dexproject.achatservice.exceptions.InternalException;
import io.dexproject.achatservice.exceptions.RessourceNotFoundException;
import io.dexproject.achatservice.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.dto.reponse.PagedResponse;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.utils.AppConstants;
import io.dexproject.achatservice.utils.GenericUtils;
import io.dexproject.achatservice.validators.log.LogExecution;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
public abstract class ServiceGenericImpl<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity<E, D>> implements ServiceGeneric<D, R, E> {

  private final JpaEntityInformation<E, Long> entityInformation;
    protected final GenericRepository<D, E> repository;
  private final GenericMapper<D, R, E> mapper;

    public ServiceGenericImpl(JpaEntityInformation<E, Long> entityInformation, GenericRepository<D, E> repository, GenericMapper<D, R, E> mapper) {
    this.entityInformation = entityInformation;
    this.repository = repository;
    this.mapper = mapper;
  }

  /**
   * @param text
   * @param fields
   * @param limit
   * @return List<R>
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public List<R> search(String text, List<String> fields, int limit) throws RessourceNotFoundException {
      List<String> fieldsToSearchBy = fields.isEmpty() ? AppConstants.SEARCHABLE_FIELDS : fields;
      boolean containsInvalidField = fieldsToSearchBy.stream().anyMatch(f -> !AppConstants.SEARCHABLE_FIELDS.contains(f));
    if(containsInvalidField) {
      throw new IllegalArgumentException();
    }
    return repository.searchBy(text, limit, fieldsToSearchBy.toArray(new String[0])).stream().map(mapper::toDto).collect(Collectors.toList());
  }

  /**
   * @param dto
   * @return R
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public R save(D dto) throws RessourceNotFoundException {
    try {
      E e = mapper.toEntity(dto);
      e.setNumEnrg(repository.newNumEnrg(e.getEntityName()));
      e = repository.save(e);
      return getOne(e.getId());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param dtos
   * @return List<R>
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public List<R> saveAll(List<D> dtos) throws RessourceNotFoundException {
    try {
      dtos.forEach(this::save);
      return getAll();
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param dto
   * @param id
   * @return R
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public R update(D dto, Long id) throws RessourceNotFoundException {
    try {
      E entity = getById(id);
        if (entity.equalsToDto(dto))
        throw new RessourceNotFoundException("La ressource " + this.entityInformation.getEntityName() + " avec les données suivante : " + dto.toString() + " existe déjà");
        entity.update(mapper.toEntity(dto));
      entity = repository.save(entity);
      return getOne(entity.getId());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param id
   * @return Boolean
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public Boolean exist(Long id) throws RessourceNotFoundException {
    try {
      return repository.existsById(id);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @return List<R>
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public List<R> getAll() throws RessourceNotFoundException {
    try {
      return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  @Override
  @Transactional
  @LogExecution
  public List<R> getAll(List<Long> ids) throws RessourceNotFoundException {
    try {
      return repository.findAllById(ids).stream().map(mapper::toDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param page
   * @param size
   * @return PagedResponse<R>
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public PagedResponse<R> getAllByPage(Integer page, Integer size) throws RessourceNotFoundException {
    try {
      // Vérifier la syntaxe de page et size
      GenericUtils.validatePageNumberAndSize(page, size);
      // Construire la pagination
      Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.PERIODE_FILTABLE_FIELD);
      // on récupere les données
      Page<E> list = repository.findAll(pageable);
      if (list.getNumberOfElements() == 0)
        throw new RessourceNotFoundException("La recherche de " + this.entityInformation.getEntityName() + " est vide!");
      // Mapper Dto
      List<R> listDto = mapper.toDto(list.getContent());
      return new PagedResponse<R>(listDto, list.getNumber(), list.getSize(), list.getTotalElements(), list.getTotalPages(), list.isLast());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @param id
   * @return E
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public E getById(Long id) throws RessourceNotFoundException {
    try {
      return repository.findById(id).orElseThrow(
              () -> new RessourceNotFoundException("La ressource " + this.entityInformation.getEntityName() + " avec l'id " + id + " n'existe pas")
      );
    } catch (Exception e) {
      throw new InternalException("Une erreur est survenue pendant le traitement de votre requête. Cause : " + e.getMessage());
    }
  }

  /**
   * @param id
   * @return R
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public R getOne(Long id) throws RessourceNotFoundException {
    try {
      return mapper.toDto(getById(id));
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
  public void delete(Long id) throws SuppressionException {
    try {
      if (!exist(id)) throw new SuppressionException("La ressource " + this.entityInformation.getEntityName() + " avec l'id " + id + " n'existe pas");
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
  public void deleteAll(List<Long> ids) throws SuppressionException {
    try {
      ids.forEach(this::delete);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @throws RessourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public void reIndex() throws IndexNotFoundException {
    try {
      repository.reIndex();
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  @Override
  @Transactional
  @LogExecution
  public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
    try {
      if (value == null) {
        return false;
      }
      return this.repository.existsByFieldValue(value, fieldName);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }
}
