package io.dexproject.achatservice.security.dto.reponse;

import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import lombok.Value;

@Value
public class UserReponse extends BaseReponseDto {
	private String displayName;
	private String email;
	private String phone;
    private String adresse;
	private Boolean actived;
	private Boolean connected;
	private String rolename;
}
