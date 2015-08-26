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
package com.threewks.thundr.user.gae.authentication;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.user.authentication.BasePasswordAuthentication;
import com.threewks.thundr.user.gae.UserGae;

@Index
@Entity
public class PasswordAuthentication extends BasePasswordAuthentication implements ObjectifyAuthentication<PasswordAuthentication> {
	@Id
	protected String id;
	protected Ref<UserGae> userRef;

	public PasswordAuthentication() {
		super();
	}

	public PasswordAuthentication(String username, String password) {
		super(username, password);
		this.id = username;
	}

	public PasswordAuthentication(String username, String password, int iterations, String digest) {
		super(username, password, iterations, digest);
		this.id = username;
	}

	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		this.id = username;
	}

	@Override
	public String getUsername() {
		return this.id;
	}

	@Override
	public UserGae getUser(Objectify ofy) {
		if (userRef == null) {
			PasswordAuthentication matchingAuth = (PasswordAuthentication) getMatchingAuthentication(ofy, this);
			return matchingAuth == null ? null : matchingAuth.userRef.get();
		}
		return userRef.get();
	}

	@Override
	public PasswordAuthentication getMatchingAuthentication(Objectify ofy, PasswordAuthentication authentication) {
		return ofy.load().type(PasswordAuthentication.class).id(authentication.username).now();
	}

	@Override
	public void setUser(UserGae user) {
		this.userRef = Ref.create(user);
	}
}
