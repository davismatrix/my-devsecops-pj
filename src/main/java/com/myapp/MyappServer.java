package com.myapp;

import com.myapp.util.LoggingInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class MyappServer extends WebMvcConfigurerAdapter
{

    public static void main(String[] args)
    {
        SpringApplication.run(MyappServer.class, args);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new LoggingInterceptor()).addPathPatterns("/**");
    }


    @Bean
    public Docket docket()
    {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(getClass().getPackage().getName()))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(generateApiInfo());
    }
      
   private ApiInfo generateApiInfo() {
    return new ApiInfoBuilder()
        .title("DAVISMATRIX DEVSECOPS SAMPLE PROJECT")
        .description("This is a sample project for Devsecops CICD pipeline.")
        .version("Version 1.0 - mw")
        .termsOfServiceUrl("urn:tos")
        .license("Apache 2.0")
        .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
        .contact(new Contact("DAVIS", "", "")) // Optional contact info
        .build();
}

}
