package io.dexproject.achatservice.generic.security.crud.repositories;

import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.entities.Droit;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends GenericRepository<Permission> {
    @Query("SELECT DISTINCT e FROM Permission e WHERE e.role = :role AND e.droit = :droit")
    Permission findByRoleAndDroit(Role role, Droit droit);

    @Query("SELECT DISTINCT e FROM Permission e WHERE e.droit = :droit")
    List<Permission> findAllByDroit(Droit droit);

    @Query("SELECT DISTINCT e FROM Permission e WHERE e.role = :role")
    List<Permission> findAllByRole(Role role);
}