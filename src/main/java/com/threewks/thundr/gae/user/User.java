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

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.exception.BaseException;
import com.threewks.thundr.util.Encoder;

@Entity(name = "thundrUser")
public class User {
	private static final int HASH_ITERATIONS = 10;

	public static class Fields {
		public static final String Email = "email";
		public static final String Username = "username";
		public static final String Roles = "roles";
		public static final String Created = "created";
		public static final String LastLogin = "lastLogin";
		public static final String EmailUser = null;
		public static final String EmailDomain = null;
	}

	@Id
	@Index
	protected String username;
	@Index
	protected String email;
	@Index
	protected List<String> roles = new ArrayList<>();

	protected String hashedPassword;
	protected byte[] salt;
	protected Long lastLogin;
	protected Long createdAt;
	@EmbedMap
	protected Map<String, String> props = new HashMap<>();

	User() {

	}

	public User(String username) {
		this(username, null);
	}

	public User(String username, String password) {
		this.username = StringUtils.trimToEmpty(username);
		this.createdAt = new DateTime().getMillis();
		this.salt = randomise(8);
		this.hashedPassword = password == null ? null : hash(password);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User withUsername(String username) {
		this.username = username;
		return this;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public String getEmailUser() {
		return StringUtils.substringBefore(email, "@");
	}

	public String getEmailDomain() {
		return StringUtils.substringAfter(email, "@");
	}

	public User withEmail(String email) {
		this.email = email;
		return this;
	}

	public Map<String, String> getProperties() {
		return props;
	}

	public String getProperty(String property) {
		return props.get(property);
	}

	public void setProperty(String key, String value) {
		props.put(key, value);
	}

	public User removeProperty(String key) {
		props.remove(key);
		return this;
	}

	public User withProperty(String key, String value) {
		props.put(key, value);
		return this;
	}

	public DateTime getCreated() {
		return createdAt == null ? null : new DateTime(createdAt);
	}

	public DateTime getLastLogin() {
		return lastLogin == null ? null : new DateTime(lastLogin);
	}

	public void loggedIn() {
		this.lastLogin = new DateTime().getMillis();
	}

	public User updatePassword(String password) {
		this.hashedPassword = hash(password);
		return this;
	}

	public boolean passwordMatches(String passwordToMatch) {
		return StringUtils.equals(hashedPassword, hash(passwordToMatch));
	}

	protected String hash(String password) {
		if (password == null) {
			return null;
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.update(salt);
			byte[] input = digest.digest(password.getBytes("UTF-8"));
			for (int i = 0; i < HASH_ITERATIONS; i++) {
				digest.reset();
				input = digest.digest(input);
			}
			return new Encoder(input).base64().string();
		} catch (Exception e) {
			throw new BaseException(e, "Failed to hash password: %s", e.getMessage());
		}
	}

	protected byte[] randomise(int bytes) {
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(System.currentTimeMillis());
			byte[] data = new byte[bytes];
			random.nextBytes(data);
			return data;
		} catch (Exception e) {
			throw new BaseException(e, "Failed to generate salt: %s", e.getMessage());
		}
	}

	private static final List<String> DefaultFieldsToIndex = list(Fields.Username, Fields.Email, Fields.EmailUser, Fields.EmailDomain, Fields.Roles, Fields.Created);

	public Iterable<String> getFieldsToIndex() {
		return DefaultFieldsToIndex;
	}
}
