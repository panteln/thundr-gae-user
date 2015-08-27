package com.threewks.thundr.user.gae;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class UserAccountRolesIndexImpl {

	@Id
	protected String id;
	@Parent
	protected Ref<Account> account;
	protected Ref<UserAccountRolesImpl> roles;

	public UserAccountRolesIndexImpl(Account account, UserAccountRolesImpl roles) {
		this.id = roles.getUser().getUsername();
		this.account = Ref.create(account);
		this.roles = Ref.create(roles);
	}

	public UserAccountRolesImpl getRoles() {
		return roles.get();
	}
	public Ref<UserAccountRolesImpl> getRolesRef() {
		return roles;
	}
}
