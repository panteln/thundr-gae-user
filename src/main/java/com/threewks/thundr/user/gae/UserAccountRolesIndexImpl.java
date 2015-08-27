package com.threewks.thundr.user.gae;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class UserAccountRolesIndexImpl<A extends Account, U extends User> {

	@Parent
	protected Ref<A> account;
	@Id
	protected String id;
	protected Ref<U> user;

	public UserAccountRolesIndexImpl(A account, U user) {
		this.account = Ref.create(account);
		this.id = user.username;
		this.user = Ref.create(user);
	}

	public User getUser() {
		if (user == null) {
			return null;
		}

		return user.getValue();
	}
}
