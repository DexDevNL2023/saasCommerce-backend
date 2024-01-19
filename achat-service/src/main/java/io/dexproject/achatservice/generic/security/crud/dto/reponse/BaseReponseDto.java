package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.Instant;

@Data
@MappedSuperclass
public abstract class BaseReponseDto {
	private Long id;
	private String createdBy;
    private Instant createdAt;
	private String updatedBy;
    private Instant updatedAt;
}
