package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "modules_application")
public class Module extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Override
    public void setNumOrder(String numberOrder) {
    }
}
