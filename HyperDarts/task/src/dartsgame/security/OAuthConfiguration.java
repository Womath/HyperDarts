package dartsgame.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final String clientId;
    private final String clientSecret;
    private final String jwtSigningKey;
    private final int accessTokenValiditySeconds;
    private final String[] authorizedGrantTypes;
    private final int refreshTokenValiditySeconds;

    public OAuthConfiguration(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        this.authenticationManager =
                authenticationConfiguration.getAuthenticationManager();
        this.clientId = "hyperdarts";
        this.clientSecret = "secret";
        this.accessTokenValiditySeconds = 3600;
        this.authorizedGrantTypes = new String[] {"password", "authorization_code", "refresh_token"};
        this.refreshTokenValiditySeconds = 3600;
        this.jwtSigningKey = "-----BEGIN RSA PRIVATE KEY-----" +
                "MIICWwIBAAKBgQDQ+7yKlJGuvYtf1soMsJjkQJGAXe90QAxqppycf+3JT5ehnvvWtwS8ef+UsqrNa5Rc9tyyHjP7ZXRN145SlRTZzc0" +
                "d03Ez10OfAEVdhGACgRxS5s+GZVtdJuVcje3Luq3VIvZ8mV/P4eRcV3yVKDwQEenMuL6Mh6JLH48KxgbNRQIDAQABAoGAd5k5w41W+k" +
                "vbcZO4uh5uwWH6Yx5fJYZqFLcZNa845Fa6jnIv6id/fGXNUMoXWcxRcgqNLxp94Uekkc/k0XokHaEac21ReDDVmufgwujoUHVacDEWW" +
                "kkool0FVBirmlWJhM8Kt0Tyr7GmUilktekTt2QC/pL0LJCbo8Exmg3DnFkCQQDpb89ftQ35zBqs+BAl9zCa3cxYGGHlBLKLPKk0MZeC" +
                "SQ8iY37fwTPlpY/fmNo/rQTDLDemJ/CYNxLOFyrPBVfDAkEA5S7ZFMH+c8D2O+73p82m0ZH96afYC2kA0UFoitAntUL/hjxfWMPU5Qn" +
                "K5n+2gCTHynVSogCPGQovZfoHsax+VwJAH3Zba9naPV2+BqwUeRl86pKUVRdMMnLUoaGWaJt6gSvZp1fjpMLEfOI4pvlSCR0HtEqEYZ" +
                "emfM2HclF7CpX8wwJARt7Hzj13HABtpHbvKnrTvTayGBEJI+4ijJL3awWXYdwH/FCrA137daAjmEeh/dph1d+V3/bgSVP2+EfrHSxEH" +
                "QJALeyliJOUCrXM6hqksKuJlSOxArd3UiQe9t/q6woGTC3Y2tz7Yw5CZkDPqHchmGv7+ZZv5dh2EHtxsM1SXUFVfQ==" +
                "-----END RSA PRIVATE KEY-----";
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception
    {
        oauthServer.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientId)
                .secret("{noop}" + clientSecret)
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .authorizedGrantTypes(authorizedGrantTypes)
                .scopes("read", "write", "update")
                .resourceIds("api");
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager);
    }

    @Bean
    JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(jwtSigningKey);
        return converter;
    }
}