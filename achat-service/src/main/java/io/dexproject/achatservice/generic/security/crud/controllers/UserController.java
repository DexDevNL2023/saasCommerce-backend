package io.dexproject.achatservice.generic.security.crud.controllers;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@PreAuthorize("hasRole('admin') or hasRole('merchant') or hasRole('customer')")
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserAccountService userService;

	@GetMapping("/me")
	public ResponseEntity<ResourceResponse> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
			return new ResponseEntity<>(new ResourceResponse(false, "User not exist or not activeted!"), HttpStatus.OK);
		}

		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
		UserAccount profileAccount = userService.loadUserByUserEmail(userPrincipal.getUsername());
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", profileAccount), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<ResourceResponse> logoutUser(@NotEmpty @RequestBody LoginRequest loginRequest) {
		LoginDto loginUserDto = userService.loginUser(loginRequest);
		return new ResponseEntity<>(new ResourceResponse("User logout successfully!", loginUserDto), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@PostMapping("/create")
	public ResponseEntity<ResourceResponse> createUser(@NotEmpty @RequestBody UserFormRequest userFormRequest) {
		userService.createUser(userFormRequest);
		return new ResponseEntity<>(new ResourceResponse("User created successfully!"), HttpStatus.OK);
	}

	@PostMapping("/update")
	public ResponseEntity<ResourceResponse> editUser(@NotEmpty @RequestBody UserFormRequest userFormRequest) {
		userService.editUser(userFormRequest);
		return new ResponseEntity<>(new ResourceResponse("User updated successfully!"), HttpStatus.OK);
	}

	@PostMapping("/edit/password")
	public ResponseEntity<ResourceResponse> editPassword(@NotEmpty @RequestBody UserFormPasswordRequest userFormPasswordRequest) {
		userService.editPassword(userFormPasswordRequest);
		return new ResponseEntity<>(new ResourceResponse("User password updated successfully!"), HttpStatus.OK);
	}

	@Secured({"admin", "merchant"})
	@DeleteMapping("/delete/by/{id}")
	public ResponseEntity<ResourceResponse> deleteUser(@NotEmpty @PathVariable("id") Long id) {
		userService.deleteUserById(id);
		return new ResponseEntity<>(new ResourceResponse("User deleted successfully!"), HttpStatus.OK);
	}

	@Secured("customer")
	@DeleteMapping("/suspend/by/{id}")
	public ResponseEntity<ResourceResponse> suspendUser(@NotEmpty @PathVariable("id") Long id) {
		userService.suspendUserById(id);
		return new ResponseEntity<>(new ResourceResponse("User suspended successfully!"), HttpStatus.OK);
	}

	@GetMapping("/get/by/{id}")
	public ResponseEntity<ResourceResponse> findUser(@NotEmpty @PathVariable("id") Long id) {
		ProfileDto profileAccount = userService.findUserById(id);
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", profileAccount), HttpStatus.OK);
	}

	@Secured("admin")
	@GetMapping("/get/all")
	public ResponseEntity<ResourceResponse> getAllUsers(@RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
														@RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page) {
		PagedResponse<ProfileDto> profileList = userService.getListUsers(page, size);
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!!", profileList), HttpStatus.OK);
	}

	@GetMapping("/get/by/role")
	public ResponseEntity<ResourceResponse> getListUsersByRole(@RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
															   @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
															   @RequestParam(name = "roleCle", defaultValue = "") String roleCle) {
		PagedResponse<ProfileDto> profileList = userService.getListUsersByRole(roleCle, page, size);
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", profileList), HttpStatus.OK);
	}

	@GetMapping("/search/by")
	public ResponseEntity<ResourceResponse> getListUsersByUsernameAndRole(@RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
																		  @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
																		  @RequestParam(name = "motCle", defaultValue = "") String motCle,
																		  @RequestParam(name = "roleCle", defaultValue = "") String roleCle) {
		PagedResponse<ProfileDto> profileList = userService.getListUsersByNameAndRole(motCle, roleCle, page, size);
		return new ResponseEntity<>(new ResourceResponse("User finded successfully!", profileList), HttpStatus.OK);
	}
}

