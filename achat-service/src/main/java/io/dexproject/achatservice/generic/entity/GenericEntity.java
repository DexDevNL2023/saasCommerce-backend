package io.dexproject.achatservice.generic.entity;

public interface GenericEntity<E> {
    // met à jour l'instance actuelle avec les données fournies
    void update(E source);

    boolean equalsToDto(E source);
    void setId(Long id);
    Long getId();
    String getNumEnrg();
    void setNumEnrg(String numEnrg);
    String getEntityName();
    String getModuleName();
}
