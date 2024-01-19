package io.dexproject.achatservice.generic.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseRequestDto {
	private Long id;
}
