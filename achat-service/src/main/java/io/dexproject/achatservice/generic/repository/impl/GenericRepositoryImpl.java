package io.dexproject.achatservice.generic.repository.impl;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.filter.dao.RepoUtil;
import io.dexproject.achatservice.generic.filter.dto.Filter;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import io.dexproject.achatservice.generic.filter.dto.InternalOperator;
import io.dexproject.achatservice.generic.filter.dto.ValueType;
import io.dexproject.achatservice.generic.filter.dto.builder.FilterBuilder;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.utils.MyUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.apache.lucene.index.IndexNotFoundException;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
public class GenericRepositoryImpl<E extends BaseEntity> extends SimpleJpaRepository<E, Long> implements GenericRepository<E> {
    private final Class<E> clazz;
    private static final int THREAD_NUMBER = 4;
    @PersistenceContext
    private final EntityManager entityManager;

    public GenericRepositoryImpl(Class<E> clazz, EntityManager entityManager) {
        super(clazz, entityManager);
        this.clazz = clazz;
        this.entityManager = entityManager;
    }

    public GenericRepositoryImpl(JpaEntityInformation<E, Long> entityInformation, Class<E> clazz, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.clazz = clazz;
        this.entityManager = entityManager;
    }

    @Override
    public String newCode(String prefixe) {
        String code = "";
        entityManager.getTransaction().begin();
        do {
            String newCode = MyUtils.GenerateCode(prefixe);
            FilterWrap filterWrap = new FilterWrap();
            List<Filter> filters = Collections.singletonList(FilterBuilder
                    .createFilter("code")
                    .value(newCode)
                    .operator(InternalOperator.EQUALS)
                    .type(ValueType.STRING)
                    .build()
            );
            filterWrap.setFilters(filters);
            final E result = filterOne(filterWrap);
            if(result == null) {
                code = newCode;
            }
        }while(!code.isEmpty());
        entityManager.getTransaction().commit();
        entityManager.close();
        return code;
    }

    @Override
    public Boolean exist(FilterWrap filterWrap) {
        entityManager.getTransaction().begin();
        final E resultList = filterOne(filterWrap);
        entityManager.getTransaction().commit();
        entityManager.close();
        return (resultList == null);
    }

    @Override
    public List<E> searchBy(String text, int limit, String... fields) {
        SearchResult<E> result = getSearchResult(text, limit, fields);
        return result.hits();
    }

    private SearchResult<E> getSearchResult(String text, int limit, String[] fields) {
        entityManager.getTransaction().begin();
        SearchSession searchSession = Search.session(entityManager);
        SearchResult<E> result = searchSession.search(getDomainClass())
                .where(f -> f.match().fields(fields).matching(text).fuzzy(2))
                .fetch(limit);
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }

    @Override
    public void reIndex(String indexClassName) throws IndexNotFoundException {
        try {
            entityManager.getTransaction().begin();
            SearchSession searchSession = Search.session(entityManager);
            Class<?> classToIndex = Class.forName(indexClassName);
            MassIndexer indexer = searchSession.massIndexer(classToIndex)
                    .threadsToLoadObjects(THREAD_NUMBER);
            indexer.startAndWait();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (ClassNotFoundException e) {
            throw new IndexNotFoundException("Classe invalide " + indexClassName+" . Cause : "+e.getMessage());
        } catch (InterruptedException e) {
            throw new IndexNotFoundException("Indexation interrompu. Cause :"+e.getMessage());
        }
    }

    @Override
    public List<E> filter(FilterWrap filterWrap) {
        entityManager.getTransaction().begin();
        Collection<Filter> filters = filterWrap.getFilters();
        List<Field> declaredClassFields = Arrays
                .stream(clazz.getDeclaredFields())
                .collect(Collectors.toList());
        filters = RepoUtil.extractCorrectFilters(filters, declaredClassFields);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<E> root = criteriaQuery.from(clazz);
        List<Predicate> predicates = predicates(filters, criteriaBuilder, root);
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        final List<E> resultList = query.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return resultList;
    }

    @Override
    public E filterOne(FilterWrap filterWrap) {
        entityManager.getTransaction().begin();
        Collection<Filter> filters = filterWrap.getFilters();
        List<Field> declaredClassFields = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toList());
        filters = RepoUtil.extractCorrectFilters(filters, declaredClassFields);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<E> root = criteriaQuery.from(clazz);
        List<Predicate> predicates = predicates(filters, criteriaBuilder, root);
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        final E resultList = query.getSingleResult();
        entityManager.getTransaction().commit();
        entityManager.close();
        return resultList;
    }

    private List<Predicate> predicates(Collection<Filter> filters, CriteriaBuilder criteriaBuilder, Root<E> root) {
        return filters.stream()
                .map(filter -> RepoUtil.extractCriteria(filter, criteriaBuilder, root))
                .collect(Collectors.toList());
    }
}