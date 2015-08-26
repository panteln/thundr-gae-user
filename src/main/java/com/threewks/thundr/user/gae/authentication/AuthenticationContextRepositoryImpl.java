package com.threewks.thundr.user.gae.authentication;

import static com.atomicleopard.expressive.Expressive.list;

import java.util.List;

import com.atomicleopard.expressive.Cast;
import com.threewks.thundr.gae.objectify.repository.UuidRepository;
import com.threewks.thundr.session.Session;
import com.threewks.thundr.user.User;
import com.threewks.thundr.user.authentication.AuthenticationContext;
import com.threewks.thundr.user.authentication.AuthenticationContextRepository;
import com.threewks.thundr.user.gae.SessionGae;
import com.threewks.thundr.user.gae.UserGae;

public class AuthenticationContextRepositoryImpl implements AuthenticationContextRepository {
	protected UuidRepository<AuthenticationContextGae> authContextRepository = new UuidRepository<>(AuthenticationContextGae.class, null);
	protected UuidRepository<SessionGae> sessionRepository = new UuidRepository<>(SessionGae.class, null);

	@Override
	public AuthenticationContext get(Session session) {
		SessionGae sessionGae = cast(session);
		return sessionGae == null ? null : sessionGae.getAuthContext();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends User> U authorise(Session session, AuthenticationContext authContext) {
		SessionGae sessionGae = cast(session);
		AuthenticationContextGae authContextGae = cast(authContext);
		sessionGae.setAuthContext(authContextGae);
		sessionRepository.save(sessionGae).complete();
		return (U) authContext.getUser();
	}

	@Override
	public Session deauthorise(Session session) {
		SessionGae sessionGae = cast(session);
		sessionGae.setAuthContext(null);
		sessionRepository.save(sessionGae).complete();
		return session;
	}

	@Override
	public List<Session> deauthorise(AuthenticationContext authenticationContext) {
		List<SessionGae> sessions = sessionRepository.loadByField("authContenxt", authenticationContext);
		for (SessionGae sessionGae : sessions) {
			sessionGae.setAuthContext(null);
		}
		sessionRepository.save(sessions).complete();
		List<Session> results = (List) sessions;
		return results;
	}

	@Override
	public AuthenticationContextGae create(User user) {
		UserGae userGae = Cast.as(user, UserGae.class);
		return authContextRepository.save(new AuthenticationContextGae(userGae)).complete();
	}

	@Override
	public void expireAll(User user) {
		List<AuthenticationContextGae> all = authContextRepository.loadByField("user", user);
		removeAuthenticatedSessions(all);
		authContextRepository.delete(all).complete();
	}

	@Override
	public void expire(AuthenticationContext context) {
		AuthenticationContextGae authContextGae = cast(context);
		removeAuthenticatedSessions(list(authContextGae));
		authContextRepository.delete(authContextGae).complete();
	}

	@Override
	public <U extends User> U getUser(AuthenticationContext context) {
		return context.getUser();
	}

	protected void removeAuthenticatedSessions(List<AuthenticationContextGae> all) {
		List<SessionGae> sessions = sessionRepository.loadByField("authContenxt", all);
		for (SessionGae sessionGae : sessions) {
			sessionGae.setAuthContext(null);
		}
		sessionRepository.save(sessions).complete();
	}

	protected SessionGae cast(Session session) {
		return Cast.as(session, SessionGae.class);
	}

	protected AuthenticationContextGae cast(AuthenticationContext authContext) {
		return Cast.as(authContext, AuthenticationContextGae.class);
	}
}
