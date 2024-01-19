package io.dexproject.achatservice.generic.security.crud.entities.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractToken implements Serializable {
    @Serial
    private static final long serialVersionUID = -7551160985498051566L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne(targetEntity = UserAccount.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    @JsonIgnore
    private UserAccount user;
    private Date expiryDate;

    public AbstractToken(String token) {
        super();
        this.token = token;
        this.expiryDate = GenericUtils.calculateExpiryDate(AppConstants.EXPIRATION);
    }

    public AbstractToken(UserAccount user, String token) {
        super();
        this.token = token;
        this.user = user;
        this.expiryDate = GenericUtils.calculateExpiryDate(AppConstants.EXPIRATION);
    }

    public void updateToken(String token) {
        this.token = token;
        this.expiryDate = GenericUtils.calculateExpiryDate(AppConstants.EXPIRATION);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractToken other = (AbstractToken) obj;
        if (expiryDate == null) {
            if (other.expiryDate != null) {
                return false;
            }
        } else if (!expiryDate.equals(other.expiryDate)) {
            return false;
        }
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!token.equals(other.token)) {
            return false;
        }
        if (user == null) {
            return other.user == null;
        } else return user.equals(other.user);
    }

    @Override
    public String toString() {
		return "Token [String=" + token + "]" + "[Expires" + expiryDate + "]";
    }
}