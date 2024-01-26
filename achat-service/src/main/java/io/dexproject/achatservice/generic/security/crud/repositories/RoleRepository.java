package io.dexproject.achatservice.generic.security.crud.repositories;

import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends GenericRepository<Role> {
  @Query("SELECT DISTINCT r FROM Role r WHERE r.libelle = :roleName")
  Role findByRoleName(String roleName);
}
