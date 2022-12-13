package dartsgame.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String ROLE_GAMER = "GAMER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_REFEREE = "REFEREE";

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("ivanhoe@acme.com").password("{noop}" + "oMoa3VvqnLxW").roles(ROLE_GAMER)
                .and()
                .withUser("robinhood@acme.com").password("{noop}" + "ai0y9bMvyF6G").roles(ROLE_GAMER)
                .and()
                .withUser("wilhelmtell@acme.com").password("{noop}" + "bv0y9bMvyF7E").roles(ROLE_GAMER)
                .and()
                .withUser("admin@acme.com").password("{noop}" + "zy0y3bMvyA6T").roles(ROLE_ADMIN)
                .and()
                .withUser("judgedredd@acme.com").password("{noop}" + "iAmALaw100500").roles(ROLE_REFEREE);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
