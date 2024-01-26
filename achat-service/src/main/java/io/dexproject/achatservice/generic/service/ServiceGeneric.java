package io.dexproject.achatservice.generic.service;

import io.dexproject.achatservice.generic.exceptions.RessourceNotFoundException;
import io.dexproject.achatservice.generic.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PagedResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitAddRequest;
import io.dexproject.achatservice.generic.security.crud.entities.audit.BaseEntity;
import io.dexproject.achatservice.generic.validators.FieldValueExists;
import org.apache.lucene.index.IndexNotFoundException;

import java.util.List;

public interface ServiceGeneric<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity> extends FieldValueExists {
    List<R> search(String text, List<String> fields, int limit) throws RessourceNotFoundException;
    R save(D dto) throws RessourceNotFoundException;
    List<R> saveAll(List<D> dtos) throws RessourceNotFoundException;
    void delete(Long id) throws SuppressionException;
    void deleteAll(List<Long> ids) throws SuppressionException;
    Boolean exist(Long id) throws RessourceNotFoundException;
    R getOne(Long id) throws RessourceNotFoundException;
    E getById(Long id) throws RessourceNotFoundException;
    List<R> getAll(Boolean byPeriode) throws RessourceNotFoundException;

    List<R> getAll(List<Long> ids) throws RessourceNotFoundException;
    PagedResponse<R> getByPage(Integer page, Integer size) throws RessourceNotFoundException;
    R update(D dto, Long id) throws RessourceNotFoundException;
    Boolean equalsToDto(D dto, Long id) throws RessourceNotFoundException;
    void reIndex() throws IndexNotFoundException;
    String getEntityName();
    String getEntityLabel();
    String getModuleName();
    void addDroit(DroitAddRequest post);
    String getEntityKey(String key);
}
