package dfomenko.config;

import dfomenko.utils.CinemaFilesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@RequiredArgsConstructor
@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class MvcConfig implements WebMvcConfigurer {

    private final CinemaFilesUtils cinemaFilesUtils;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String location = cinemaFilesUtils.getCinemaFilesAbsPath().toUri().toString();
        String pattern = "/" + cinemaFilesUtils.getCinemaFilesDirNameProperty() + "/**";

//        System.out.println("Cinema images pattern: " + pattern);
//        System.out.println("Cinema images URI:     " + location);

        registry
                .addResourceHandler(pattern)
                .addResourceLocations(location)
//                    .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

}
