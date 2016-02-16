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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.gae.objectify.Refs;
import com.threewks.thundr.search.SearchIndex;
import com.threewks.thundr.user.Account;

@Entity(name = "thundrAccount")
public class AccountGae implements Account {

	@Id
	protected String id;

	@SearchIndex
	@Index
	protected String name;

	@Index
	@SearchIndex
	protected UUID uuid;

	@Index
	@SearchIndex
	protected Set<String> usernames = new LinkedHashSet<>();

	protected String description;

	@Index
	protected Ref<OrganisationGae> organisation;

	protected AccountGae() {

	}

	public AccountGae(String name) {
		this(name, null);
	}

	public AccountGae(String name, OrganisationGae organisation) {
		this.uuid = UUID.randomUUID();
		this.id = uuid.toString();
		this.name = name;
		this.organisation = Refs.ref(organisation);
	}

	public String getId() {
		return id;
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public List<String> getUsernames() {
		return new ArrayList<>(usernames);
	}

	public void addUser(UserGae user) {
		this.usernames.add(user.getUsername());
	}

	public void addUsers(List<UserGae> users) {
		List<String> usernames = UserGae.ToNames.from(users);
		this.usernames.addAll(usernames);
	}

	public boolean hasUser(UserGae user) {
		return this.usernames.contains(user.getUsername());
	}

	public void removeUser(UserGae user) {
		this.usernames.remove(user.getUsername());
	}

	public void removeUsers(List<UserGae> users) {
		List<String> usernames = UserGae.ToNames.from(users);
		this.usernames.removeAll(usernames);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AccountGae other = (AccountGae) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public OrganisationGae getOrganisation() {
		return Refs.unref(organisation);
	}

	public void setOrganisation(OrganisationGae organisation) {
		this.organisation = Refs.ref(organisation);
	}

	public AccountGae withOrganisation(OrganisationGae organisation) {
		this.setOrganisation(organisation);
		return this;
	}

	public AccountGae withName(String name) {
		this.setName(name);
		return this;
	}

	public AccountGae withDescription(String description) {
		this.setDescription(description);
		return this;
	}
}
