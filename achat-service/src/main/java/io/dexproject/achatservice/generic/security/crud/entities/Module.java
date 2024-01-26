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
@Table(name = "modules_application")
public class Module extends BaseEntity {

    private static final String ENTITY_PREFIX = "MODULE";

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Override
    public String getEntityPrefixe() {
        return ENTITY_PREFIX;
    }
}
