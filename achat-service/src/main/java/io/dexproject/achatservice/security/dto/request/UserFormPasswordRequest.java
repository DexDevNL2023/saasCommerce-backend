package io.dexproject.achatservice.security.dto.request;

import lombok.Value;

@Value
public class UserFormPasswordRequest {
    private String lastPassword;
    private String newPassword;
}
