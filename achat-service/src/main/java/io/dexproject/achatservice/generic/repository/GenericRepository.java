package io.dexproject.achatservice.generic.repository;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface GenericRepository<E extends BaseEntity> extends JpaRepository<E, Long> {
    String newCode(String prefixe);

    Boolean exist(FilterWrap filterWrap);

    List<E> searchBy(String text, int limit, String... fields);

    void reIndex(String indexClassName) throws IndexNotFoundException;

    List<E> filter(FilterWrap filterWrap);

    E filterOne(FilterWrap filterWrap);
}
