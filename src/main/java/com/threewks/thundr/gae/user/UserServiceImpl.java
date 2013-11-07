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

import static com.atomicleopard.expressive.Expressive.list;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeConstants;

import com.atomicleopard.expressive.EList;
import com.atomicleopard.expressive.Expressive;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.VoidWork;
import com.threewks.thundr.configuration.Environment;
import com.threewks.thundr.gae.user.User.Fields;
import com.threewks.thundr.search.google.SearchRequest;
import com.threewks.thundr.search.google.SearchService;

public class UserServiceImpl implements UserService {
	private static final int SixtyDaysInSeconds = DateTimeConstants.SECONDS_PER_DAY * 60;
	private SearchService searchService;

	public UserServiceImpl(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	public User get(String username) {
		return ofy().load().type(User.class).filter(Fields.Username, username).first().now();
	}

	@Override
	public User put(User user) {
		ofy().save().entity(user).now();
		searchService.index(user, user.getUsername(), user.getFieldsToIndex()).complete();
		return user;
	}

	@Override
	public boolean delete(String username) {
		User user = get(username);
		if (user != null) {
			ofy().delete().entity(user).now();
			searchService.remove(User.class, list(username));
		}
		return user != null;
	}

	@Override
	public boolean login(String username, String password, HttpServletResponse resp) {
		User user = get(username);
		if (user != null && user.passwordMatches(password)) {
			return loginInternal(resp, user);
		}
		clearAuthCookies(resp);
		return false;
	}

	@Override
	public boolean login(String username, HttpServletResponse resp) {
		User user = get(username);
		if (user != null) {
			return loginInternal(resp, user);
		}
		clearAuthCookies(resp);
		return false;
	}

	public void logout(User user, HttpServletResponse resp) {
		if (user != null) {
			expireTokens(user);
		}
		clearAuthCookies(resp);
	}

	@Override
	public void expireTokens(User user) {
		List<UserToken> tokens = ofy().load().type(UserToken.class).ancestor(user).list();
		ofy().delete().entities(tokens).now();
	}

	@Override
	public UserToken createToken(User user) {
		final UserToken token = new UserToken(user);
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				ofy().save().entity(token).now();
			}
		});
		return token;
	}

	@Override
	public User getUserFromRequest(HttpServletRequest req) {
		String cookieName = req.isSecure() ? SecureAuthCookie : InsecureAuthCookie;
		Cookie authCookie = getCookie(req, cookieName);
		String token = authCookie == null ? null : authCookie.getValue();
		return token == null ? null : getUserFromToken(token);
	}

	@Override
	public User getUserFromToken(String token) {
		Key<UserToken> key = Key.create(token);
		UserToken userToken = ofy().load().key(key).now();
		return userToken == null ? null : userToken.getUser();
	}

	@Override
	public List<User> search(String email, int limit) {
		SearchRequest<User> query = searchService.search(User.class);

		if (StringUtils.isNotBlank(email)) {
			query.field(User.Fields.Email).is(email);
		}
		query.order(User.Fields.Email).ascending();
		query.limit(limit);

		EList<String> ids = query.search().getSearchResultIds();
		return load(ids);
	}

	private List<User> load(EList<String> ids) {
		Map<String, User> results = ofy().load().type(User.class).ids(ids);
		return Expressive.Transformers.transformAllUsing(Expressive.Transformers.usingLookup(results)).from(ids);
	}

	private boolean loginInternal(HttpServletResponse resp, User user) {
		expireTokens(user);
		UserToken secureToken = createToken(user);
		UserToken insecureToken = createToken(user);
		storeAuthCookies(resp, secureToken, insecureToken);
		user.loggedIn();
		put(user);
		return true;
	}

	public static void storeAuthCookies(HttpServletResponse resp, UserToken secureToken, UserToken insecureToken) {
		Cookie secureCookie = cookie(SecureAuthCookie, secureToken.getToken(), secure(), SixtyDaysInSeconds);
		Cookie insecureCookie = cookie(InsecureAuthCookie, insecureToken.getToken(), false, SixtyDaysInSeconds);
		resp.addCookie(secureCookie);
		resp.addCookie(insecureCookie);
	}

	public static void clearAuthCookies(HttpServletResponse resp) {
		Cookie secureCookie = cookie(SecureAuthCookie, "", secure(), 0);
		Cookie insecureCookie = cookie(InsecureAuthCookie, "", false, 0);
		resp.addCookie(secureCookie);
		resp.addCookie(insecureCookie);
	}

	private static Cookie cookie(String name, String value, boolean secure, int durationSeconds) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(durationSeconds);
		cookie.setPath("/");
		cookie.setSecure(secure);
		return cookie;
	}

	private static boolean secure() {
		boolean secure = true;
		if ("dev".equals(Environment.get())) {
			secure = false;
		}
		return secure;
	}

	private static Cookie getCookie(HttpServletRequest req, String name) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	public static void registerObjectifyClasses(ObjectifyFactory objectifyFactory) {
		objectifyFactory.register(User.class);
		objectifyFactory.register(UserToken.class);
	}
}
