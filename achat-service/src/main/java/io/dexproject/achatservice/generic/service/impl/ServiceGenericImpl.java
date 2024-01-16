package io.dexproject.achatservice.generic.service.impl;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.generic.validators.CurrentCompany;
import io.dexproject.achatservice.generic.validators.LogExecution;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Slf4j
public class ServiceGenericImpl<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> implements ServiceGeneric<D, R, E> {

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("id" , "createdAt");
  private static final String ID_FILTABLE_FIELD = "id";
  private static final String PERIODE_FILTABLE_FIELD = "createdAt";
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
  public R save(D dto) throws ResourceNotFoundException {
    try {
      E e = repository.save(mapper.toEntity(dto));
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
      return getAll(null);
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
  @LogExecution
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
  @Transactional
  @LogExecution
  public Boolean exist(Long id) throws ResourceNotFoundException {
    try {
      FilterWrap filterWrap = new FilterWrap();
      List<Filter> filters = Collections.singletonList(FilterBuilder
              .createFilter("id")
              .value(id.toString())
              .operator(InternalOperator.EQUALS)
              .type(ValueType.NUMERIC)
              .build()
      );
      filterWrap.setFilters(filters);
      return repository.exist(filterWrap);
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
  public R getOne(Long id, Boolean byPeriode) throws ResourceNotFoundException {
    try {
      return mapper.toDto(getById(id, byPeriode));
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
  public E getById(Long id, Boolean byPeriode) throws ResourceNotFoundException {
    try {
      if (byPeriode) {
        return repository.filterOne(getFiltresByPeriode(id));
      } else {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("La ressource avec l'id " + id + " n'existe pas")
        );
      }
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
  public List<R> getAll(Boolean byPeriode) throws ResourceNotFoundException {
    try {
      if (byPeriode) {
        return repository.filter(getFiltresByPeriode(null)).stream().map(mapper::toDto).collect(Collectors.toList());
      } else {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
      }
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
  public R update(D dto, Long id) throws ResourceNotFoundException {
    try {
      if (equalsToDto(dto, id)) throw new RuntimeException("La ressource avec les données suivante : " + dto.toString() + " existe déjà");
      E entity = getById(id);
      dto.setId(entity.getId());
      entity = repository.save(mapper.toEntity(dto));
      return getOne(entity.getId());
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
   * @throws ResourceNotFoundException
   */
  @Override
  @Transactional
  @LogExecution
  public void reIndex(String indexClassName) throws IndexNotFoundException {
    try {
      repository.reIndex(indexClassName);
    } catch (Exception e) {
      throw new InternalException(e.getMessage());
    }
  }

  private static FilterWrap getFiltresByPeriode(Long id) throws ParseException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
    String financiyalYearFrom="01-01-"+(CurrentYear)+" 07:00:00";
    String financiyalYearTo="31-12-"+(CurrentYear)+" 23:59:59";
    LocalDate fromdayDateTime = LocalDate.parse(financiyalYearFrom, formatter);
    LocalDate todayDateTime = LocalDate.parse(financiyalYearTo, formatter);
    FilterWrap filterWrap = new FilterWrap();
    List<Filter> filters = new ArrayList<>();
    if (id != null) {
      Filter filterDebut = FilterBuilder.createFilter(ID_FILTABLE_FIELD)
              .value(Long.toString(id))
              .operator(InternalOperator.EQUALS)
              .type(ValueType.NUMERIC)
              .build();
      filters.add(filterDebut);
    }
    Filter filterDebut = FilterBuilder.createFilter(PERIODE_FILTABLE_FIELD)
            .value(fromdayDateTime.toString())
            .operator(InternalOperator.GREATER_THAN)
            .type(ValueType.LOCAL_DATE_TIME)
            .build();
    filters.add(filterDebut);
    Filter filterFin = FilterBuilder.createFilter(PERIODE_FILTABLE_FIELD)
            .value(todayDateTime.toString())
            .operator(InternalOperator.GREATER_THAN)
            .type(ValueType.LOCAL_DATE_TIME)
            .build();
    filters.add(filterFin);
    filterWrap.setFilters(filters);
    return filterWrap;
  }
}
