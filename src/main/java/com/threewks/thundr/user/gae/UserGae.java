/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://3wks.github.io/thundr/
 * Copyright (C) 2015 3wks, <thundr@3wks.com.au>
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.atomicleopard.expressive.Expressive;
import com.atomicleopard.expressive.transform.CollectionTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.gae.objectify.Refs;
import com.threewks.thundr.search.SearchId;
import com.threewks.thundr.search.SearchIndex;
import com.threewks.thundr.user.Organisation;
import com.threewks.thundr.user.Roles;

@Entity(name = "thundrUser")
public class UserGae implements com.threewks.thundr.user.User {
	public static class Fields {
		public static final String Email = "email";
		public static final String Username = "username";
		public static final String Roles = "roles";
		public static final String Created = "created";
		public static final String LastLogin = "lastLogin";
		public static final String EmailUser = null;
		public static final String EmailDomain = null;
	}

	public static final CollectionTransformer<UserGae, String> ToNames = Expressive.Transformers
			.transformAllUsing(Expressive.Transformers.<UserGae, String> toProperty(Fields.Username, UserGae.class));

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

	protected Long lastLogin;
	protected Long createdAt;
	protected Map<String, String> props = new HashMap<>();
	protected Ref<Organisation> organisation;
	protected Map<Key<AccountGae>, Set<String>> accounts = new HashMap<>();

	protected UserGae() {

	}

	public UserGae(String username) {
		this(username, null);
	}

	public UserGae(String username, Organisation organisation) {
		this.username = StringUtils.trimToEmpty(username);
		this.organisation = Refs.ref(organisation);
		this.createdAt = new DateTime().getMillis();
	}

	public Organisation getOrganistion() {
		return Refs.unref(organisation);
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = Refs.ref(organisation);
	}

	public UserGae withOrganisation(Organisation organisation) {
		this.setOrganisation(organisation);
		return this;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	public UserGae withUsername(String username) {
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

	public UserGae withEmail(String email) {
		this.email = email;
		return this;
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

	public UserGae withProperty(String key, String value) {
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
	public Roles getRoles() {
		return new Roles(roles);
	}

	@Override
	public void setRoles(Roles roles) {
		this.roles = roles.getRoles();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserGae other = (UserGae) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public UserGae withRoles(Roles roles) {
		this.setRoles(roles);
		return this;
	}

	public void addAccount(AccountGae account) {
		Key<AccountGae> ref = Refs.key(account);
		if (!accounts.containsKey(ref)) {
			accounts.put(ref, Collections.singleton(Roles.Member));
		}
	}

	public void removeAccount(AccountGae account) {
		Ref<AccountGae> ref = Refs.ref(account);
		accounts.remove(ref);
	}

	public Collection<AccountGae> getAccounts() {
		Set<Key<AccountGae>> keySet = accounts.keySet();
		return Refs.unkey(keySet);
	}

	public Roles getRoles(AccountGae account) {
		Ref<AccountGae> ref = Refs.ref(account);
		Set<String> set = accounts.get(ref);
		return new Roles(set);
	}

	public Map<AccountGae, Roles> getAllRoles() {
		Map<AccountGae, Roles> result = new LinkedHashMap<>();
		for (Map.Entry<Key<AccountGae>, Set<String>> entry : this.accounts.entrySet()) {
			result.put(Refs.unkey(entry.getKey()), new Roles(entry.getValue()));
		}
		result.remove(null);
		return result;
	}

	public void setRoles(AccountGae accountGae, Roles roles) {
		Set<String> set = roles.getRoles();
		this.accounts.put(Refs.key(accountGae), set);
	}

	public UserGae withRoles(AccountGae accountGae, Roles roles) {
		this.setRoles(accountGae, roles);
		return this;
	}

}
