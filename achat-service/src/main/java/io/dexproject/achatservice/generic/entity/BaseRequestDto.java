package io.dexproject.achatservice.generic.entity;

import lombok.Data;

import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class BaseRequestDto implements GenericEntity<BaseRequestDto> {
	private Long id;

	@Override
	public Long getId() {
		return id;
	}
}
