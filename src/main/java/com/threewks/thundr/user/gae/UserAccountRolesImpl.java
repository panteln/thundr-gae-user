package com.threewks.thundr.user.gae;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.threewks.thundr.user.Roles;

@Entity
public class UserAccountRolesImpl implements Roles {

	@Id
	protected String id;

	@Parent
	protected Ref<UserGae> user;
	protected Ref<Account> account;

	protected Set<String> roles = new LinkedHashSet<>();
	protected DateTime created = new DateTime();
	protected DateTime lastUpdated;

	public UserAccountRolesImpl(Account account, UserGae user, Set<String> roles) {
		setAccountAndUser(account, user);
		this.roles.addAll(roles);
		this.lastUpdated = DateTime.now();
	}

	public UserGae getUser() {
		return user == null ? null : user.get();
	}

	public Account getAccount() {
		return account == null ? null : account.get();
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
	public void addRoles(List<String> roles) {
		this.roles.addAll(roles);
	}

	@Override
	public void removeRoles(List<String> roles) {
		this.roles.removeAll(roles);
	}

	public DateTime getLastUpdated() {
		return this.lastUpdated;
	}

	protected void setAccountAndUser(Account account, UserGae user) {
		this.id = user.username + account.getId();
		this.user = Ref.create(user);
		this.account = Ref.create(account);
	}
}
