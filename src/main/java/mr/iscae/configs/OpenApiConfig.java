package mr.iscae.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
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

        return new OpenAPI()
                .addServersItem(server);
    }
}