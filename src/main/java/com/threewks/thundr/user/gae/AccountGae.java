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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.search.SearchIndex;
import com.threewks.thundr.user.Account;
import com.threewks.thundr.user.User;

@Entity
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

	protected AccountGae() {

	}

	public AccountGae(String name) {
		this.uuid = UUID.randomUUID();
		this.id = uuid.toString();
		this.name = name;
	}

	@Override
	public String getId() {
		return id;
	}

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

	@Override
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
}
