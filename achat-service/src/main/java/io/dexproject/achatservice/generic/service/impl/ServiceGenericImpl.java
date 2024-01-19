package io.dexproject.achatservice.generic.service.impl;

import io.dexproject.achatservice.exceptions.InternalException;
import io.dexproject.achatservice.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import io.dexproject.achatservice.generic.filter.dto.Filter;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import io.dexproject.achatservice.generic.filter.dto.InternalOperator;
import io.dexproject.achatservice.generic.filter.dto.ValueType;
import io.dexproject.achatservice.generic.filter.dto.builder.FilterBuilder;
import io.dexproject.achatservice.generic.mapper.GenericMapper;
import io.dexproject.achatservice.generic.page.PagedResponse;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import io.dexproject.achatservice.generic.validators.LogExecution;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServiceGenericImpl<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> implements ServiceGeneric<D, R, E> {

  private final JpaEntityInformation<E, Long> entityInformation;
  protected final GenericRepository<E> repository;
  private final GenericMapper<D, R, E> mapper;

  public ServiceGenericImpl(JpaEntityInformation<E, Long> entityInformation, GenericRepository<E> repository, GenericMapper<D, R, E> mapper) {
    this.entityInformation = entityInformation;
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
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public R save(D dto) throws ResourceNotFoundException {
    try {
      E e = mapper.toEntity(dto);
        if (isFieldExist(e, AppConstants.CODE_FILTABLE_FIELD))
            e.setNumOrder(repository.newNumOrder(e.getEntityPrefixe()));
      e = repository.save(e);
      return getOne(e.getId());
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
  public List<R> saveAll(List<D> dtos) throws ResourceNotFoundException {
    try {
      dtos.forEach(this::save);
      return getAll(false);
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
  public R update(D dto, Long id) throws ResourceNotFoundException {
    try {
      if (equalsToDto(dto, id))
        throw new RuntimeException("La ressource " + this.entityInformation.getEntityName() + " avec les données suivante : " + dto.toString() + " existe déjà");
      E entity = getById(id);
      dto.setId(entity.getId());
      entity = repository.save(mapper.toEntity(dto));
      return getOne(entity.getId());
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
  public Boolean exist(Long id) throws ResourceNotFoundException {
    try {
      return repository.existsById(id);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @return List<R>
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public List<R> getAll(Boolean byPeriode) throws ResourceNotFoundException {
    try {
      if (byPeriode) {
        return repository.filter(getFiltresByPeriode()).stream().map(mapper::toDto).collect(Collectors.toList());
      } else {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
      }
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
  public E getById(Long id) throws ResourceNotFoundException {
    try {
      return repository.findById(id).orElseThrow(
              () -> new ResourceNotFoundException("La ressource " + this.entityInformation.getEntityName() + " avec l'id " + id + " n'existe pas")
      );
    } catch (Exception e) {
      throw new InternalException("Une erreur est survenue pendant le traitement de votre requête. Cause : " + e.getMessage());
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
  public R getOne(Long id) throws ResourceNotFoundException {
    try {
      return mapper.toDto(getById(id));
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
  public Boolean equalsToDto(D dto, Long id) throws ResourceNotFoundException {
    try {
      E entity = getById(id);
      return !entity.getId().equals(id) || !entity.equals(dto);
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
   * @param page
   * @param size
   * @return PagedResponse<R>
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public PagedResponse<R> getByPage(Integer page, Integer size) throws ResourceNotFoundException {
    try {
      // Vérifier la syntaxe de page et size
      GenericUtils.validatePageNumberAndSize(page, size);
      // Construire la pagination
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.PERIODE_FILTABLE_FIELD);
      // on récupere les données
      Page<E> list = repository.findAll(pageable);
      if (list.getNumberOfElements() == 0) throw new ResourceNotFoundException("La recherche de " + this.entityInformation.getEntityName() + " est vide!");
      // Mapper Dto
      List<R> listDto = mapper.toDto(list.getContent());
      return new PagedResponse<R>(listDto, list.getNumber(), list.getSize(), list.getTotalElements(), list.getTotalPages(), list.isLast());
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  /**
   * @throws ResourceNotFoundException
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

  public boolean isFieldExist(Object object, String fieldName) {
    Class<?> objectClass = object.getClass();
    for (Field field : objectClass.getFields()) {
      if (field.getName().equals(fieldName)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isFieldExist(Class<?> clazz, String property) {
    String[] fields = property.split("\\.");
    try {
      Field file = clazz.getDeclaredField(fields[0]);
      if (fields.length > 1) {
        return isFieldExist(file.getType(), property.substring(property.indexOf('.') + 1));
      }
      return true;
    } catch (NoSuchFieldException | SecurityException e) {
      if (clazz.getSuperclass() != null) {
        return isFieldExist(clazz.getSuperclass(), property);
      }
      return false;
    }
  }

  private static FilterWrap getFiltresByPeriode() throws ParseException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
    String financiyalYearFrom = "01-01-" + (CurrentYear) + " 07:00:00";
    String financiyalYearTo = "31-12-" + (CurrentYear) + " 23:59:59";
    LocalDate fromdayDateTime = LocalDate.parse(financiyalYearFrom, formatter);
    LocalDate todayDateTime = LocalDate.parse(financiyalYearTo, formatter);
    FilterWrap filterWrap = new FilterWrap();
    List<Filter> filters = new ArrayList<>();
    Filter filterDebut = FilterBuilder.createFilter(AppConstants.PERIODE_FILTABLE_FIELD)
            .value(fromdayDateTime.toString())
            .operator(InternalOperator.GREATER_THAN)
            .type(ValueType.LOCAL_DATE_TIME)
            .build();
    filters.add(filterDebut);
    Filter filterFin = FilterBuilder.createFilter(AppConstants.PERIODE_FILTABLE_FIELD)
            .value(todayDateTime.toString())
            .operator(InternalOperator.GREATER_THAN)
            .type(ValueType.LOCAL_DATE_TIME)
            .build();
    filters.add(filterFin);
    filterWrap.setFilters(filters);
    return filterWrap;
  }
}
