package io.dexproject.achatservice.generic.security.crud.enums;

import io.dexproject.achatservice.generic.enums.impl.GenericEnumImpl;

public class RoleEnumImpl extends GenericEnumImpl<RoleName> implements RoleEnum {
    protected RoleEnumImpl(Class<RoleName> enumCls, String value) {
        super(enumCls, value);
    }
}
