package vn.cosbeauty.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourcesConfig implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry ) {
		registry.addResourceHandler("/images/**")
				.addResourceLocations("file:D:/University/Nam3HK2/Analyze and Design Information System/FinalProject/CosBeauty/src/upload/images/");
	}
}
