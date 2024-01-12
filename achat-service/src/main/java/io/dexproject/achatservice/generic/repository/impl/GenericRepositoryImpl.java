package io.dexproject.achatservice.generic.repository.impl;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.filter.dao.RepoUtil;
import io.dexproject.achatservice.generic.filter.dto.Filter;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import io.dexproject.achatservice.generic.repository.GenericRepository;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    public String newCode() {
        String code = "";
        entityManager.getTransaction().begin();
        final Optional<E> resultList = Optional.ofNullable(entityManager.find(clazz, id));
        entityManager.getTransaction().commit();
        entityManager.close();
        do {
            String newCode =  getCode();
            appEntityCode = appEntityCodeRepository.findByCode(newCode);
            if(appEntityCode == null) {
                code = newCode;
            }
        }while(appEntityCode != null);
        return code;
    }

    @Override
    public void save(E entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void update(E entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void remove(E entity) {
        entityManager.getTransaction().begin();
        final E managedEntity = entityManager.find(clazz, entity.getId());
        if(managedEntity != null) {
            entityManager.remove(managedEntity);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public Optional<E> findById(Long id) {
        entityManager.getTransaction().begin();
        final Optional<E> resultList = Optional.ofNullable(entityManager.find(clazz, id));
        entityManager.getTransaction().commit();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<E> findAll() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<E> criteria = criteriaBuilder.createQuery(clazz);
        final Root<E> root = criteria.from(clazz);
        criteria.select(root);
        final TypedQuery<E> query = entityManager.createQuery(criteria);
        final List<E> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<E> searchBy(String text, int limit, String... fields) {
        SearchResult<E> result = getSearchResult(text, limit, fields);
        return result.hits();
    }

    private SearchResult<E> getSearchResult(String text, int limit, String[] fields) {
        SearchSession searchSession = Search.session(entityManager);
        SearchResult<E> result = searchSession.search(getDomainClass())
                .where(f -> f.match().fields(fields).matching(text).fuzzy(2))
                .fetch(limit);
        return result;
    }

    @Override
    public void reIndex(String indexClassName) throws IndexNotFoundException {
        try {
            SearchSession searchSession = Search.session(entityManager);
            Class<?> classToIndex = Class.forName(indexClassName);
            MassIndexer indexer = searchSession.massIndexer(classToIndex)
                    .threadsToLoadObjects(THREAD_NUMBER);
            indexer.startAndWait();
        } catch (ClassNotFoundException e) {
            throw new IndexNotFoundException("Classe invalide " + indexClassName+" . Cause : "+e.getMessage());
        } catch (InterruptedException e) {
            throw new IndexNotFoundException("Indexation interrompu. Cause :"+e.getMessage());
        }
    }

    @Override
    public List<E> filter(FilterWrap filterWrap) {
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
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public E filterOne(FilterWrap filterWrap) {
        Collection<Filter> filters = filterWrap.getFilters();
        List<Field> declaredClassFields = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toList());
        filters = RepoUtil.extractCorrectFilters(filters, declaredClassFields);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<E> root = criteriaQuery.from(clazz);
        //List<Predicate> predicates = predicates(filters, criteriaBuilder, root);
        List<Predicate> predicates = filters.stream()
                .map(filter -> RepoUtil.extractCriteria(filter, criteriaBuilder, root))
                .collect(Collectors.toList());
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private List<Predicate> predicates(Collection<Filter> filters, CriteriaBuilder criteriaBuilder, Root<E> root) {
        return filters.stream()
                .map(filter -> RepoUtil.extractCriteria(filter, criteriaBuilder, root))
                .collect(Collectors.toList());
    }
}