package com.ndroid.shopping.shopping_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for error handling
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // This will ensure that NoHandlerFoundException is thrown for 404 errors
    // Instead of the default Whitelabel error page
}