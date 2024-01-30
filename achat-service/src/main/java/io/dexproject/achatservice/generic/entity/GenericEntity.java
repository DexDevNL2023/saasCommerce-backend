package io.dexproject.achatservice.generic.entity;

public interface GenericEntity<E, D> {
    // met à jour l'instance actuelle avec les données fournies
    void update(E source);

    boolean equalsToDto(D source);
    void setId(Long id);
    Long getId();
    String getNumEnrg();
    void setNumEnrg(String numEnrg);
    String getEntityName();
    String getModuleName();
}
