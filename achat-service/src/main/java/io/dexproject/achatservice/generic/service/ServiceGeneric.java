package io.dexproject.achatservice.generic.service;

import io.dexproject.achatservice.generic.dto.reponse.BaseReponse;
import io.dexproject.achatservice.generic.dto.request.BaseRequest;
import io.dexproject.achatservice.generic.entity.audit.BaseEntity;
import io.dexproject.achatservice.exceptions.RessourceNotFoundException;
import io.dexproject.achatservice.exceptions.SuppressionException;
import io.dexproject.achatservice.generic.dto.reponse.PagedResponse;
import io.dexproject.achatservice.validators.unique.FieldValueExists;
import org.apache.lucene.index.IndexNotFoundException;

import java.util.List;

public interface ServiceGeneric<D extends BaseRequest, R extends BaseReponse, E extends BaseEntity<E, D>> extends FieldValueExists {
    List<R> search(String text, List<String> fields, int limit) throws RessourceNotFoundException;
    R save(D dto) throws RessourceNotFoundException;
    List<R> saveAll(List<D> dtos) throws RessourceNotFoundException;
    void delete(Long id) throws SuppressionException;
    void deleteAll(List<Long> ids) throws SuppressionException;
    Boolean exist(Long id) throws RessourceNotFoundException;
    R getOne(Long id) throws RessourceNotFoundException;
    E getById(Long id) throws RessourceNotFoundException;
    List<R> getAll() throws RessourceNotFoundException;
    List<R> getAll(List<Long> ids) throws RessourceNotFoundException;
    PagedResponse<R> getAllByPage(Integer page, Integer size) throws RessourceNotFoundException;
    R update(D dto, Long id) throws RessourceNotFoundException;
    void reIndex() throws IndexNotFoundException;
}
