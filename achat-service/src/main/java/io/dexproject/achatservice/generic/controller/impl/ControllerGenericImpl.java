package io.dexproject.achatservice.generic.controller.impl;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.generic.exceptions.InternalException;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitAddRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.SearchRequest;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ResponseBody
@Slf4j
@RefreshScope
public abstract class ControllerGenericImpl<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity> implements ControllerGeneric<D, R, E> {

  protected abstract E newInstance();
  private final ServiceGeneric<D, R, E> service;
  private final AuthorizationService authorizationService;

  protected ControllerGenericImpl(ServiceGeneric<D, R, E> service, AuthorizationService authorizationService) {
    this.service = service;
    this.authorizationService = authorizationService;
  }

  /**
   * @param dto
   * @return List<R>
   */
  @Override
  @GetMapping("/search")
  @Operation(summary = "Rechercher une entité par valeur de texte intégral")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Liste d'entité retrouvée", content = @Content),
          @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
          @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> search(@NotEmpty @Valid @RequestBody SearchRequest dto) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Rechercher un " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-SEARCH"), "GET", true));
      log.info("Demande de recherche reçue avec les données : " + dto);
      return new ResponseEntity<>(new RessourceResponse("Recherche de donnée effectuée avec succès!", service.search(dto.getText(), dto.getFields(), dto.getLimit())), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de recherche de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param dto
   * @return R
   */
  @Override
  @PostMapping
  @Operation(summary = "Enregistrer une entité")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité enregistrée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> save(@NotEmpty @Valid @RequestBody D dto) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Ajouter un " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-ADD"), "POST", true));
      log.info("Demande de sauvegarde reçue avec les données : " + dto);
      return new ResponseEntity<>(new RessourceResponse("Enregistrement de donnée effectuée avec succès!", service.save(dto)), HttpStatus.CREATED);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de sauvegarde de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param dtos
   * @return List<R>
   */
  @Override
  @PostMapping("/all")
  @Operation(summary = "Enregistrer toute une entité de la liste")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité enregistrée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> saveAll(@NotEmpty @RequestBody List<D> dtos) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Ajouter une liste de " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-ADD-LIST"), "POST", true));
      log.info("Demande de sauvegarde reçue avec les données : " + dtos);
      return new ResponseEntity<>(new RessourceResponse("Enregistrement de donnée effectuée avec succès!", service.saveAll(dtos)), HttpStatus.CREATED);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de sauvegarde de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param id
   * @return String
   */
  @Override
  @DeleteMapping("/{id}")
  @Operation(summary = "Supprimer une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité supprimée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> deleteById(@NotNull @PathVariable("id") Long id) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Supprimer un " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-DELET"), "DELET", false));
      log.info("Demande de suppression reçue pour la donnée avec l'id : " + id);
      service.delete(id);
      return new ResponseEntity<>(new RessourceResponse("Suppression de donnée effectuée avec succès!"), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de suppression de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param ids
   * @return String
   */
  @Override
  @Operation(summary = "Supprimer la liste d'entité par leur identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité supprimée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> deleteAll(@NotEmpty @RequestBody List<Long> ids) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Supprimer une liste de " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-DELET-LIST"), "DELET", false));
      log.info("Demande de suppression reçue pour la donnée avec l'id : " + ids);
      service.deleteAll(ids);
      return new ResponseEntity<>(new RessourceResponse("Suppression de donnée effectuée avec succès!"), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de suppression de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param id
   * @return R
   */
  @Override
  @GetMapping("/{id}")
  @Operation(summary = "Récupérer une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité trouvée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> getOne(@NotNull @PathVariable("id") Long id) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Afficher les details sur un " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-GET"), "GET", true));
      log.info("Demande d'affichage reçue pour la donnée avec l'id : " + id);
      return new ResponseEntity<>(new RessourceResponse("Recupération de donnée effectuée avec succès!", service.getOne(id)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param id
   * @return E
   */
  @Override
  @GetMapping("/{id}")
  @Operation(summary = "Récupérer une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entité trouvée", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> getById(@NotNull @PathVariable("id") Long id) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Afficher les details sur un " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-GET"), "GET", true));
      log.info("Demande d'affichage reçue pour la donnée avec l'id : " + id);
      return new ResponseEntity<>(new RessourceResponse("Recupération de donnée effectuée avec succès!", service.getById(id)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @return List<R>
   */
  @Override
  @GetMapping
  @Operation(summary = "Récupérer la liste de tous les entités")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité trouvée", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> getAll() {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Afficher la liste des " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"GET-ALL"), "GET", true));
      log.info("Demande d'affichage reçue pour la liste de donnée");
      return new ResponseEntity<>(new RessourceResponse("Recupération de donnée effectuée avec succès!", service.getAll()), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @return List<R>
   */
  @Override
  @GetMapping
  @Operation(summary = "Récupérer la liste d'entité par leur identifiant")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Liste d'entité trouvée", content = @Content),
          @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content)})
  public ResponseEntity<RessourceResponse> getAll(List<Long> ids) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Afficher la liste des " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"GET-ALL"), "GET", true));
      log.info("Demande d'affichage reçue pour la liste de donnée pour les identifients {} ", ids);
      return new ResponseEntity<>(new RessourceResponse("Recupération de donnée effectuée avec succès!", service.getAll(ids)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity(new RessourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param page
   * @param size
   * @return PagedResponse<R>
   */
  @Override
  @GetMapping("/page")
  @Operation(summary = "Récupérer la liste d'entité par page")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité trouvée", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> getByPage(@RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                     @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Afficher la liste des " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"GET-ALL"), "GET", true));
      log.info("Demande d'affichage reçue pour la liste de donnée pour la page : " + page + ", nombre : " + size);
      return new ResponseEntity<>(new RessourceResponse("Recupération de donnée effectuée avec succès!", service.getAllByPage(page, size)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de recupération de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param dto
   * @param id
   * @return R
   */
  @Override
  @PostMapping("/{id}")
  @Operation(summary = "Mettre à jour une entité par son identifiant")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Etité mise à jour", content = @Content),
    @ApiResponse(responseCode = "400", description = "Identifiant fourni non valide", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> update(@NotEmpty @Valid @RequestBody D dto, @PathVariable("id") Long id) {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Modifier un " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-UPDATE"), "PUT", false));
      log.info("Demande de mise à jour reçue avec les données : " + dto + " pour l'entité avec l'id : " + id);
      return new ResponseEntity<>(new RessourceResponse("Modification de donnée effectuée avec succès!", service.update(dto, id)), HttpStatus.OK);
    } catch (InternalException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de modification de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   */
  @Override
  @GetMapping
  @Operation(summary = "Reindex all a entity")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Found the entity", content = @Content),
          @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<RessourceResponse> reIndex() {
    try {
      E entity = newInstance();
      authorizationService.checkIfHasDroit(new DroitAddRequest(entity.getModuleName(), "Re-indexer un " + GenericUtils.camelOrSnakeToLabel(entity.getEntityName()), GenericUtils.camelOrSnakeToKey(entity.getEntityName()+"-REINDEX"), "GET", true));
      log.info("Demande de reindexation des fichiers de données");
      service.reIndex();
      return new ResponseEntity<>(new RessourceResponse("Reindexation réussie!"), HttpStatus.OK);
    } catch (IndexNotFoundException e) {
      return new ResponseEntity<>(new RessourceResponse(false, "Erreur de reindexation de donnée.\n\n Cause : " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
