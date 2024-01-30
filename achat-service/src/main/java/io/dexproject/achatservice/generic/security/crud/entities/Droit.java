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
public class Droit extends BaseEntity<Droit> {

    private static final String ENTITY_NAME = "DROIT";

    private static final String MODULE_NAME = "AUTORISATIONS";

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
	public void update(Droit source) {
		this.libelle = source.getLibelle();
		this.verbe = source.getVerbe();
		this.description = source.getDescription();
		this.isDefault = source.getIsDefault();
		this.module = source.getModule();
	}

	@Override
	public boolean equalsToDto(Droit source) {
		if (source == null) {
			return false;
		}
		return libelle.equals(source.getLibelle()) &&
				verbe.equals(source.getVerbe()) &&
				description.equals(source.getDescription()) &&
				isDefault.equals(source.getIsDefault()) &&
				module.getId().equals(source.getModule().getId());
	}

	@Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
	}
}
