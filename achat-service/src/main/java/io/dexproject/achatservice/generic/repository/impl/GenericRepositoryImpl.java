package io.dexproject.achatservice.generic.repository.impl;

import io.dexproject.achatservice.generic.filter.dao.RepoUtil;
import io.dexproject.achatservice.generic.filter.dto.Filter;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
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
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class GenericRepositoryImpl<E extends BaseEntity> extends SimpleJpaRepository<E, Long> implements GenericRepository<E> {
    private final Class<E> clazz;
    private final JpaEntityInformation<E, Long> entityInformation;
    @PersistenceContext
    private final EntityManager entityManager;

    public GenericRepositoryImpl(Class<E> clazz, JpaEntityInformation<E, Long> entityInformation, EntityManager entityManager) {
        super(clazz, entityManager);
        this.clazz = clazz;
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    public GenericRepositoryImpl(JpaEntityInformation<E, Long> entityInformation, Class<E> clazz, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.clazz = clazz;
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    @Override
    public String newNumEnrg(String prefixe) {
        String num = "";
        String fieldName = AppConstants.CODE_FILTABLE_FIELD;
        if (!isFieldExist(fieldName)) {
            throw new UnsupportedOperationException("Le champ " + fieldName + " non pris en charge");
        }
        entityManager.getTransaction().begin();
        do {
            String newNum = GenericUtils.GenerateNumEnrg(prefixe);
            String queryString = String.format("select from %s x where %s = :newNum",
                    this.entityInformation.getEntityName(),
                    "%s",
                    fieldName);
            final E result = entityManager.createQuery(queryString, clazz).
                    setParameter("newNum", newNum)
                    .getSingleResult();
            if(result == null) {
                num = newNum;
            }
        } while (!num.isEmpty());
        entityManager.getTransaction().commit();
        entityManager.close();
        return num;
    }

    @Override
    public List<E> searchBy(String text, int limit, String... fields) {
        SearchResult<E> result = getSearchResult(text, limit, fields);
        return result.hits();
    }

    @Override
    public boolean existsByFieldValue(Object value, String fieldName) {
        if (!isFieldExist(fieldName)) {
            throw new UnsupportedOperationException("Le champ " + fieldName + " non pris en charge");
        }

        String queryString = String.format("select from %s x where %s = :value",
                this.entityInformation.getEntityName(),
                "%s",
                fieldName);
        final Optional<E> result = Optional.ofNullable(entityManager.createQuery(queryString, clazz).
                setParameter("value", value.toString())
                .getSingleResult());

        return result.isPresent();
    }

    @Override
    public void reIndex() throws IndexNotFoundException {
        try {
            entityManager.getTransaction().begin();
            SearchSession searchSession = Search.session(entityManager);
            MassIndexer indexer = searchSession.massIndexer(clazz)
                    .threadsToLoadObjects(AppConstants.THREAD_NUMBER);
            indexer.startAndWait();
            entityManager.getTransaction().commit();
            entityManager.close();
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

    private SearchResult<E> getSearchResult(String text, int limit, String[] fields) {
        for (String fieldName : fields) {
            if (!isFieldExist(fieldName)) {
                throw new UnsupportedOperationException("Le champ " + fieldName + " non pris en charge");
            }
        }
        entityManager.getTransaction().begin();
        SearchSession searchSession = Search.session(entityManager);
        SearchResult<E> result = searchSession.search(getDomainClass())
                .where(f -> f.match().fields(fields).matching(text).fuzzy(2))
                .fetch(limit);
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }

    private List<Predicate> predicates(Collection<Filter> filters, CriteriaBuilder criteriaBuilder, Root<E> root) {
        return filters.stream()
                .map(filter -> RepoUtil.extractCriteria(filter, criteriaBuilder, root))
                .collect(Collectors.toList());
    }

    public boolean isFieldExist(String fieldName) {
        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}