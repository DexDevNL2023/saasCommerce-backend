package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.ResourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.LoginRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormPasswordRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormRequest;
import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import io.dexproject.achatservice.generic.utils.AppConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('admin') or hasRole('partner') or hasRole('merchant') or hasRole('customer')")
@RestController
@RequestMapping("/api/users")
@RefreshScope
public class UserController {

	private final UserAccountService userAccountService;

	public UserController(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
    }

    @GetMapping("/me")
	public ResponseEntity<ResourceResponse> getCurrentUser() {
		return new ResponseEntity<>(new ResourceResponse("Utilisateur trouvé avec succès!", userAccountService.loadCurrentUser()), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<ResourceResponse> logoutUser(@NotEmpty @Valid @RequestBody LoginRequest loginRequest) {
		userAccountService.logoutUser(loginRequest);
		return new ResponseEntity<>(new ResourceResponse("Déconnexion de l'utilisateur réussie!"), HttpStatus.OK);
	}

	@Secured({"admin", "partner"})
	@PostMapping("/create")
	public ResponseEntity<ResourceResponse> createUser(@NotEmpty @Valid @RequestBody UserFormRequest userFormRequest) {
		return new ResponseEntity<>(new ResourceResponse("Utilisateur créé avec succès!", userAccountService.createUser(userFormRequest)), HttpStatus.OK);
	}

	@PostMapping("/update")
	public ResponseEntity<ResourceResponse> editUser(@NotEmpty @Valid @RequestBody UserFormRequest userFormRequest) {
		return new ResponseEntity<>(new ResourceResponse("L'utilisateur a été mis à jour avec succès!", userAccountService.editUser(userFormRequest)), HttpStatus.OK);
	}

	@PostMapping("/edit/password")
	public ResponseEntity<ResourceResponse> editPassword(@NotEmpty @Valid @RequestBody UserFormPasswordRequest userFormPasswordRequest) {
		return new ResponseEntity<>(new ResourceResponse("Mot de passe utilisateur mis à jour avec succès!", userAccountService.editPassword(userFormPasswordRequest)), HttpStatus.OK);
	}

	@Secured({"admin", "partner"})
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResourceResponse> deleteUser(@NotEmpty @PathVariable("id") Long id) {
		userAccountService.deleteUserById(id);
		return new ResponseEntity<>(new ResourceResponse("Utilisateur supprimé avec succès!"), HttpStatus.OK);
	}

	@Secured("admin")
	@DeleteMapping("/suspend/{id}")
	public ResponseEntity<ResourceResponse> suspendUser(@NotEmpty @PathVariable("id") Long id) {
		return new ResponseEntity<>(new ResourceResponse("Utilisateur suspendu avec succès!", userAccountService.suspendUserById(id)), HttpStatus.OK);
	}

	@Secured({"admin", "partner", "merchant"})
	@GetMapping("/get/{id}")
	public ResponseEntity<ResourceResponse> findUser(@NotEmpty @PathVariable("id") Long id) {
		return new ResponseEntity<>(new ResourceResponse("Utilisateur trouvé avec succès!", userAccountService.findUserById(id)), HttpStatus.OK);
	}

	@Secured({"admin", "partner", "merchant"})
	@GetMapping("/get/all")
	public ResponseEntity<ResourceResponse> getAllUsers() {
		return new ResponseEntity<>(new ResourceResponse("Utilisateur trouvé avec succès!!", userAccountService.getAllUsers()), HttpStatus.OK);
	}

	@Secured({"admin", "partner", "merchant"})
	@GetMapping("/get/page")
	public ResponseEntity<ResourceResponse> getAllUsersByPage(@RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
															   @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page) {
		return new ResponseEntity<>(new ResourceResponse("Utilisateur trouvé avec succès!", userAccountService.getUsersByPage(page, size)), HttpStatus.OK);
	}

	@Secured({"admin", "partner", "merchant"})
	@GetMapping("/search/by")
	public ResponseEntity<ResourceResponse> searchUser(@RequestParam(name = "motCle", defaultValue = "") String motCle) {
		return new ResponseEntity<>(new ResourceResponse("Utilisateur trouvé avec succès!", userAccountService.search(motCle)), HttpStatus.OK);
	}
}