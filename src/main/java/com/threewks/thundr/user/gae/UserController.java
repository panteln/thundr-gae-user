/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.threewks.thundr.user.gae;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.threewks.thundr.http.URLEncoder;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;
import com.threewks.thundr.view.redirect.RedirectView;

public class UserController {
	public static class Methods {
		public static final String Login = "login";
		public static final String Logout = "logout";
	}

	private UserService userService;
	private String loginPath;

	public UserController(UserService userService, String userLoginPath) {
		this.userService = userService;
		this.loginPath = userLoginPath;
	}

	public RedirectView login(String username, String password, String r, HttpServletResponse resp) {
		Logger.info("%s is attempting to login", username);

		if (StringUtils.isBlank(username)) {
			return redirectOnError(r, "u", "no");
		}
		if (StringUtils.isBlank(password)) {
			return redirectOnError(r, "p", "no");
		}

		User user = userService.login(new PasswordAuthentication(username, password), resp, password);
		if (user == null) {
			return redirectOnError(r, "l", "no");
		}
		return new RedirectView(StringUtils.isEmpty(r) ? "/" : r);
	}

	private RedirectView redirectOnError(String r, String field, String result) {
		return new RedirectView(String.format("%s?%s=%s&r=%s", loginPath, field, result, URLEncoder.encodeQueryComponent(r)));
	}

	public RedirectView logout(String r, HttpServletRequest req, HttpServletResponse resp) {
		User user = userService.getUserFromRequest(req);
		userService.logout(user, resp);
		return new RedirectView(r == null ? loginPath : r);
	}

	/*
	 * @RequireRole(Role.Admin)
	 * public HandlebarsView search(User user) {
	 * List<User> results = userService.search(null, null, 1000);
	 * 
	 * Map<String, Object> model = map();
	 * model.put("users", results);
	 * model.put("user", user);
	 * return new HandlebarsView(Views.Users, model);
	 * }
	 * 
	 * @RequireRole(Role.Admin)
	 * public HandlebarsView createUserGet() {
	 * Map<String, Object> model = map();
	 * return new HandlebarsView(Views.CreateUser, model);
	 * }
	 * 
	 * @RequireRole(Role.Admin)
	 * public JsonView deleteUser(String email, User user) {
	 * Map<String, Object> model = map();
	 * model.put("ok", userService.delete(email));
	 * auditService.audit(user, Action.DeleteUser, "Deleted user %s", email);
	 * return new JsonView(model);
	 * }
	 * 
	 * @RequireRole(Role.Admin)
	 * public JsonView updateRole(String email, Role role, User user) {
	 * User updateUser = userService.get(email);
	 * if (updateUser != null) {
	 * updateUser.updateRole(role);
	 * userService.put(updateUser);
	 * auditService.audit(user, Action.UpdateUser, "Updated user %s to role %s", email, role);
	 * }
	 * Map<String, Object> model = map();
	 * model.put("ok", updateUser != null);
	 * return new JsonView(model);
	 * }
	 * 
	 * @RequireRole(Role.Admin)
	 * public HandlebarsView createUserPost(String userEmail, String userPassword, Role role, User user) {
	 * Map<String, Object> model = map();
	 * List<String> errors = validateUser(userEmail, userPassword, role);
	 * if (Expressive.isNotEmpty(errors)) {
	 * model.put("errors", errors);
	 * model.put("email", userEmail);
	 * model.put("role", role);
	 * } else {
	 * User createdUser = new User(userEmail, userPassword, role);
	 * userService.put(createdUser);
	 * auditService.audit(user, Action.CreateUser, "Created user %s with role %s", userEmail, role);
	 * model.put("createdUser", createdUser);
	 * }
	 * return new HandlebarsView(Views.CreateUser, model);
	 * }
	 * 
	 * public StringView createUserSecretSquirrel(String email, String password, Role role) {
	 * User user = new User(email, password, role);
	 * userService.put(user);
	 * auditService.audit(null, Action.CreateUser, "Used super user rights to create user %s with role %s", email, role);
	 * return new StringView("OK!");
	 * }
	 * 
	 * @RequireRole(Role.Admin)
	 * public HandlebarsView auditGet(String email, Action action, Integer page) {
	 * Map<String, Object> model = map();
	 * page = page == null ? 1 : page;
	 * List<AuditEntry> entries = auditService.list(email, action, page, 100);
	 * model.put("entries", entries);
	 * model.put("email", email);
	 * model.put("action", action);
	 * model.put("page", page);
	 * model.put("next", page + 1);
	 * if (page > 1) {
	 * model.put("previous", page - 1);
	 * }
	 * model.put("actions", Action.values());
	 * return new HandlebarsView(Views.Audit, model);
	 * }
	 * 
	 * @RequireRole
	 * public HandlebarsView viewProfile(User user) {
	 * Profile profile = userService.getProfile(user);
	 * 
	 * Map<Event, EventResponse> eventResponses = new LinkedHashMap<Event, EventResponse>();
	 * for (Event event : Event.values()) {
	 * eventResponses.put(event, new EventResponse().withEvent(event));
	 * }
	 * if (profile != null) {
	 * List<EventResponse> eventResponses2 = profile.getEventResponses();
	 * for (EventResponse eventResponse : eventResponses2) {
	 * eventResponses.put(eventResponse.getEvent(), eventResponse);
	 * }
	 * }
	 * Map<String, Object> model = map();
	 * model.put("user", user);
	 * model.put("profile", profile);
	 * model.put("eventResponses", eventResponses.values());
	 * return new HandlebarsView(Views.Profile, model);
	 * }
	 * 
	 * @RequireRole
	 * public JsonView updateProfile(User user, String name, List<EventResponse> eventResponses) {
	 * try {
	 * Profile existingProfile = userService.getProfile(user);
	 * existingProfile = existingProfile == null ? new Profile(user) : existingProfile;
	 * existingProfile.setName(name);
	 * existingProfile.setEventResponses(eventResponses);
	 * userService.updateProfile(existingProfile);
	 * return new JsonView(new Result(true, "Your profile has been updated."));
	 * } catch (Exception e) {
	 * Logger.error("Failed to update the profile of %s: %s", user.getEmail(), e.getMessage());
	 * return new JsonView(new Result(false, "There was a problem updating your profile."));
	 * }
	 * }
	 * private static final Pattern EmailRegex = Pattern.compile("[a-zA-Z0-9\\.\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]{1,63}@[a-zA-Z0-9\\.\\-\\_]{1,200}\\.[a-zA-Z0-9\\-\\_]{1,5}");
	 * private static final Pattern PasswordRegex = Pattern.compile(".{6,40}");
	 * 
	 * private List<String> validateUser(String email, String password, Role role) {
	 * List<String> errors = new ArrayList<String>();
	 * if (!EmailRegex.matcher(email).matches()) {
	 * errors.add("Please check the email address");
	 * }
	 * if (!PasswordRegex.matcher(password).matches()) {
	 * errors.add("Please choose another password");
	 * }
	 * if (role == null) {
	 * errors.add("Please specify a user role");
	 * }
	 * return errors;
	 * }
	 */
}
