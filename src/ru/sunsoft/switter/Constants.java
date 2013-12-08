package ru.sunsoft.switter;

public class Constants {

    public static final String CONSUMER_KEY                 = "cUqSVdsIMKAbroqJsfD3Eg";
    public static final String CONSUMER_SECRET              = "bm8aJWypiNLQuZMnvPKacuuA9ZcmzWEYnLTSUtcc";

    public static final String IEXTRA_AUTH_URL              = "auth_url";
    public static final String IEXTRA_OAUTH_VERIFIER        = "oauth_verifier";
    public static final String IEXTRA_OAUTH_TOKEN           = "oauth_token";

    public static final String REQUEST_URL                  = "https://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL                   = "https://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL                = "https://api.twitter.com/oauth/authorize";

    public static final String PREF_NAME                    = "ru.sunsoft.flatstack_twitter";
    public static final String PREF_KEY_ACCESS_TOKEN        = "access_token";
    public static final String PREF_KEY_ACCESS_TOKEN_SECRET = "access_token_secret";

    public static final String OAUTH_CALLBACK_SCHEME        = "x-oauthflow-twitter";
    public static final String OAUTH_CALLBACK_HOST          = "callback";
    public static final String OAUTH_CALLBACK_URL           = OAUTH_CALLBACK_SCHEME
                                                                    + "://"
                                                                    + OAUTH_CALLBACK_HOST;
}
