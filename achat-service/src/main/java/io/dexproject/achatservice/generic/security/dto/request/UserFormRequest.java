package io.dexproject.achatservice.generic.security.dto.request;

import lombok.Value;

@Value
public class UserFormRequest {
    private String displayName;
    private String email;
    private String phone;
    private String adresse;
    private boolean actived;
	private boolean usingQr;
	private String rolename;
}
