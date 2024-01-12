package io.dexproject.achatservice.generic.filter.dao;

import io.dexproject.achatservice.generic.entity.BaseEntity;
import io.dexproject.achatservice.generic.filter.dto.FilterWrap;

import java.util.List;

/**
 * FilterRepo Interface.
 * Contains one single method used for filtering
 */
public interface FilterRepo {

    /**
     * Filter method to be used by the client
     *
     * @param filterWrap {@link FilterWrap}
     * @param clazz {@link Class}
     * @return {@link List<ENTITY>} -- List of Results from filtering
     * <br/>
     * throws {@link IllegalArgumentException}
     */
    <ENTITY extends BaseEntity> List<ENTITY> filter(FilterWrap filterWrap, Class<ENTITY> clazz);

    /**
     * Filter method to be used by the client
     *
     * @param filterWrap {@link FilterWrap}
     * @param clazz {@link Class}
     * @return {@link ENTITY} -- List of Results from filtering
     * <br/>
     * throws {@link IllegalArgumentException}
     */
    <ENTITY extends BaseEntity> ENTITY filterOne(FilterWrap filterWrap, Class<ENTITY> clazz);
}
