package io.dexproject.achatservice.generic.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.AbstractGenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.UserReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserRequest;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import org.mapstruct.Mapper;

@Mapper
public abstract class UserAccountMapper extends AbstractGenericMapper<UserRequest, UserReponse, UserAccount> {

    protected UserAccountMapper(GenericRepository<UserAccount> repository) {
        super(repository);
    }

    protected UserAccount newInstance() {
        return new UserAccount();
    }
}
