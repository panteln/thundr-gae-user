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

import com.threewks.thundr.gae.objectify.repository.UuidRepository;

public class PasswordResetRepositoryGae extends UuidRepository<PasswordResetGae> implements PasswordResetRepository {

	public PasswordResetRepositoryGae() {
		super(PasswordResetGae.class, null);
	}

	@Override
	public PasswordReset create(String email) {
		PasswordResetGae passwordReset = createPasswordReset(email);
		put(passwordReset);
		return passwordReset;
	}

	@Override
	public PasswordReset getPasswordReset(UUID id) {
		return get(id);
	}

	@Override
	public void deletePasswordReset(UUID id) {
		deleteByKey(id);
	}

	protected PasswordResetGae createPasswordReset(String email) {
		return new PasswordResetGae(email);
	}
}
