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
package com.threewks.thundr.user.invitation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class UserInvitationGae implements UserInvitation {
	@Id
	protected String id;
	@Index
	protected String username;
	@Index
	protected String email;
	@Index
	protected DateTime created;
	@Index
	protected UUID account;
	@Index
	protected UUID organisation;

	protected Map<String, String> properties = new LinkedHashMap<>();

	protected UserInvitationGae() {
	}

	public UserInvitationGae(String email, String username) {
		super();
		this.id = UUID.randomUUID().toString();
		this.created = new DateTime();
		this.email = email;
		this.username = username;
	}

	@Override
	public UUID getId() {
		return UUID.fromString(id);
	}

	@Override
	public UUID getAccountId() {
		return account;
	}

	@Override
	public void setAccountId(UUID accountId) {
		this.account = accountId;
	}

	public UserInvitationGae withAccountId(UUID accountId) {
		setAccountId(accountId);
		return this;
	}

	@Override
	public UUID getOrganisationId() {
		return organisation;
	}

	@Override
	public void setOrganisationId(UUID organisation) {
		this.organisation = organisation;
	}

	public UserInvitationGae withOrganisationId(UUID organisation) {
		setOrganisationId(organisation);
		return this;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public DateTime getCreated() {
		return created;
	}

	@Override
	public boolean isExpired(Duration duration) {
		return created != null && created.plus(duration).isBeforeNow();
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}
	
	public UserInvitationGae withProperties(Map<String, String> properties) {
		setProperties(properties);
		return this;
	}

	public UserInvitationGae withProperty(String key, String value) {
		setProperty(key, value);
		return this;
	}
}
