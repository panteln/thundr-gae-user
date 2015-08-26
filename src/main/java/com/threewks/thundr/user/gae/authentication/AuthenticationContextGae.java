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
