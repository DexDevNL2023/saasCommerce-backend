package io.dexproject.achatservice.generic.repository.impl;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.List;

@Transactional
public class GenericRepositoryImpl<E extends BaseEntity> extends SimpleJpaRepository<E, Long> implements GenericRepository<E> {

    private final EntityManager entityManager;

    public GenericRepositoryImpl(Class<E> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public GenericRepositoryImpl(
            JpaEntityInformation<E, Long> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<E> searchBy(String text, int limit, String... fields) {
        SearchResult<E> result = getSearchResult(text, limit, fields);
        return result.hits();
    }

    private SearchResult<E> getSearchResult(String text, int limit, String[] fields) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<E> result =
                searchSession
                        .search(getDomainClass())
                        .where(f -> f.match().fields(fields).matching(text).fuzzy(2))
                        .fetch(limit);
        return result;
    }
}