package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles_utilisateur")
public class Role extends BaseEntity {

    private static final String ENTITY_NAME = "ROLE";

    private static final String MODULE_NAME = "AUTORISATIONS";

    @Column(nullable = false, unique = true)
    private String libelle;

    private Boolean isSuper = false;

    private Boolean isGrant = false;

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }
}
