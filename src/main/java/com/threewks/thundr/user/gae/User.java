/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://3wks.github.io/thundr/
 * Copyright (C) 2014 3wks, <thundr@3wks.com.au>
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

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.search.SearchId;
import com.threewks.thundr.search.SearchIndex;
import com.threewks.thundr.user.Organisation;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.*;

@Entity(name = "thundrUser")
public class User implements com.threewks.thundr.user.User {
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
	@SearchId
	@SearchIndex
	protected String username;
	@Index
	@SearchIndex
	protected String email;
	@Index
	@SearchIndex
	protected Set<String> roles = new LinkedHashSet<>();

	protected String hashedPassword;
	protected byte[] salt;
	protected Long lastLogin;
	protected Long createdAt;
	protected Map<String, String> props = new HashMap<>();

	protected Ref<Organisation> organisation;

	protected User() {

	}

	public User(String username) {
		this.username = StringUtils.trimToEmpty(username);
		this.createdAt = new DateTime().getMillis();
	}

	public User(String username, Organisation organisation) {

	}

	@Override
	public Organisation getOrganistion() {
		if (organisation == null) {
			return null;
		}

		return organisation.get();
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	public User withUsername(String username) {
		this.username = username;
		return this;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@SearchIndex
	public String getEmailUser() {
		return StringUtils.substringBefore(email, "@");
	}

	@SearchIndex
	public String getEmailDomain() {
		return StringUtils.substringAfter(email, "@");
	}

	public User withEmail(String email) {
		this.email = email;
		return this;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	@Override
	public Map<String, String> getProperties() {
		return props;
	}

	@Override
	public String getProperty(String property) {
		return props.get(property);
	}

	@Override
	public void setProperty(String key, String value) {
		props.put(key, value);
	}

	@Override
	public void removeProperty(String key) {
		props.remove(key);
	}

	public User withProperty(String key, String value) {
		props.put(key, value);
		return this;
	}

	@Override
	@SearchIndex
	public DateTime getCreated() {
		return createdAt == null ? null : new DateTime(createdAt);
	}

	@Override
	public DateTime getLastLogin() {
		return lastLogin == null ? null : new DateTime(lastLogin);
	}

	@Override
	public void setLastLogin(DateTime lastLogin) {
		this.lastLogin = lastLogin.getMillis();
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	@Override
	public void setRoles(Collection<String> roles) {
		this.roles = new LinkedHashSet<>(roles);
	}

	@Override
	public boolean hasRole(String role) {
		return roles.contains(role);
	}

	@Override
	public boolean hasRoles(String... roles) {
		return hasRoles(Arrays.asList(roles));
	}

	@Override
	public boolean hasRoles(Collection<String> roles) {
		return this.roles.containsAll(roles);
	}

	@Override
	public void addRole(String role) {
		this.roles.add(role);
	}

	@Override
	public void removeRole(String role) {
		this.roles.remove(role);
	}
}
