/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
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
import com.threewks.thundr.user.authentication.BaseOAuthAuthentication;
import com.threewks.thundr.user.gae.User;

@Index
@Entity
public class OAuthAuthentication extends BaseOAuthAuthentication implements ObjectifyAuthentication<OAuthAuthentication> {
	@Id protected String id;
	protected Ref<User> userRef;

	public OAuthAuthentication() {
	}

	public OAuthAuthentication(String provider, String identity, String email) {
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
			OAuthAuthentication matchingAuth = (OAuthAuthentication) getMatchingAuthentication(ofy, this);
			return matchingAuth == null ? null : matchingAuth.userRef.get();
		}
		return userRef.get();
	}

	@Override
	public OAuthAuthentication getMatchingAuthentication(Objectify ofy, OAuthAuthentication authentication) {
		return ofy.load().type(OAuthAuthentication.class).id(authentication.id).now();
	}

	@Override
	public void setUser(User user) {
		userRef = Ref.create(user);
	}

	protected void ensureId() {
		this.id = this.identity + ":" + this.provider;
	}

}
