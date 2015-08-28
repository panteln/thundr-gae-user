package com.threewks.thundr.user.gae;

import static com.atomicleopard.expressive.Expressive.list;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.threewks.thundr.gae.objectify.repository.StringRepository;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.user.AccountRepository;
import com.threewks.thundr.user.Roles;
import com.threewks.thundr.user.RolesImpl;

public class AccountRepositoryImpl<A extends AccountGae, U extends UserGae> implements AccountRepository<A, U> {

	private StringRepository<A> delegateRepository;
	private UserRepositoryGae<U> userRepository;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AccountRepositoryImpl(UserRepositoryGae<U> userRepository, SearchConfig searchConfig) {
		this((Class) AccountGae.class, userRepository, searchConfig);
	}

	public AccountRepositoryImpl(Class<A> entityType, UserRepositoryGae<U> userRepository, SearchConfig searchConfig) {
		this.delegateRepository = new StringRepository<>(entityType, searchConfig);
		this.userRepository = userRepository;
	}

	@Override
	public A put(A account) {
		delegateRepository.save(account).complete();
		return account;
	}

	/**
	 * This is not a transactional operation, if you require this to be transactional
	 * you need to remove the users in batches yourself, then delete the empty account
	 * - this probably should happen with sequential task queue jobs.
	 */
	@Override
	public A delete(A account) {
		List<String> usernames = account.getUsernames();
		List<U> users = userRepository.load(usernames);
		for (U user : users) {
			user.removeAccount(account);
		}
		// Order of these is important
		delegateRepository.delete(account).complete();
		userRepository.save(users).complete();
		return account;
	}

	@Override
	public Roles getRoles(A account, U user) {
		return new RolesImpl(user.getRoles(account));
	}

	@Override
	public Roles putRoles(A account, U user, Roles roles) {
		user.setRoles(account, roles.getRoles());
		account.addUser(user);
		boolean updateAccount = !account.hasUser(user);
		userRepository.save(user).complete();
		if (updateAccount) {
			delegateRepository.save(account).complete();
		}
		return roles;
	}

	@Override
	public A addUserToAccount(U user, A account) {
		account.addUser(user);
		user.addAccount(account);
		userRepository.save(user).complete();
		delegateRepository.save(account).complete();
		return account;
	}

	@Override
	public List<A> getAccounts(U user) {
		// TODO - Enhancement: Given the potential for writes across entity groups to fail,
		// we could validate the returned accounts have the username in their set of users
		// to ensure a consistent view.
		Collection<Key<AccountGae>> accounts = user.getAccounts();
		return (List) list(ofy().load().keys(accounts).values()).removeItems((AccountGae) null);
	}

	@Override
	public List<U> getUsers(A account) {
		List<String> usernames = account.getUsernames();
		return userRepository.load(usernames);
	}

	@Override
	public Map<U, Roles> getUserRoles(A account) {
		List<U> users = getUsers(account);
		Map<U, Roles> results = new LinkedHashMap<>();
		for (U user : users) {
			results.put(user, new RolesImpl(user.getRoles(account)));
		}
		return results;
	}

	@Override
	public A getAccount(String accountId) {
		return delegateRepository.load(accountId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeUsersFromAccount(A account, List<U> users) {
		account.removeUsers((List) users);
		for (U user : users) {
			user.removeAccount(account);
		}
		// Order here is important
		delegateRepository.save(account).complete();
		userRepository.save(users).complete();
	}

	@Override
	public void removeUserFromAccounts(U user, List<A> accounts) {
		for (A account : accounts) {
			account.removeUser(user);
			user.removeAccount(account);
		}
		// Order is important
		delegateRepository.save(accounts).complete();
		userRepository.save(user).complete();

	}

}
