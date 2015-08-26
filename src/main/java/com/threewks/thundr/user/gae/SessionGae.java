package com.threewks.thundr.user.gae;

import java.util.UUID;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.session.Session;
import com.threewks.thundr.user.gae.authentication.AuthenticationContextGae;

// TODO - v3 - How do we clean up sessions and SessionId - a last used timestamp and cron?
// thats pretty heavy on writes
@Entity
public class SessionGae implements Session {

	@Id
	private String id;

	@Index
	private Ref<UserGae> user;

	@Index
	private Ref<AuthenticationContextGae> authContext;

	public SessionGae() {
		this.id = UUID.randomUUID().toString();
	}

	@Override
	public UUID getId() {
		return UUID.fromString(id);
	}

	public UserGae getUser() {
		return user == null ? null : user.get();
	}

	public void setUser(UserGae user) {
		this.user = user == null ? null : Ref.create(user);
	}

	public AuthenticationContextGae getAuthContext() {
		return authContext == null ? null : authContext.get();
	}

	public Ref<AuthenticationContextGae> getAuthContextRef() {
		return authContext;
	}

	public void setAuthContext(AuthenticationContextGae authContext) {
		this.authContext = authContext == null ? null : Ref.create(authContext);
	}
}
