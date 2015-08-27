package com.threewks.thundr.user.gae;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.atomicleopard.expressive.Cast;
import com.atomicleopard.expressive.ETransformer;
import com.atomicleopard.expressive.Expressive;
import com.atomicleopard.expressive.transform.CollectionTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.LoadIds;
import com.threewks.thundr.gae.objectify.repository.StringRepository;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.user.AccountRepository;
import com.threewks.thundr.user.Roles;
import com.threewks.thundr.user.User;

public class AccountRepositoryImpl<A extends Account, U extends UserGae> implements AccountRepository<A, U> {

	private final ETransformer<Collection<UserAccountRolesImpl>, Map<A, UserAccountRolesImpl>> AccountRolesLookup = Expressive.Transformers.toKeyBeanLookup("account", UserAccountRolesImpl.class);
	private final ETransformer<Collection<UserAccountRolesImpl>, Map<U, UserAccountRolesImpl>> UserRolesLookup = Expressive.Transformers.toKeyBeanLookup("user", UserAccountRolesImpl.class);
	private static final CollectionTransformer<UserAccountRolesIndexImpl, Ref<UserAccountRolesImpl>> RolesIndexToRolesRef = Expressive.Transformers.transformAllUsing(Expressive.Transformers
			.<UserAccountRolesIndexImpl, Ref<UserAccountRolesImpl>> toProperty("rolesRef", UserAccountRolesIndexImpl.class));

	private StringRepository<A> delegateRepository;

	public AccountRepositoryImpl(Class<A> entityType, SearchConfig searchConfig) {
		this.delegateRepository = new StringRepository<>(entityType, searchConfig);
	}

	@Override
	public A put(A account) {
		delegateRepository.save(account).complete();
		return account;
	}

	@Override
	public A delete(A account) {
		delegateRepository.delete(account).complete();
		return account;
	}

	@Override
	public List<A> delete(List<A> accounts) {
		delegateRepository.delete(accounts).complete();
		return accounts;
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
	public Roles putRoles(A account, U user, Roles roles) {
		final UserAccountRolesImpl userAccountRoles = Cast.as(roles, UserAccountRolesImpl.class);
		userAccountRoles.setAccountAndUser(account, user);
		final UserAccountRolesIndexImpl userAccountRolesIndex = new UserAccountRolesIndexImpl(account, userAccountRoles);
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				ofy().save().entities(userAccountRoles, userAccountRolesIndex).now();
			}
		});

		return userAccountRoles;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<A, Roles> getAccounts(U user) {
		List<UserAccountRolesImpl> roles = ofy().load().type(UserAccountRolesImpl.class).ancestor(user).list();
		Map<A, UserAccountRolesImpl> result = AccountRolesLookup.from(roles);
		return (Map) result;
	}

	@Override
	public List<U> getUsers(A account) {
		return new ArrayList<>(getUserRoles(account).keySet());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<U, Roles> getUserRoles(A account) {
		List<UserAccountRolesIndexImpl> rolesIndexes = ofy().load().type(UserAccountRolesIndexImpl.class).ancestor(account).list();
		List<Ref<UserAccountRolesImpl>> rolesRefs = RolesIndexToRolesRef.from(rolesIndexes);
		Collection<UserAccountRolesImpl> roles = ofy().load().refs(rolesRefs).values();
		Map<U, UserAccountRolesImpl> usersAndRoles = UserRolesLookup.from(roles);
		return (Map) usersAndRoles;
	}

	@Override
	public A get(String accountId) {
		return delegateRepository.load(accountId);
	}

	@Override
	public void removeUsersFromAccount(A account, List<U> user) {
		// TODO - v3 - sean
	}

	@Override
	public void removeUserFromAccounts(U user, List<A> accounts) {
		// TODO - v3 - sean
	}

}
