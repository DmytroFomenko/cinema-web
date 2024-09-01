package dfomenko.service;


import dfomenko.entity.LoginData;

import java.util.List;

public interface LoginDataService {
    List<LoginData> getAllLoginData();
    LoginData getLoginDataById(Long id);
    boolean checkLoginData(LoginData loginData);
    boolean existsLoginDataByNickname(String nickname);
    boolean existsLoginDataByEmail(String email);
    LoginData findLoginDataByEmail(String email);
    LoginData findLoginDataByNickname(String nickname);
    LoginData saveLoginData(LoginData loginData);
    LoginData updateLoginData(LoginData loginData);
    LoginData findLoginDataById(Long loginDataId);
    String encrypt(String plainText);
    String decrypt(String encryptedText);

}
