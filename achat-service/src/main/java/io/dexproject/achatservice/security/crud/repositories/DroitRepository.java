package io.dexproject.achatservice.security.crud.repositories;

import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.security.crud.dto.request.DroitRequest;
import io.dexproject.achatservice.security.crud.entities.Droit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroitRepository extends GenericRepository<DroitRequest, Droit> {
    @Query("SELECT DISTINCT e FROM Droit e WHERE e.verbe = :verbe OR e.key = :key OR e.libelle = :libelle")
    Optional<Droit> findByVerbeAndKeyAndLibelle(String verbe, String key, String libelle);

    @Query("SELECT DISTINCT e FROM Droit e WHERE e.key = :key")
    Optional<Droit> findByKey(String key);

    @Query("SELECT DISTINCT e FROM Droit e WHERE e.libelle = :libelle")
    Optional<Droit> findByLibelle(String libelle);

    @Query("SELECT DISTINCT e FROM Droit e WHERE e.module = :module")
    List<Droit> findAllDroitsByModule(Module module);
}
