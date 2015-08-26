package com.threewks.thundr.user.gae;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.atomicleopard.expressive.Cast;
import com.atomicleopard.expressive.ETransformer;
import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.gae.objectify.repository.UuidRepository;
import com.threewks.thundr.session.SessionRepository;
import com.threewks.thundr.user.User;

public class SessionRepositoryGae implements SessionRepository<SessionGae> {
	private UuidRepository<SessionGae> sessionRepository = new UuidRepository<>(SessionGae.class, null);
	private UuidRepository<SessionId> sessionIdRepository = new UuidRepository<>(SessionId.class, null);

	@Override
	public List<String> createSessionIds(SessionGae session, int count) {
		List<SessionId> ids = new ArrayList<>();
		List<String> results = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			SessionId id = new SessionId(session);
			ids.add(id);
			results.add(id.getId());
		}
		sessionIdRepository.save(ids).complete();
		return results;
	}

	@Override
	public SessionGae getSession(String sessionId) {
		if (StringUtils.isBlank(sessionId)) {
			return null;
		}
		SessionId s = ofy().load().type(SessionId.class).id(sessionId).now();
		return s == null ? null : s.getSession();
	}

	@Override
	public SessionGae create() {
		return sessionRepository.save(new SessionGae()).complete();
	}

	@Override
	public SessionGae attach(SessionGae session, User user) {
		session.setUser(Cast.as(user, UserGae.class));
		return sessionRepository.save(session).complete();
	}

	@Override
	public SessionGae detach(SessionGae session, User user) {
		// TODO - v3 - Does this API make sense, wouldnt you just detach the user
		// from the session rather than a specific user?
		if (session.getUser().equals(user)) {
			session.setUser(null);
			return sessionRepository.save(session).complete();
		}
		return session;
	}

	@Override
	public User getUser(String sessionId) {
		SessionGae session = getSession(sessionId);
		return session == null ? null : session.getUser();
	}

	@Override
	public User getUser(SessionGae session) {
		return session == null ? null : session.getUser();
	}

	@Override
	public SessionGae put(SessionGae session) {
		// TODO - v3 - NAO - Why is this needed? Sessions cant be mutated
		return sessionRepository.save(session).complete();
	}

	@Override
	public List<SessionGae> put(List<SessionGae> sessions) {
		// TODO - v3 - NAO - Why is this needed? Sessions cant be mutated
		return sessionRepository.save(sessions).complete();
	}

	@Override
	public void delete(SessionGae session) {
		List<SessionId> sessionIds = sessionIdRepository.loadByField("session", session);
		sessionIdRepository.delete(sessionIds).complete();
		sessionRepository.delete(session).complete();
	}

	@Override
	public void delete(User user) {
		List<SessionGae> sessions = listSessions(user);
		List<SessionId> sessionIds = sessionIdRepository.loadByField("session", sessions);
		sessionIdRepository.delete(sessionIds).complete();
		sessionRepository.delete(sessions).complete();
	}

	@Override
	public List<SessionGae> listSessions(User user) {
		return sessionRepository.loadByField("user", user);
	}

	@Override
	public Map<User, List<SessionGae>> listSessions(List<User> users) {
		List<SessionGae> sessions = sessionRepository.loadByField("user", users);
		return UserSessionLookup.from(sessions);
	}

	@Override
	public List<SessionGae> listSessions(User user, String channel) {
		// TODO - v3 - introduce the idea of channels to sessions as a whole
		return null;
	}

	private static final ETransformer<Collection<SessionGae>, Map<User, List<SessionGae>>> UserSessionLookup = Expressive.Transformers.toBeanLookup("user", SessionGae.class);
}
