package online.shop.shophere.SecurityConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = "file:///C:/Users/User/OneDrive/Desktop/SpringBoot projects/ShopHere/uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}