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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.user.Organisation;

@Entity(name = "thundrOrganisation")
public class OrganisationGae implements Organisation {

	@Id
	protected String id;
	@Index
	protected String name;
	protected String description;
	protected DateTime created;
	protected Map<String, Object> properties = new HashMap<>();
	protected Set<String> usernames = new LinkedHashSet<>();

	protected OrganisationGae() {
	}

	public OrganisationGae(String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.created = new DateTime();
	}

	@Override
	public UUID getUuid() {
		return UUID.fromString(id);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public DateTime getCreated() {
		return created;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String name) {
		return (T) properties.get(name);
	}

	@Override
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}

	@Override
	public void removeProperty(String name) {
		properties.remove(name);
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
		OrganisationGae other = (OrganisationGae) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public OrganisationGae withName(String name) {
		setName(name);
		return this;
	}

	public OrganisationGae withDescription(String name) {
		setDescription(name);
		return this;
	}

	public OrganisationGae withProperty(String name, Object value) {
		setProperty(name, value);
		return this;
	};

	public Set<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(Set<String> usernames) {
		this.usernames = usernames;
	}

	public void addUsernames(Set<String> usernames) {
		this.usernames.addAll(usernames);
	}

	public void removeUsernames(Set<String> usernames) {
		this.usernames.removeAll(usernames);
	}

}
