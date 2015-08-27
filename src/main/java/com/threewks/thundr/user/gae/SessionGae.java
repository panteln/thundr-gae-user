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

import java.util.UUID;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.session.Session;
import com.threewks.thundr.session.SessionState;
import com.threewks.thundr.user.User;

// TODO - v3 - How do we clean up sessions and SessionId - a last used timestamp and cron?
// thats pretty heavy on writes
@Entity
public class SessionGae implements Session {

	@Id
	private String id;

	@Index
	private Ref<UserGae> user;
	private SessionState sessionState = SessionState.Anonymous;

	public SessionGae() {
		this.id = UUID.randomUUID().toString();
	}

	@Override
	public UUID getId() {
		return UUID.fromString(id);
	}

	@Override
	public <U extends User> U getUser() {
		return user == null ? null : (U) user.get();
	}

	@Override
	public SessionState getSessionState() {
		return this.sessionState;
	}

	@Override
	public boolean is(SessionState state) {
		return this.sessionState == state;
	}

	@Override
	public boolean isAnonymous() {
		return is(SessionState.Anonymous);
	}

	@Override
	public boolean isIdentified() {
		return is(SessionState.Identified);
	}

	@Override
	public boolean isAuthenticated() {
		return is(SessionState.Authenticated);
	}

	@Override
	public Session anonymise() {
		this.sessionState = SessionState.Anonymous;
		this.user = null;
		return this;
	}

	@Override
	public Session identify(User user) {
		this.sessionState = SessionState.Identified;
		this.user = user(user);
		return this;
	}

	@Override
	public Session authenticate(User user) {
		this.sessionState = SessionState.Authenticated;
		this.user = user(user);
		return this;
	}

	protected Ref<UserGae> user(User user) {
		UserGae userGae = (UserGae) user;
		return Ref.create(userGae);
	}
}
