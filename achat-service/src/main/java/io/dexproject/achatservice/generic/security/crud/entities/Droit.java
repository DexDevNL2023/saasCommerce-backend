package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "droits_utilisateur")
public class Droit extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String key;

	@Column(nullable = false)
	private String libelle;

	@Column(nullable = false)
	private String verbe;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	private Module module;

	@Override
	public void setNumOrder(String numberOrder) {
	}
}
