package dfomenko.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties
public class UiConfig {

    private int additionNameMinLength;
    private int additionNameMaxLength;

    private String priceStep;
    private String priceMinValue;
    private String priceMaxValue;
    private String passwordMinLength;
    private String passwordMaxLength;
    private String passwordRegPattern;
    private String passwordLoginPattern;
    private int nicknameMinLength;
    private int nicknameMaxLength;
    private String nicknameRegPattern;
    private String nicknameLoginPattern;
    private String emailAdminPattern;
    private String emailClientPattern;
    private int sessionClientTimeout;
    private int sessionAdminTimeout;

}
