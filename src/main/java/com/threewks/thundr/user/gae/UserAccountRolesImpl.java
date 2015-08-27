package com.threewks.thundr.user.gae;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.threewks.thundr.user.Roles;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
public class UserAccountRolesImpl<U extends User, A extends Account> implements Roles {

	@Id
	protected String id;

	protected Account account;

	@Parent
	protected Ref<User> user;
	protected Set<String> roles = new LinkedHashSet<>();
	protected DateTime lastUpdated;

	public UserAccountRolesImpl(A account, U user, Set<String> roles) {
		this.id = user.username + account.getId();
		this.user = (Ref<User>) Ref.create(user);
		this.account = account;
		this.roles.addAll(roles);
		this.lastUpdated = DateTime.now();
	}

	@Override
	public User getUser() {
		if (user == null) {
			return null;
		}
		return user.get();
	}

	@Override
	public Account getAccount() {
		return account;
	}

	@Override
	public boolean hasRole(String role) {
		return roles.contains(role);
	}

	@Override
	public Set<String> getRoles() {
		return Collections.unmodifiableSet(roles);
	}

	@Override
	public void addRoles(List roles) {
		this.roles.addAll(roles);
	}

	@Override
	public void removeRoles(List roles) {
		this.roles.removeAll(roles);
	}

	@Override
	public DateTime getLastUpdated() {
		return this.lastUpdated;
	}
}
