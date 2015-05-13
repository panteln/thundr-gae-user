thundr-gae-user [![Build Status](https://travis-ci.org/3wks/thundr-gae-user.svg)](https://travis-ci.org/3wks/thundr-gae-user)
=================

A thundr module with persistence implementations of [thundr-user](https://github.com/3wks/thundr-user).

You can read more about how thundr-user works [here](https://github.com/3wks/thundr-user)

### Getting started

Include the ``thundr-gae-user`` dependency using maven or your favourite dependency management tool.
    
    <dependency>
        <groupId>com.threewks.thundr</groupId>
        <artifactId>thundr-gae-user</artifactId>
        <version>2.0.0</version>
        <scope>compile</scope>
    </dependency>

[You can see all versions here](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.threewks.thundr%22%20AND%20a%3A%22thundr-gae-user%22)

In your ApplicationModule.java you must add the UserGaeModule as a dependency. You don't need to add UserModule, that will be handled for you.

    @Override
    public void requires(DependencyRegistry dependencyRegistry) {
        dependencyRegistry.addDependency(UserGaeModule.class);

The thundr-gae-user module will take care of the following implementations for you, including registering all entities with Objectify. This is everything that is defined as abstract in the base thundr-user module, so you're ready to go. 

* ``User``
* ``UserRepository``
* ``UserTokenRepository``
* ``PasswordAuthentication`` 
* ``OAuthAuthentication`` 

Remember to secure all urls that users submit credentials over (such as log in, change password, and any APIs passing credentials such as sessions as headers). The cookie session store has two session tokens, one for http and one for https. In general, when in doubt, use https.

OAuth
-----

To include Oauth functionality for Facebook and Gmail add the following services to your `ApplicationModule#addServices`:

	injectionContext.inject(FacebookOAuthProviderConfig.class).as(FacebookOAuthProviderConfig.class);
	injectionContext.inject(GoogleOAuthProviderConfig.class).as(GoogleOAuthProviderConfig.class);

	FacebookOAuthProviderConfig facebookOAuthConfig = injectionContext.get(FacebookOAuthProviderConfig.class);
	GoogleOAuthProviderConfig goolgeOAuthConfig = injectionContext.get(GoogleOAuthProviderConfig.class);
	FacebookOAuthProvider facebookOAuthProvider = new FacebookOAuthProvider(facebookOAuthConfig, OAuthAuthentication.class);
	GoogleOAuthProvider googleOAuthProvider = new GoogleOAuthProvider(goolgeOAuthConfig, OAuthAuthentication.class);

	injectionContext.inject(OAuthService.class).as(OAuthService.class);
	OAuthService<User> oAuthService = injectionContext.get(OAuthService.class);
	oAuthService.registerProvider(facebookOAuthProvider);
	oAuthService.registerProvider(googleOAuthProvider);


You will also need to update your `application.properties` file to include environment specific parameters dependent
on the settings from you OAuth Provider and the custom call back url. For example:

	oAuthCallbackGoogle=http://<domain>/account/provider/google/signin
	oAuthApiKeyGoogle=334224234152392.apps.googleusercontent.com
	oAuthApiSecretGoogle=wLsdls30ISOD99rXy
	
	oAuthCallbackFacebook=http://<domain>/account/provider/facebook/signin
	oAuthApiKeyFacebook=989879080700
	oAuthApiSecretFacebook=r098098sb0809809a8sdf098

Now you have wired all the dependencies in you will need to write you custom controller and include the `OAuthService.java` and `UserService`.
Refer to the example below which illustrates:


	public class OAuthController {
	
	    private static final String URL_ACCOUNT_LOGIN = "/login";
	    private static final String URL_USER_RESTAURANT = "/user/restaurants";
	    private static final String VIEW_AUTH = "user/signIn.jsp";
	
	    private static Map<String, Class<? extends OAuthProvider>> providerClassLookup = map();
	    static {
	        providerClassLookup.put("facebook", FacebookOAuthProvider.class);
	        providerClassLookup.put("google", GoogleOAuthProvider.class);
	
	    }
	    private OAuthService<User> oAuthService;
	    private UserService userService;
	
	    public OAuthController(OAuthService<User> oAuthService, UserService userService) {
	        this.oAuthService = oAuthService;
	        this.userService = userService;
	    }
	
	    public View logout(User user, HttpServletResponse response) {
	        userService.logout(user, response);
	        return new RedirectView(URL_ACCOUNT_LOGIN);
	    }
	
	    public View login() {
	        Map<String, Object> model = map();
	        return new JspView(VIEW_AUTH, model);
	    }
	
	    public View signIn(String providerName, HttpServletRequest request) {
	        Class<? extends OAuthProvider> providerType = providerClassLookup.get(providerName);
	        String authorizationUrl = oAuthService.initiateSignIn(providerType, request);
	        return new RedirectView(authorizationUrl);
	    }
	
	    public View signInIdpCallBack(String providerName, String code, HttpServletRequest request, HttpServletResponse response) {
	        if (code == null) {
	            Logger.error("Missing code from provider %s", providerName);
	            return new RedirectView(URL_ACCOUNT_LOGIN);
	        }
	
	        Class<? extends OAuthProvider> providerType = providerClassLookup.get(providerName);
	        OAuthAuthentication authentication = oAuthService.completeSignIn(providerType, code, request);
	        if (authentication == null) {
	            Logger.error("user could not be authenticated for provider %s and access code", providerType, code);
	            return new RedirectView(URL_ACCOUNT_LOGIN);
	        }
	
	        User user = retrieveUserAndLogin(response, authentication);
	        if (user == null) {
	            Logger.error("user could not be retrieved and logged in for authentication %s", authentication);
	            return new RedirectView(URL_ACCOUNT_LOGIN);
	        }
	
	        return new RedirectView(URL_USER_RESTAURANT);
	    }
	
	    private User retrieveUserAndLogin(HttpServletResponse response, OAuthAuthentication authentication) {
	        User user = userService.get(authentication);
	        if (user == null) {
	            user = new User(authentication.getIdentity());
	            user.setEmail(authentication.getEmail());
	            user.setProperty("identityId", authentication.getIdentity());
	            user.setProperty("provider", authentication.getProvider());
	            userService.put(user, authentication);
	        }
	
	        user = userService.login(user, response);
	        return user;
	    }
	}

