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
package com.threewks.thundr.user.gae;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class UserToken {
	@Id
	private Long id;

	@Load
	@Parent
	private Ref<User> user;

	UserToken() {

	}

	public UserToken(User user) {
		this.user = Ref.create(user);
	}

	public User getUser() {
		return user.get();
	}

	public String getToken() {
		return Key.create(user.getKey(), UserToken.class, id).getString();
	}

	public Key<UserToken> getKey() {
		Key<User> parentKey = user.getKey();
		return Key.create(parentKey, UserToken.class, id);
	}

}
