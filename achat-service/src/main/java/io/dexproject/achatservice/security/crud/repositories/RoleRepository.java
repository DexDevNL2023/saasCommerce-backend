package io.dexproject.achatservice.security.crud.repositories;

import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.security.crud.entities.Role;
import io.dexproject.achatservice.security.crud.enums.RoleName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends GenericRepository<RoleRequest, Role> {
  @Query("SELECT DISTINCT r FROM Role r WHERE r.libelle = :roleName")
  Role findByRoleName(RoleName roleName);
}
