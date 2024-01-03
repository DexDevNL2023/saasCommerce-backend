package io.dexproject.achatservice.security.dto.reponse;

import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import lombok.Value;

@Value
public class LoginReponse extends BaseReponseDto {
	private String displayName;
    private String adresse;
	private String accesToken;
	private String rolename;
}
