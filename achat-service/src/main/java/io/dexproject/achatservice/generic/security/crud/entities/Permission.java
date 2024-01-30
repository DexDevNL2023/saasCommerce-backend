package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.entity.audit.BaseEntity;
import io.dexproject.achatservice.generic.security.crud.dto.request.PermissionRequest;
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
public class Permission extends BaseEntity<Permission, PermissionRequest> {

    private static final String ENTITY_NAME = "PERMISSION";

    private static final String MODULE_NAME = "AUTORISATIONS";

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private Droit droit;

    private Boolean hasPermission;

    @Override
    public void update(Permission source) {
        this.role = source.getRole();
        this.droit = source.getDroit();
        this.hasPermission = source.getHasPermission();
    }

    @Override
    public boolean equalsToDto(PermissionRequest source) {
        if (source == null) {
            return false;
        }
        return role.getId().equals(source.getRoleId()) &&
                droit.getId().equals(source.getDroitId()) &&
                hasPermission.equals(source.getHasPermission());
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
