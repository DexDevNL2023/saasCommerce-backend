package io.dexproject.achatservice.generic.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseRequestDto implements GenericEntity {
	private Long id;

	@Override
	public Long getId() {
		return id;
	}
}
