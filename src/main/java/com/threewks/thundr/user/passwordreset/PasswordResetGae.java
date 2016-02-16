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
package com.threewks.thundr.user.passwordreset;

import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class PasswordResetGae implements PasswordReset {
	@Id
	protected String uuid;
	@Index
	protected String email;
	@Index
	protected DateTime created;

	protected PasswordResetGae() {

	}

	public PasswordResetGae(String email) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.email = email;
		this.created = new DateTime();
	}

	@Override
	public UUID getId() {
		return UUID.fromString(uuid);
	}

	@Override
	public DateTime getCreated() {
		return created;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public boolean isExpired(Duration duration) {
		return created != null && created.plus(duration).isBeforeNow();
	}
}
