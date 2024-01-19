package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.crud.entities.token.AbstractToken;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@Entity
@Table(name = "tokens")
public class VerifyToken extends AbstractToken implements Serializable {
    public VerifyToken() {
        super();
    }

    public VerifyToken(String token) {
        super(token);
    }

    public VerifyToken(UserAccount user, String token) {
        super(user, token);
    }
}
