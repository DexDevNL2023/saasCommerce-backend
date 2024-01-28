package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.RessourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.DroitAddRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.LoginRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormPasswordRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserRequest;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.services.AuthorizationService;
import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import io.dexproject.achatservice.generic.utils.AppConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('admin') or hasRole('partner') or hasRole('merchant') or hasRole('customer')")
@RefreshScope
@RestController
@CrossOrigin("*")
@RequestMapping("/api/users")
public class UserController {

	private static final String MODULE_NAME = "UTILISATEURS";

	private final UserAccountService userAccountService;
	private final AuthorizationService authorizationService;

	public UserController(UserAccountService userAccountService, AuthorizationService authorizationService) {
		this.userAccountService = userAccountService;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/me")
	public ResponseEntity<RessourceResponse> getCurrentUser() {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher les details sur l'utilisateur courant", MODULE_NAME+"-GET-CURRENT-USER", "GET", true));
		return new ResponseEntity<>(new RessourceResponse("Utilisateur trouvé avec succès!", userAccountService.loadCurrentUser()), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<RessourceResponse> logoutUser(@NotEmpty @Valid @RequestBody LoginRequest loginRequest) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Deconnecter un utilisateur", MODULE_NAME+"-LOGOUT-USER", "POST", false));
		userAccountService.logoutUser(loginRequest);
		return new ResponseEntity<>(new RessourceResponse("Déconnexion de l'utilisateur réussie!"), HttpStatus.OK);
	}

	@PostMapping("/create")
    public ResponseEntity<RessourceResponse> createUser(@NotEmpty @Valid @RequestBody UserRequest userRequest) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Creer un compte utilisateur", MODULE_NAME+"-CREATE-USER-ACCOUNT", "POST", false));
        return new ResponseEntity<>(new RessourceResponse("Utilisateur créé avec succès!", userAccountService.createUser(userRequest)), HttpStatus.OK);
	}

	@PutMapping("/update")
    public ResponseEntity<RessourceResponse> editUser(@NotEmpty @Valid @RequestBody UserRequest userRequest) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Modifier un compte utilisateur", MODULE_NAME+"-UPDATE-USER-ACCOUNT", "PUT", false));
        return new ResponseEntity<>(new RessourceResponse("L'utilisateur a été mis à jour avec succès!", userAccountService.editUser(userRequest)), HttpStatus.OK);
	}

	@PutMapping("/edit/password")
	public ResponseEntity<RessourceResponse> editPassword(@NotEmpty @Valid @RequestBody UserFormPasswordRequest userFormPasswordRequest) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Modifier le mot de passe d'un compte utilisateur", MODULE_NAME+"-UPDATE-USER-PASSWORD", "PUT", false));
		return new ResponseEntity<>(new RessourceResponse("Mot de passe utilisateur mis à jour avec succès!", userAccountService.editPassword(userFormPasswordRequest)), HttpStatus.OK);
	}

	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<RessourceResponse> deleteUser(@NotNull @PathVariable("userId") Long userId) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Supprimer un compte utilisateur", MODULE_NAME+"-DELET-USER-ACCOUNT", "DELET", false));
		userAccountService.deleteUserById(userId);
		return new ResponseEntity<>(new RessourceResponse("Utilisateur supprimé avec succès!"), HttpStatus.OK);
	}

	@DeleteMapping("/suspend/{userId}")
	public ResponseEntity<RessourceResponse> suspendUser(@NotNull @PathVariable("userId") Long userId) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher les permissions d'un utilisateur", MODULE_NAME+"-GET-PERMISSION-USER", "GET", false));
		return new ResponseEntity<>(new RessourceResponse("Utilisateur suspendu avec succès!", userAccountService.suspendUserById(userId)), HttpStatus.OK);
	}

	@GetMapping("/get/{userId}")
	public ResponseEntity<RessourceResponse> findUser(@NotNull @PathVariable("userId") Long userId) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher les details sur un compte utilisateur", MODULE_NAME+"-GET-PERMISSION-USER", "GET", false));
		return new ResponseEntity<>(new RessourceResponse("Utilisateur trouvé avec succès!", userAccountService.findUserById(userId)), HttpStatus.OK);
	}

	@GetMapping("/get/all")
	public ResponseEntity<RessourceResponse> getAllUsers() {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher la liste des comptes utilisateurs", MODULE_NAME+"-GET-ALL-USER", "GET", false));
		return new ResponseEntity<>(new RessourceResponse("Utilisateur trouvé avec succès!!", userAccountService.getAllUsers()), HttpStatus.OK);
	}

	@GetMapping("/get/all/by/role/{roleName}")
	public ResponseEntity<RessourceResponse> getAllUsers(@NotNull @PathVariable("roleName") RoleName roleName) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher la liste des comptes utilisateurs", MODULE_NAME+"-GET-ALL-USER", "GET", false));
		return new ResponseEntity<>(new RessourceResponse("Utilisateur trouvé avec succès!!", userAccountService.getAllUsersByRole(roleName)), HttpStatus.OK);
	}

	@GetMapping("/get/page")
	public ResponseEntity<RessourceResponse> getAllUsersByPage(@RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
															   @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher la liste des comptes utilisateurs", MODULE_NAME+"-GET-ALL-USER", "GET", false));
		return new ResponseEntity<>(new RessourceResponse("Utilisateur trouvé avec succès!", userAccountService.getAllUsersByPage(page, size)), HttpStatus.OK);
	}

	@GetMapping("/search/{motCle}")
	public ResponseEntity<RessourceResponse> searchUser(@NotNull @PathVariable("motCle") String motCle) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Rechercher un utilisateur", MODULE_NAME+"-FIND-USER-ACCOUNT", "GET", false));
		return new ResponseEntity<>(new RessourceResponse("Utilisateur trouvé avec succès!", userAccountService.search(motCle)), HttpStatus.OK);
	}

	@PutMapping("/user-autorisations/{userId}")
	public ResponseEntity<RessourceResponse> getAllAutorisations(@NotNull @PathVariable("userId") Long userId) {
		authorizationService.checkIfHasDroit(new DroitAddRequest(MODULE_NAME, "Afficher les permissions d'un utilisateur", MODULE_NAME+"-GET-PERMISSION-USER", "GET", false));
		return new ResponseEntity<>(new RessourceResponse("Permissions trouvées avec succès!", userAccountService.getAutorisations(userId)), HttpStatus.OK);
	}
}