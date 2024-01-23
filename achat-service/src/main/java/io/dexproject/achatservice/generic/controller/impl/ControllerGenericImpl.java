package io.dexproject.achatservice.generic.controller.impl;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.generic.exceptions.InternalException;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponseDto;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.ResourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequestDto;
import io.dexproject.achatservice.generic.security.crud.dto.request.SearchRequestDTO;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.validators.AuthorizeUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ResponseBody
@Slf4j
@RefreshScope
public class ControllerGenericImpl<D extends BaseRequestDto, R extends BaseReponseDto, E extends BaseEntity> implements ControllerGeneric<D, R, E> {

  private final ServiceGeneric<D, R, E> service;

  public ControllerGenericImpl(ServiceGeneric<D, R, E> service) {
    this.service = service;
  }

  /**
   * @param dto
   * @return List<R>
   */
  @Override
  @AuthorizeUser
  @GetMapping("/search")
  @Operation(summary = "Rechercher une entité par valeur de texte intégral")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Liste d'entité retrouvée", content = @Content),
          @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
          @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> search(SearchRequestDTO dto) {
    try {
      log.info("Demande de recherche reçue avec les données : " + dto);
      return new ResponseEntity<>(new ResourceResponse("Recherche de donnée effectuée avec succès!", service.search(dto.getText(), dto.getFields(), dto.getLimit())), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de recherche de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param dto
   * @return R
   */
  @Override
  @AuthorizeUser
  @PostMapping
  @Operation(summary = "Enregistrer une entité")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité enregistrée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> save(@Valid @RequestBody D dto) {
    try {
      log.info("Demande de sauvegarde reçue avec les données : " + dto);
      return new ResponseEntity<>(new ResourceResponse("Enregistrement de donnée effectuée avec succès!", service.save(dto)), HttpStatus.CREATED);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de sauvegarde de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param dtos
   * @return List<R>
   */
  @Override
  @AuthorizeUser
  @PostMapping("/all")
  @Operation(summary = "Enregistrer toute une entité de la liste")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité enregistrée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> saveAll(@Valid @RequestBody List<D> dtos) {
    try {
      log.info("Demande de sauvegarde reçue avec les données : " + dtos);
      return new ResponseEntity<>(new ResourceResponse("Enregistrement de donnée effectuée avec succès!", service.saveAll(dtos)), HttpStatus.CREATED);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de sauvegarde de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param id
   * @return String
   */
  @Override
  @AuthorizeUser
  @DeleteMapping("/{id}")
  @Operation(summary = "Supprimer une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité supprimée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> deleteById(@PathVariable("id") Long id) {
    try {
      log.info("Demande de suppression reçue pour la donnée avec l'id : " + id);
      service.delete(id);
      return new ResponseEntity<>(new ResourceResponse("Suppression de donnée effectuée avec succès!"), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de suppression de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param ids
   * @return String
   */
  @Override
  @AuthorizeUser
  @Operation(summary = "Supprimer la liste d'entité par leur identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité supprimée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> deleteAll(@RequestBody List<Long> ids) {
    try {
      log.info("Demande de suppression reçue pour la donnée avec l'id : " + ids);
      service.deleteAll(ids);
      return new ResponseEntity<>(new ResourceResponse("Suppression de donnée effectuée avec succès!"), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de suppression de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param id
   * @return R
   */
  @Override
  @AuthorizeUser
  @GetMapping("/{id}")
  @Operation(summary = "Récupérer une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité trouvée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> getOne(@PathVariable("id") Long id) {
    try {
      log.info("Demande d'affichage reçue pour la donnée avec l'id : " + id);
      return new ResponseEntity<>(new ResourceResponse("Recupération de donnée effectuée avec succès!", service.getOne(id)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param id
   * @return E
   */
  @Override
  @AuthorizeUser
  @GetMapping("/{id}")
  @Operation(summary = "Récupérer une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité trouvée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> getById(@PathVariable("id") Long id) {
    try {
      log.info("Demande d'affichage reçue pour la donnée avec l'id : " + id);
      return new ResponseEntity<>(new ResourceResponse("Recupération de donnée effectuée avec succès!", service.getById(id)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @return List<R>
   */
  @Override
  @AuthorizeUser
  @GetMapping
  @Operation(summary = "Récupérer la liste d'entité par leur identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité trouvée", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> getAll(Boolean byPeriode) {
    try {
      log.info("Demande d'affichage reçue pour la liste de donnée par période : " + byPeriode);
      return new ResponseEntity<>(new ResourceResponse("Recupération de donnée effectuée avec succès!", service.getAll(byPeriode)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param page
   * @param size
   * @return PagedResponse<R>
   */
  @Override
  @AuthorizeUser
  @GetMapping("/page")
  @Operation(summary = "Récupérer la liste d'entité par page")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité trouvée", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> getByPage(@RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER)Integer page,
                                                    @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE)Integer size) {
    try {
      log.info("Demande d'affichage reçue pour la liste de donnée pour la page : " + page + ", nombre : " + size);
      return new ResponseEntity<>(new ResourceResponse("Recupération de donnée effectuée avec succès!", service.getByPage(page, size)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param dto
   * @param id
   * @return R
   */
  @Override
  @AuthorizeUser
  @PostMapping("/{id}")
  @Operation(summary = "Mettre à jour une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Etité mise à jour", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> update(@Valid @RequestBody D dto, @PathVariable("id") Long id) {
    try {
      log.info("Demande de mise à jour reçue avec les données : " + dto + " pour l'entité avec l'id : " + id);
      return new ResponseEntity<>(new ResourceResponse("Modification de donnée effectuée avec succès!", service.update(dto, id)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de modification de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   */
  @Override
  @AuthorizeUser
  @GetMapping
  @Operation(summary = "Reindex all a entity")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Found the entity", content = @Content),
          @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<ResourceResponse> reIndex() {
    try {
      log.info("Demande de reindexation des fichiers de données");
      service.reIndex();
      return new ResponseEntity<>(new ResourceResponse("Reindexation réussie!"), HttpStatus.OK);
    } catch (IndexNotFoundException e) {
      return new ResponseEntity(new ResourceResponse(false, "Erreur de reindexation de donnée.\n\n Cause : "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
