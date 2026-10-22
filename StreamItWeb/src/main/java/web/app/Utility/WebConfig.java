package web.app.Utility;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import web.app.Identification.FileIdentification;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String directory = FileIdentification.ReferencePictures;

        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        "file:" + directory + "/"
                );
    }
}
