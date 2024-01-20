package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.ResourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.LoginRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormPasswordRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormRequest;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import io.dexproject.achatservice.generic.utils.AppConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('admin') or hasRole('merchant') or hasRole('customer')")
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserAccountService userService;

    public UserController(UserAccountService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
	public ResponseEntity<ResourceResponse> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
			return new ResponseEntity<>(new ResourceResponse(false, "User not exist or not activeted!"), HttpStatus.OK);
		}
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
		UserAccount profileAccount = userService.loadUserByEmailOrPhone(userPrincipal.getUsername());
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", profileAccount), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<ResourceResponse> logoutUser(@NotEmpty @Valid @RequestBody LoginRequest loginRequest) {
		userService.logoutUser(loginRequest);
		return new ResponseEntity<>(new ResourceResponse("User logout successfully!"), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@PostMapping("/create")
	public ResponseEntity<ResourceResponse> createUser(@NotEmpty @Valid @RequestBody UserFormRequest userFormRequest) {
		return new ResponseEntity<>(new ResourceResponse("User created successfully!", userService.createUser(userFormRequest)), HttpStatus.OK);
	}

	@PostMapping("/update")
	public ResponseEntity<ResourceResponse> editUser(@NotEmpty @Valid @RequestBody UserFormRequest userFormRequest) {
		return new ResponseEntity<>(new ResourceResponse("User updated successfully!", userService.editUser(userFormRequest)), HttpStatus.OK);
	}

	@PostMapping("/edit/password")
	public ResponseEntity<ResourceResponse> editPassword(@NotEmpty @Valid @RequestBody UserFormPasswordRequest userFormPasswordRequest) {
		return new ResponseEntity<>(new ResourceResponse("User password updated successfully!", userService.editPassword(userFormPasswordRequest)), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResourceResponse> deleteUser(@NotEmpty @PathVariable("id") Long id) {
		userService.deleteUserById(id);
		return new ResponseEntity<>(new ResourceResponse("User deleted successfully!"), HttpStatus.OK);
	}

	@Secured("admin")
	@DeleteMapping("/suspend/{id}")
	public ResponseEntity<ResourceResponse> suspendUser(@NotEmpty @PathVariable("id") Long id) {
		return new ResponseEntity<>(new ResourceResponse("User suspended successfully!", userService.suspendUserById(id)), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@GetMapping("/get/{id}")
	public ResponseEntity<ResourceResponse> findUser(@NotEmpty @PathVariable("id") Long id) {
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", userService.findUserById(id)), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@GetMapping("/get/all")
	public ResponseEntity<ResourceResponse> getAllUsers() {
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!!", userService.getAllUsers()), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@GetMapping("/get/page")
	public ResponseEntity<ResourceResponse> getAllUsersByPage(@RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
															   @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page) {
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", userService.getUsersByPage(page, size)), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@GetMapping("/search/by")
	public ResponseEntity<ResourceResponse> searchUser(@RequestParam(name = "motCle", defaultValue = "") String motCle) {
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", userService.search(motCle)), HttpStatus.OK);
	}
}