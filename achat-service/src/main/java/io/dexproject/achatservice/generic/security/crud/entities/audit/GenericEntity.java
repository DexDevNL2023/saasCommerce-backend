package io.dexproject.achatservice.generic.security.crud.entities.audit;

public interface GenericEntity {
    Long getId();
    void setNumOrder(String numberOrder);
    String getEntityPrefixe();
}
