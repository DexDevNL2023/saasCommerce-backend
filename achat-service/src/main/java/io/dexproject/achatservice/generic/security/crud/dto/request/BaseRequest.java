package io.dexproject.achatservice.generic.security.crud.dto.request;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseRequest {
	private Long id;
}
