package com.threewks.thundr.user.gae;

import com.atomicleopard.expressive.Cast;
import com.google.common.collect.Lists;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.LoadIds;
import com.threewks.thundr.gae.objectify.repository.StringRepository;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.user.AccountRepository;
import com.threewks.thundr.user.Roles;
import com.threewks.thundr.user.UserAccountRoles;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class AccountRepositoryImpl<A extends Account, U extends User> extends StringRepository<A> implements AccountRepository<A, U> {

	public AccountRepositoryImpl(Class<A> entityType, SearchConfig searchConfig) {
		super(entityType, searchConfig);
	}

	@Override
	public A putAccount(A account) {
		ofy().save().entity(account).now();
		return account;
	}

	@Override
	public Roles getRoles(A account, U user) {
		UserAccountRolesImpl roles = load(user).id(user.username + account.getId()).now();
		return roles;
	}

	private LoadIds<UserAccountRolesImpl> load(User user) {
		return ofy().load().type(UserAccountRolesImpl.class).parent(user);
	}

	@Override
	public Roles putRoles(Roles roles) {
		final UserAccountRolesImpl userAccountRoles = Cast.as(roles, UserAccountRolesImpl.class);
		final UserAccountRolesIndexImpl userAccountRolesIndex = new UserAccountRolesIndexImpl(userAccountRoles.getAccount(), userAccountRoles.getUser());
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				ofy().save().entity(userAccountRoles).now();
				ofy().save().entity(userAccountRolesIndex).now();
			}
		});

		return userAccountRoles;
	}

	@Override
	public List<UserAccountRoles<U, A>> getAccounts(U user) {
		List<UserAccountRoles<U, A>> list = (List) ofy().load().type(UserAccountRolesImpl.class).ancestor(user).list();
		return list;
	}

	@Override
	public List<U> getUsers(A account) {
		List<User> users = Lists.newArrayList();
		List<UserAccountRolesIndexImpl> rolesIndexes = ofy().load().type(UserAccountRolesIndexImpl.class).ancestor(account).list();
		for (UserAccountRolesIndexImpl userAccountRolesIndex : rolesIndexes) {
			User user = userAccountRolesIndex.getUser();
			users.add(user);
		}
		return (List<U>) users;
	}

	@Override
	public com.threewks.thundr.user.Account getAccount(String accountId) {
		return ofy().load().type(Account.class).id(accountId).now();
	}

	@Override
	public void removeUserFromAccount(A account, U user) {

	}

	@Override
	public void removeUsersFromAccount(A account, List<U> user) {

	}

	@Override
	public void removeUserFromAllAccounts(U user) {

	}

	@Override
	public void removeAllUsersFromAccount(A account) {

	}

	@Override
	public void removeAccount(A account) {

	}

	@Override
	public void removeAccounts(List<A> accounts) {

	}

}
