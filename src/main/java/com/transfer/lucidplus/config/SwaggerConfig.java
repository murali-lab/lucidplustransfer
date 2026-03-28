package com.transfer.lucidplus.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	
	@Value("${email.support.address}")
	private String supportEmail;
	 
    private static final String COOKIE_SCHEME_NAME = "cookieAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LucidPlus Transfer API")
                        .description("REST API documentation for LucidPlus Transfer Service")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("LucidPlus Team")
                                .email(supportEmail))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(COOKIE_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(COOKIE_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("LuCiDpLuStOkEn")
                                        .description("JWT token stored in HttpOnly cookie")));
    }
}