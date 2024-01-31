package io.dexproject.achatservice.generic.enums.impl;

import io.dexproject.achatservice.generic.security.crud.enums.RoleName;

public class RoleEnumImpl extends GenericEnumImpl<RoleName> implements RoleEnum {
    protected RoleEnumImpl(Class<RoleName> enumCls, String value) {
        super(enumCls, value);
    }
}
