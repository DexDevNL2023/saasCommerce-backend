package io.dexproject.achatservice.generic.security.crud.dto.request;

import io.dexproject.achatservice.generic.dto.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest extends BaseRequest {
    private Long roleId;
    private Long droitId;
    private Boolean hasPermission = false;
}
