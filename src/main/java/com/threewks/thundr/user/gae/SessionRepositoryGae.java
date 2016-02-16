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

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.atomicleopard.expressive.ETransformer;
import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.gae.objectify.repository.UuidRepository;
import com.threewks.thundr.session.SessionRepository;
import com.threewks.thundr.user.User;

public class SessionRepositoryGae implements SessionRepository<SessionGae> {
	private UuidRepository<SessionGae> sessionRepository = new UuidRepository<>(SessionGae.class, null);
	private UuidRepository<SessionId> sessionIdRepository = new UuidRepository<>(SessionId.class, null);

	@Override
	public String createSessionId(SessionGae session) {
		SessionId id = new SessionId(session);
		id = sessionIdRepository.put(id);
		return id.getId();
	}

	@Override
	public SessionGae get(String sessionId) {
		if (StringUtils.isBlank(sessionId)) {
			return null;
		}
		SessionId s = ofy().load().type(SessionId.class).id(sessionId).now();
		return s == null ? null : s.getSession();
	}

	@Override
	public SessionGae create() {
		return new SessionGae();
	}

	@Override
	public SessionGae put(SessionGae session) {
		return sessionRepository.put(session);
	}

	@Override
	public List<SessionGae> put(List<SessionGae> sessions) {
		return sessionRepository.put(sessions);
	}

	@Override
	public void delete(SessionGae session) {
		List<SessionId> sessionIds = sessionIdRepository.getByField("session", session);
		sessionIdRepository.delete(sessionIds);
		sessionRepository.delete(session);
	}

	@Override
	public List<SessionGae> listSessions(User user) {
		return sessionRepository.getByField("user", user);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends User> Map<U, List<SessionGae>> listSessions(List<U> users) {
		List<SessionGae> sessions = sessionRepository.getByField("user", users);
		return (Map<U, List<SessionGae>>) UserSessionLookup.from(sessions);
	}

	@Override
	public List<SessionGae> deleteFor(User user) {
		List<SessionGae> sessions = listSessions(user);
		List<SessionId> sessionIds = sessionIdRepository.getByField("session", sessions);
		sessionIdRepository.delete(sessionIds);
		sessionRepository.delete(sessions);
		return sessions;
	}

	private static final ETransformer<Collection<SessionGae>, Map<User, List<SessionGae>>> UserSessionLookup = Expressive.Transformers.toBeanLookup("user", SessionGae.class);

}
