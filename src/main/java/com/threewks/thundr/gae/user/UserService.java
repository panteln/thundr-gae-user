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
package com.threewks.thundr.gae.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
	public static String SecureAuthCookie = "__userAuth";
	public static String InsecureAuthCookie = "__user";

	public User get(String username);

	public User put(User user);

	public boolean delete(String username);

	/** 
	 * Log in the given user with the supplied password
	 * 
	 * @param username
	 * @param password
	 * @param resp
	 * @return true if login was 
	 */
	public boolean login(String username, String password, HttpServletResponse resp);

	/**
	 * Log in the given user
	 * @param username
	 * @param resp
	 * @return true if login was successful
	 */
	public boolean login(String username, HttpServletResponse resp);

	public void logout(User user, HttpServletResponse resp);

	public void expireTokens(User user);

	public UserToken createToken(User user);

	public User getUserFromRequest(HttpServletRequest req);

	public User getUserFromToken(String token);

	public List<User> search(String email, int limit);
}