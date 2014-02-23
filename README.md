thundr-gae-user
=================

A thundr module implemented for GAE to provide login logout functionality using:
* Basic login with username password
* OAuth2 authorisation for providers Facebook or Gmail.

Include the thundr-gae-user dependency using maven or your favourite dependency management tool.
    
    <dependency>
        <groupId>com.threewks.thundr</groupId>
        <artifactId>thundr-gae-user</artifactId>
        <version>1.0-rc2</version>
        <scope>compile</scope>
    </dependency>


In your ApplicationModule.java you must add the UserGaeModule as a dependency.

    @Override
    public void requires(DependencyRegistry dependencyRegistry) {
        dependencyRegistry.addDependency(UserGaeModule.class);

To include Oauth functionality for Facebook and Gmail add the following services to your `ApplicationModule#addServices`:
````
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

````
You will also need to update your `application.properties` file to include environment specific parameters dependent
on the settings from you OAuth Provider and the custom call back url. For example:

````
oAuthCallbackGoogle=http://<domain>/account/provider/google/signin
oAuthApiKeyGoogle=334234234152392.apps.googleusercontent.com
oAuthApiSecretGoogle=wLsdlt30ISOD99rXy

oAuthCallbackFacebook=http://<domain>/account/provider/facebook/signin
oAuthApiKeyFacebook=989879080800
oAuthApiSecretFacebook=r098098sd0809809a8sdf098
````

Now you have wired all the dependencies in you will need to write you custom controller and include the `OAuthService.java` and `UserService`.
Refer to the example below which illustrates:

````
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
````

