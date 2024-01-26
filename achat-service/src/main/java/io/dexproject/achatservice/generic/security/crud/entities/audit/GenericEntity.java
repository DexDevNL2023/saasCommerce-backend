package io.dexproject.achatservice.generic.security.crud.entities.audit;

public interface GenericEntity {
    void setId(Long id);
    Long getId();

    String getNumEnrg();

    void setNumEnrg(String numEnrg);
    String getEntityPrefixe();
}
