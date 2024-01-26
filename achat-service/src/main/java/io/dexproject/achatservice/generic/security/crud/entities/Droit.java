package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "droits_utilisateur")
public class Droit extends BaseEntity {

	private static final String ENTITY_PREFIX = "DROIT";

	@Column(nullable = false, unique = true)
	private String key;

	@Column(nullable = false)
	private String libelle;

	@Column(nullable = false)
	private String verbe;

	private String description;

	private Boolean isDefault = false;

	@ManyToOne(fetch = FetchType.LAZY)
	private Module module;

	@Override
	public String getEntityPrefixe() {
		return ENTITY_PREFIX;
	}
}
