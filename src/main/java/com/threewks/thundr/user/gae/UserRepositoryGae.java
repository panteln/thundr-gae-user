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

import java.util.List;

import com.threewks.thundr.gae.objectify.repository.AsyncRepository;
import com.threewks.thundr.user.UserRepository;

public interface UserRepositoryGae<U extends UserGae> extends UserRepository<U>, AsyncRepository<U, String> {
//	public <O extends OrganisationGae> List<U> listUsers(O organisation);
//	public <O extends OrganisationGae> O getOrganisation(U user);
//	public <O extends OrganisationGae> O setOrganisation(U user, O organisation);
}