package eu.neclab.ngsildbroker.registryhandler.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("eu.neclab.ngsildbroker"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInformation() );
    }

    private ApiInfo getApiInformation(){
        return new ApiInfo("Registry Manager APIs",
                "Contains GET, PUT, PATCH and DELETE Operations",
                "1.0",
                "API Terms of Service URL",
                new Contact("Endpoint", "https://github.com/ScorpioBroker/ScorpioBroker", "https://github.com/ScorpioBroker/ScorpioBroker"),
                "API License",
                "API License URL",
                Collections.emptyList()
                );
    }
}


