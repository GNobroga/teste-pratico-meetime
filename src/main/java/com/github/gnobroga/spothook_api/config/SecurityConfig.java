package com.github.gnobroga.spothook_api.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gnobroga.spothook_api.controller.filter.ApiBearerTokenAuthFilter;
import com.github.gnobroga.spothook_api.service.OAuthService;

@Configuration
public class SecurityConfig {

    private static final String[] AUTH_PROTECTED_PATHS = { "/api/v1/contact/hubspot/*" };

    @Bean
    FilterRegistrationBean<ApiBearerTokenAuthFilter> bearerTokenAuthFilter(ObjectMapper objectMapper, OAuthService oAuthProvider) {
        ApiBearerTokenAuthFilter apiBearerTokenAuthFilter = new ApiBearerTokenAuthFilter(oAuthProvider, objectMapper);
        FilterRegistrationBean<ApiBearerTokenAuthFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(apiBearerTokenAuthFilter);
        filterRegistrationBean.addUrlPatterns(AUTH_PROTECTED_PATHS);
        return filterRegistrationBean;
    }
}
