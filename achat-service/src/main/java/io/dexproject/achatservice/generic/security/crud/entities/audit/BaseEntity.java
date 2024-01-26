package io.dexproject.achatservice.generic.security.crud.entities.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdBy", "createdAt", "updatedBy", "updatedAt"},
        allowGetters = true
)
public abstract class BaseEntity implements GenericEntity, Serializable {
    @Serial
    private static final long serialVersionUID = -8551160985498051566L;
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
	@Column(unique = true, nullable = false, updatable = false)
	protected String numEnrg;
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
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getNumEnrg() {
		return numEnrg;
	}

	@Override
	public void setNumEnrg(String numEnrg) {
		this.numEnrg = numEnrg;
	}
}
