package quiz.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import quiz.security.CustomUserDetailsService
import quiz.security.JwtAuthenticationEntryPoint
import quiz.security.JwtAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
open class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) : WebSecurityConfigurerAdapter() {

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    override fun authenticationManagerBean(): AuthenticationManager? = super.authenticationManagerBean()

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth
            ?.userDetailsService(customUserDetailsService)
            ?.passwordEncoder(passwordEncoder())
    }

    override fun configure(http: HttpSecurity?) {
        http
            ?.cors()
            ?.and()
            ?.csrf()
            ?.disable()
            ?.exceptionHandling()
            ?.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            ?.and()
            ?.sessionManagement()
            ?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ?.and()
            ?.authorizeRequests()
            ?.antMatchers(
                "/",
                "/favicon.ico",
                "/**/*.png",
                "/**/*.gif",
                "/**/*.svg",
                "/**/*.jpg",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js"
            )
            ?.permitAll()
            ?.antMatchers(
                "/api/auth/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "v2/**",
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources",
                "/swagger-ui.html",
                "/webjars/**"
            )
            ?.permitAll()
            ?.antMatchers(HttpMethod.GET, "/api/quiz/**")
            ?.permitAll()
            ?.antMatchers(HttpMethod.POST, "/api/quiz/access")
            ?.permitAll()
            ?.anyRequest()
            ?.authenticated()

        http!!.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}