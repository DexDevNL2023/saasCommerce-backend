package io.dexproject.achatservice.security.crud.entities;

import io.dexproject.achatservice.generic.entity.audit.BaseEntity;
import io.dexproject.achatservice.security.crud.dto.request.ModuleRequest;
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
public class Module extends BaseEntity<Module, ModuleRequest> {

    private static final String ENTITY_NAME = "MODULE";

    private static final String MODULE_NAME = "AUTORISATIONS";

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Override
    public void update(Module source) {
        this.name = source.getName();
        this.description = source.getDescription();
    }

    @Override
    public boolean equalsToDto(ModuleRequest source) {
        if (source == null) {
            return false;
        }
        return name.equals(source.getName()) &&
                description.equals(source.getDescription());
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
