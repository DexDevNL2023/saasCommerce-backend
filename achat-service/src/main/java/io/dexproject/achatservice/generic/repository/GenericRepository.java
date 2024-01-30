package io.dexproject.achatservice.generic.repository;

import io.dexproject.achatservice.generic.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.entity.audit.BaseEntity;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface GenericRepository<D extends BaseRequest, E extends BaseEntity<E, D>> extends JpaRepository<E, Long> {
    String newNumEnrg(String prefixe);

    void reIndex() throws IndexNotFoundException;

    List<E> searchBy(String text, int limit, String... fields);

    boolean existsByFieldValue(Object value, String fieldName);
}
