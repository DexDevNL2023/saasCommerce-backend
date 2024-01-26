package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions_utilisateur")
public class Permission extends BaseEntity {

    private static final String ENTITY_PREFIX = "PERMISSION";

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private Droit droit;

    private Boolean hasDroit = false;

    @Override
    public String getEntityPrefixe() {
        return ENTITY_PREFIX;
    }
}