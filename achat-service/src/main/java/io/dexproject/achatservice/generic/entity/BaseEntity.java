package io.dexproject.achatservice.generic.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdBy", "createdAt", "updatedBy", "updatedAt"},
        allowGetters = true
)
public abstract class BaseEntity implements GenericEntity, Serializable {

    @Serial
	private static final long serialVersionUID = 1L;

	@Transient
	private String entityName;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

	@CreatedBy
	@Column(name = "created_by", updatable = false)
	private String createdBy;
	
	@CreatedDate
	@Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedBy
	@Column(name = "updated_by")
	private String updatedBy;
	
    @LastModifiedDate
	@Column(name = "updated_at")
    private Instant updatedAt;

	@Override
	public Long getId() {
		return id;
	}
}
