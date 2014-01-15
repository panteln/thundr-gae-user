package com.threewks.thundr.user.gae.authentication;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.threewks.thundr.user.authentication.BaseOAuthAuthentication;
import com.threewks.thundr.user.gae.User;

@Entity(name = "OAuthAuthentication")
@Index
public class ObjectifyOAuthAuthentication extends BaseOAuthAuthentication implements ObjectifyAuthentication<ObjectifyOAuthAuthentication> {
	@Id protected String id;
	protected Ref<User> userRef;

	public ObjectifyOAuthAuthentication() {
	}

	public ObjectifyOAuthAuthentication(String provider, String identity, String email) {
		super(provider, identity, email);
		ensureId();
	}

	@Override
	public void setIdentity(String identity) {
		super.setIdentity(identity);
		ensureId();
	}

	@Override
	public void setProvider(String provider) {
		super.setProvider(provider);
		ensureId();
	};

	@Override
	public User getUser(Objectify ofy) {
		if (userRef == null) {
			ObjectifyOAuthAuthentication matchingAuth = (ObjectifyOAuthAuthentication) getMatchingAuthentication(ofy, this);
			return matchingAuth == null ? null : matchingAuth.userRef.get();
		}
		return userRef.get();
	}

	@Override
	public ObjectifyOAuthAuthentication getMatchingAuthentication(Objectify ofy, ObjectifyOAuthAuthentication authentication) {
		return ofy.load().type(ObjectifyOAuthAuthentication.class).id(authentication.id).now();
	}

	@Override
	public void setUser(User user) {
		userRef = Ref.create(user);
	}

	protected void ensureId() {
		this.id = this.identity + ":" + this.provider;
	}

}
