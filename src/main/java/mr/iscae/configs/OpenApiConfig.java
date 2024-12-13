package mr.iscae.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

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

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            Parameter authHeader = new Parameter()
                    .in("header")
                    .name("Authorization")
                    .description("Authorization header")
                    .required(false)
                    .schema(new io.swagger.v3.oas.models.media.StringSchema());

            operation.addParametersItem(authHeader);
            return operation;
        };
    }
}