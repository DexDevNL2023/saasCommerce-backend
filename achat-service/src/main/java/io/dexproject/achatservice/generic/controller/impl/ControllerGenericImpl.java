package io.dexproject.achatservice.generic.controller.impl;

import io.dexproject.achatservice.generic.controller.ControllerGeneric;
import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.entity.BaseReponseDto;
import io.dexproject.achatservice.generic.entity.BaseRequestDto;
import io.dexproject.achatservice.generic.entity.SearchRequestDTO;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import io.dexproject.achatservice.generic.validators.AuthorizeUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
  @Operation(summary = "Search a entity by full text value")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Saved the entity", content = @Content),
          @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
          @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<List<R>> search(SearchRequestDTO dto) {
    log.info("Request for plant search received with data : " + dto);
    try {
      return new ResponseEntity(service.search(dto.getText(), dto.getFields(), dto.getLimit()), HttpStatus.OK);
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
  @Operation(summary = "Save a entity by list")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Saved the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<R> save(@Valid @RequestBody D dto) {
    try {
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
  @Operation(summary = "Save all a entity by list")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Saved all the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<List<R>> saveAll(@Valid @RequestBody List<D> dtos) {
    try {
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
  @Operation(summary = "Delete a entity by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Deleted the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
    try {
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
  @Operation(summary = "Delete all a entity by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Deleted all the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<String> deleteAll(@RequestBody List<Long> ids) {
    try {
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
  @Operation(summary = "Get a entity by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Found the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<R> getOne(@PathVariable("id") Long id) {
    try {
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
  @Operation(summary = "Get a entity by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Found the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<E> getById(@PathVariable("id") Long id) {
    try {
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
  @Operation(summary = "Get all a entity")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Found the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<List<R>> getAll(Class<E> clazz) {
    try {
      return new ResponseEntity(service.getAll(clazz), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur lors de la recherche!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @param pageable
   * @return Page<R>
   */
  @Override
  @AuthorizeUser
  @GetMapping("/page-query")
  @Operation(summary = "Get a entity by page")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Found the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<Page<R>> getByPage(Pageable pageable) {
    try {
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
  @Operation(summary = "Update a entity by its id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Updated the entity", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content) })
  public ResponseEntity<R> update(@Valid @RequestBody D dto, @PathVariable("id") Long id) {
    try {
      return new ResponseEntity(service.update(dto, id), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("Erreur de modification!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
