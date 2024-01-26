package io.dexproject.achatservice.generic.security.crud.dto.request;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseRequest {
	private Long id;
}
