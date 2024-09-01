package dfomenko.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Component
public class CinemaFilesUtils {

    @Value("${app.cinemafiles-dir-name:cinemafiles}")
    private String cinemaFilesDirNameProperty;

    @Value("${app.cinemafiles-container-path:cinemafiles}")
    private String cinemaFilesDockerContainerPathProperty;

    @Value("${app.cinemafiles-host-path:/src/main/resources/cinemafiles}")
    private String cinemaFilesHostPathProperty;

    @Value("${app.is-from-docker-container:false}")
    private boolean isFromDockerContainer;


    public String getCinemaFilesPathProperty() {
        if (!isFromDockerContainer) {
            return cinemaFilesHostPathProperty;
        }
        return cinemaFilesDockerContainerPathProperty;
    }

    public Resource getCinemaFilesResource() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        return resolver.getResource(getCinemaFilesPathProperty());
    }

    public Path getCinemaFilesAbsPath() {

        Path basePath = Paths.get("").toAbsolutePath();

//        System.out.println("Cinema from Docker Container = " + isFromDockerContainer);

        if (isFromDockerContainer) {
            return Paths.get(cinemaFilesDockerContainerPathProperty);
        }

        return Paths.get(basePath.toString(), cinemaFilesHostPathProperty);
    }

}
