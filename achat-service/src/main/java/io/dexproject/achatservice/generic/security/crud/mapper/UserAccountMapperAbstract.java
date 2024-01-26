package io.dexproject.achatservice.generic.security.crud.mapper;

import io.dexproject.achatservice.generic.mapper.AbstractGenericMapper;
import io.dexproject.achatservice.generic.repository.GenericRepository;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.UserReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserRequest;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import org.mapstruct.Mapper;

@Mapper
public abstract class UserAccountMapperAbstract extends AbstractGenericMapper<UserRequest, UserReponse, UserAccount> {

    protected UserAccountMapperAbstract(GenericRepository<UserAccount> repository) {
        super(repository);
    }

    final ADto dto = Mappers.getMapper(AMapper.class).toDto(a);
    protected UserAccount newInstance() {
        return new UserAccount();
    }
}
