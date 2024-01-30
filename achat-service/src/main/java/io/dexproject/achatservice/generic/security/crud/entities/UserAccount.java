package io.dexproject.achatservice.generic.security.crud.entities;

import io.dexproject.achatservice.generic.security.oauth2.users.OAuth2UserInfo;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "utilisateurs")
public class UserAccount implements OAuth2User, OidcUser, Serializable {

    @Serial
    private static final long serialVersionUID = -8551160985498051566L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Transient
    private OidcIdToken idToken;

    @Transient
    private OidcUserInfo userInfo;

    @Transient
    private String displayName;

    private String lastName;

    private String firstName;

    @Transient
    private String emailOrPhone;

    private String email;

    private String phone;

    private String adresse;

    private String password;

    private String resetPasswordToken;

    private boolean actived;

    private boolean connected;

    private String accesToken;

    private boolean usingQr;

    @Column(unique = true, nullable = false)
    private String loginUrl;

    private String langKey;

    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    public void addAuthorities(Role authority) {
        this.roles.add(authority);
    }

    @Transient
    private Map<String, Object> attributes;

    public UserAccount(String username, String email, String password, String phone, String adresse, Boolean actived) {
        this.displayName = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.adresse = adresse;
        this.actived = actived;
    }

    public UserAccount(String name, String email, OidcIdToken idToken2, OidcUserInfo userInfo2) {
        this.displayName = name;
        this.email = email;
        this.idToken = idToken2;
        this.userInfo = userInfo2;
    }

    public static UserAccount create(OAuth2UserInfo user, OidcIdToken idToken, OidcUserInfo userInfo) {
        UserAccount localUser = new UserAccount(user.getName(), user.getEmail(), idToken, userInfo);
        localUser.setAttributes(user.getAttributes());
        return localUser;
    }

    public String getDisplayName() {
        return this.displayName = !this.firstName.isEmpty() ? this.lastName + " " + this.firstName : this.lastName;
    }

    public String getEmailOrPhone() {
        return this.emailOrPhone = this.email.isEmpty() ? this.phone : this.email;
    }

    @Override
    public String getName() {
        return this.getDisplayName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.idToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return GenericUtils.buildSimpleGrantedAuthorities(this.getRoles());
    }
}