package io.dexproject.achatservice.generic.repository;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface GenericRepository<E extends BaseEntity> extends JpaRepository<E, Long> {
    List<E> searchBy(String text, int limit, String... fields);
}
