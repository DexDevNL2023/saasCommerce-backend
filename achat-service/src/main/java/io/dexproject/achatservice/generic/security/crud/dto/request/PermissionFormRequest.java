package io.dexproject.achatservice.generic.security.crud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionFormRequest {
    private Long permissionId;
    private Boolean hasPermission;
}
