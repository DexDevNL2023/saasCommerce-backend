package io.dexproject.achatservice.security.crud.repositories;

import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.security.crud.dto.request.ModuleRequest;
import io.dexproject.achatservice.security.crud.entities.Module;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends GenericRepository<ModuleRequest, Module> {
    @Query("SELECT DISTINCT e FROM Module e WHERE e.name = :name")
    Optional<Module> findByName(String name);
}
