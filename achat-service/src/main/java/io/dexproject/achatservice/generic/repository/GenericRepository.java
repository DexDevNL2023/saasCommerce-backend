package io.dexproject.achatservice.generic.repository;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface GenericRepository<E extends BaseEntity> extends JpaRepository<E, Long> {
    String newNumOrder(String prefixe);

    void reIndex() throws IndexNotFoundException;

    List<E> filter(FilterWrap filterWrap);

    List<E> searchBy(String text, int limit, String... fields);

    boolean existsByFieldValue(Object value, String fieldName);
}
