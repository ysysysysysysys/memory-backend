package com.yesong.memory.memory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Swagger2配置
 * 
 * @author tangh
 *
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

	@Bean
	public Docket groupApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("测试")
				.globalOperationParameters(getParameters())
				.apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.yesong.memory.memory.action"))
				.paths(PathSelectors.any())
				.build();
	}
	private List<Parameter> getParameters(){
		ParameterBuilder builder = new ParameterBuilder()
				.name("user-holder")
				.description("User holder")
		    	.modelRef(new ModelRef("string"))
		    	.parameterType("header") 
		    	.required(false);
        return Arrays.asList(builder.build());
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Log Apis").description("Apis for memory")
				.contact(new Contact("ye song", "", "")).version("1.0").build();
	}
}
