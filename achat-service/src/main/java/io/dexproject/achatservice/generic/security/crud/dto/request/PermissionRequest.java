package io.dexproject.achatservice.generic.security.crud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionRequest extends BaseRequest {
    private Long roleId;
    private Long droitId;
    private Boolean hasPermission;
}
