package io.dexproject.achatservice.generic.controller.impl;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import io.dexproject.achatservice.generic.entity.SearchRequestDTO;
import io.dexproject.achatservice.generic.page.PagedResponse;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.generic.validators.AuthorizeUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ResponseBody
@Slf4j
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
  public ResponseEntity<List<R>> search(SearchRequestDTO dto) {
    try {
      log.info("Demande de recherche reçue avec les données : " + dto);
      return new ResponseEntity(service.search(dto.getText(), dto.getFields(), dto.getLimit()), HttpStatus.OK);
      return new ResponseEntity<>(new ResourceResponse("Payment create successfully!", payment), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur lors de la recherche!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<R> save(@Valid @RequestBody D dto) {
    try {
      log.info("Demande de sauvegarde reçue avec les données : " + dto);
      return new ResponseEntity(service.save(dto),HttpStatus.CREATED);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur de sauvegarde", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<List<R>> saveAll(@Valid @RequestBody List<D> dtos) {
    try {
      log.info("Demande de sauvegarde reçue avec les données : " + dtos);
      return new ResponseEntity(service.saveAll(dtos),HttpStatus.CREATED);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur de sauvegarde!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
    try {
      log.info("Demande de suppression reçue pour la donnée avec l'id : " + id);
      service.delete(id);
      return new ResponseEntity("Suppression avec succes!", HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur de suppression!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<String> deleteAll(@RequestBody List<Long> ids) {
    try {
      log.info("Demande de suppression reçue pour la donnée avec l'id : " + ids);
      service.deleteAll(ids);
      return new ResponseEntity("Suppression avec succes!", HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur de suppression!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<R> getOne(@PathVariable("id") Long id) {
    try {
      log.info("Demande d'affichage reçue pour la donnée avec l'id : " + id);
      return new ResponseEntity(service.getOne(id), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur lors de la recherche!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<E> getById(@PathVariable("id") Long id) {
    try {
      log.info("Demande d'affichage reçue pour la donnée avec l'id : " + id);
      return new ResponseEntity(service.getById(id), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur lors de la recherche!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<List<R>> getAll(Boolean byPeriode) {
    try {
      log.info("Demande d'affichage reçue pour la liste de donnée par période : " + byPeriode);
      return new ResponseEntity(service.getAll(byPeriode), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur lors de la recherche!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param pageable
   * @return PagedResponse<R>
   */
  @Override
  @AuthorizeUser
  @GetMapping("/page")
  @Operation(summary = "Récupérer la liste d'entité par page")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Liste d'entité trouvée", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entité introuvable", content = @Content) })
  public ResponseEntity<PagedResponse<R>> getByPage(Pageable pageable) {
    try {
      log.info("Demande d'affichage reçue pour la liste de donnée par page : " + pageable);
      return new ResponseEntity(service.getByPage(pageable), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur lors de la recherche!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<R> update(@Valid @RequestBody D dto, @PathVariable("id") Long id) {
    try {
      log.info("Demande de mise à jour reçue avec les données : " + dto + " pour l'entité avec l'id : " + id);
      return new ResponseEntity(service.update(dto, id), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur de modification!", HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<?> reIndex() {
    try {
      log.info("Demande de reindexation des fichiers de données");
      service.reIndex();
      return new ResponseEntity("Reindexation réussie!", HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur lors de la recherche!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
