package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.crud.entities.token.AbstractToken;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "tokens")
public class VerifyToken extends AbstractToken implements Serializable {

    public VerifyToken(String token) {
        super(token);
    }

    public VerifyToken(UserAccount user, String token) {
        super(user, token);
    }
}
