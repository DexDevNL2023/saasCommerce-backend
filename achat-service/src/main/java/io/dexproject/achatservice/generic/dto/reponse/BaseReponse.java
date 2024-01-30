package io.dexproject.achatservice.generic.dto.reponse;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.Instant;

@Data
@MappedSuperclass
public abstract class BaseReponse {
	private Long id;
	private String createdBy;
    private Instant createdAt;
	private String updatedBy;
    private Instant updatedAt;
}
