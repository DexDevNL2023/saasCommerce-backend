package io.dexproject.achatservice.generic.security.crud.dto.reponse;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseReponse {
	private Long id;
	private String createdBy;
    private Instant createdAt;
	private String updatedBy;
    private Instant updatedAt;
}
