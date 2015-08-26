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

import java.util.UUID;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.user.User;
import com.threewks.thundr.user.authentication.AuthenticationContext;
import com.threewks.thundr.user.gae.UserGae;

@Entity
public class AuthenticationContextGae implements AuthenticationContext {
	@Id
	private String id;
	@Index
	private Ref<UserGae> userRef;

	public AuthenticationContextGae() {
		id = UUID.randomUUID().toString();
	}

	public AuthenticationContextGae(UserGae user) {
		id = UUID.randomUUID().toString();
		userRef = user == null ? null : Ref.create(user);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public <U extends User> U getUser() {
		return (U) userRef.get();
	}
}
