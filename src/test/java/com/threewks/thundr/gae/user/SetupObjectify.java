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
package com.threewks.thundr.gae.user;

import java.util.Arrays;
import java.util.List;

import org.junit.rules.ExternalResource;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.translate.opt.joda.JodaTimeTranslators;

public class SetupObjectify extends ExternalResource {
	
	private List<Class<?>> register;

	public SetupObjectify(Class<?>...register){
		this.register = Arrays.asList(register);
	}
	@Override
	protected void before() throws Throwable {
		ObjectifyFactory factory = new ObjectifyFactory();
//		factory.getTranslators().add(Money.objectifyTranslator);
		JodaTimeTranslators.add(factory);
		for(Class<?> type : register){
			factory.register(type);
		}
		ObjectifyService.setFactory(factory);
	}

	@Override
	protected void after() {
		ObjectifyService.setFactory(new ObjectifyFactory());
		ObjectifyService.reset();
	}
}
