package com.threewks.thundr.user.gae;

import com.google.common.collect.Sets;
import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.search.gae.mediator.FieldMediatorSet;
import com.threewks.thundr.search.gae.meta.IndexTypeLookup;
import com.threewks.thundr.transformer.TransformerManager;
import com.threewks.thundr.user.Roles;
import com.threewks.thundr.user.UserTokenRepository;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.atomicleopard.expressive.Expressive.list;
import static com.atomicleopard.expressive.Expressive.set;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class AccountServiceImplIT {

	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();

	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(User.class, UserToken.class, PasswordAuthentication.class, Account.class, UserAccountRolesImpl.class, UserAccountRolesIndexImpl.class);

	private String username = "username";
	private UserServiceImpl userService;
	private AccountServiceImpl accountService;
	private User user;
	private PasswordAuthentication password;
	private Account account;

	@Before
	public void before() {
		user = new User(username);
		account = new Account("3wks", "3kwk sales pipeline");
		password = new PasswordAuthentication(username, "password");
		UserTokenRepository<User> tokenRepository = new UserTokenRepositoryImpl<User>();
		UserRepositoryImpl<User> userRepository = new UserRepositoryImpl<User>(User.class, new SearchConfig(TransformerManager.createWithDefaults(), new FieldMediatorSet(), new IndexTypeLookup()));
		userService = new UserServiceImpl(tokenRepository, userRepository);
		AccountRepositoryImpl<Account, User> accountRepository = new AccountRepositoryImpl<Account, User>(Account.class, null);
		accountService = new AccountServiceImpl(accountRepository);
	}

	@Test
	public void shouldCreateAccountWithRoles() throws Exception {
		userService.put(user, password);
		Set<String> roles = set("Admin", "Business");
		UserAccountRolesImpl<User, Account> userAccountRoles = new UserAccountRolesImpl<>(account, user, roles);
		accountService.createAccountWithRoles(userAccountRoles);
		Roles rolesForUser = accountService.getRolesForUser(account, user);
		assertThat(rolesForUser.getRoles(), CoreMatchers.<Set> is(roles));
		List<User> users = accountService.getUsers(account);
		assertThat(users.get(0), is(user));
	}

	@Test
	public void shouldReturnAllUserForAccount() {
		User user1 = new User("user1");
		User user2 = new User("user2");
		userService.put(user1, password);
		userService.put(user2, password);

		Set<String> roles = set("Admin", "Business");
		UserAccountRolesImpl<User, Account> userAccountRoles1 = new UserAccountRolesImpl<>(account, user1, roles);
		accountService.createAccountWithRoles(userAccountRoles1);

		UserAccountRolesImpl<User, Account> userAccountRoles2 = new UserAccountRolesImpl<>(account, user2, roles);
		accountService.createAccountWithRoles(userAccountRoles2);

		List<User> users = accountService.getUsers(account);
		assertThat(users, contains(user1, user2));
	}

	@Test
	public void shouldAddRoles() {
		userService.put(user, password);
		Set<String> roles = Sets.newLinkedHashSet();
		roles.add("Admin");
		roles.add("Business");
		UserAccountRolesImpl<User, Account> userAccountRoles = new UserAccountRolesImpl<>(account, user, roles);
		accountService.createAccountWithRoles(userAccountRoles);
		Roles returnedRoles = accountService.addRoles(account, user, list("Guest"));
		assertThat((Set<String>) returnedRoles.getRoles(), contains("Admin", "Business", "Guest"));
	}

	@Test
	public void shouldRemoveRoles() {
		userService.put(user, password);
		Set<String> roles = Sets.newLinkedHashSet();
		roles.add("Admin");
		roles.add("Business");
		UserAccountRolesImpl<User, Account> userAccountRoles = new UserAccountRolesImpl<>(account, user, roles);
		accountService.createAccountWithRoles(userAccountRoles);
		Roles returnedRoles = accountService.removeRoles(account, user, list("Admin"));
		assertThat((Set<String>) returnedRoles.getRoles(), contains("Business"));
	}

	@Test
	public void shouldValidateIfUsersHaveAccess() {
		userService.put(user, password);
		Set<String> roles = Sets.newLinkedHashSet();
		roles.add("Admin");
		roles.add("Business");

		UserAccountRolesImpl<User, Account> userAccountRoles = new UserAccountRolesImpl<>(account, user, roles);
		accountService.createAccountWithRoles(userAccountRoles);
		assertThat(accountService.hasAccess(account, user, "Admin"), is(true));
		assertThat(accountService.hasAccess(account, user, "Business"), is(true));
		assertThat(accountService.hasAccess(account, user, "Guest"), is(false));

		accountService.addRoles(account, user, list("Guest"));
		assertThat(accountService.hasAccess(account, user, "Guest"), is(true));
	}
}