package io.dexproject.achatservice.generic.entity;

import java.time.Instant;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseReponseDto implements GenericEntity<BaseReponseDto> {
	private Long id;
	private String createdBy;
    private Instant createdAt;
	private String updatedBy;
    private Instant updatedAt;

	@Override
	public Long getId() {
		return id;
	}
}
