package mr.iscae.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${PROD:false}")
    private boolean isProd;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server;
        if (isProd) {
            server = new Server().url("https://api.dedahi.com/location");
        } else {
            server = new Server().url("http://localhost:8080");
        }

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("api_key", securityScheme))
                .addServersItem(server);
    }
}