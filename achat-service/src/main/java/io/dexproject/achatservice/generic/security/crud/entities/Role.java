package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.entity.audit.BaseEntity;
import io.dexproject.achatservice.generic.security.crud.dto.request.RoleRequest;
import io.dexproject.achatservice.generic.security.crud.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles_utilisateur")
public class Role extends BaseEntity<Role, RoleRequest> {

    private static final String ENTITY_NAME = "ROLE";

    private static final String MODULE_NAME = "AUTORISATIONS";

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName libelle;

    private Boolean isSuper;

    @Override
    public void update(Role source) {
        this.libelle = source.getLibelle();
        this.isSuper = source.getIsSuper();
    }

    @Override
    public boolean equalsToDto(RoleRequest source) {
        if (source == null) {
            return false;
        }
        return libelle.equals(source.getLibelle()) &&
                isSuper.equals(source.getIsSuper());
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }
}
