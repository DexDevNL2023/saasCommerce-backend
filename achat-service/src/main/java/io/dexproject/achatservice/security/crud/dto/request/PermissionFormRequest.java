package io.dexproject.achatservice.security.crud.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionFormRequest {
    private Long permissionId;
    private Boolean hasPermission;
}
