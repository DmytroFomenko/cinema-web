package dfomenko.service.impl;


import dfomenko.entity.LoginData;
import dfomenko.repository.LoginDataRepository;
import dfomenko.service.LoginDataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class LoginDataServiceImpl implements LoginDataService {

    private final LoginDataRepository loginDataRepository;

    @Override
    public List<LoginData> getAllLoginData() {
        return loginDataRepository.findAll();
    }

    @Override
    public LoginData getLoginDataById(Long id) {
        return loginDataRepository.findById(id).get();
    }

    @Override
    public String encrypt(String plainText) {
        String key = "aesEncryptionKey";
        String initVector = "encryptionIntVec";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String decrypt(String encryptedText) {
        String key = "aesEncryptionKey";
        String initVector = "encryptionIntVec";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedText));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean checkLoginData(LoginData enteredLoginData) {
        LoginData existingLoginData = loginDataRepository.findLoginDataByNickname(enteredLoginData.getNickname());
        if (existingLoginData != null) {
            return this.decrypt(existingLoginData.getPassword()).equals(enteredLoginData.getPassword());
        }
        return false;
    }

    @Override
    public boolean existsLoginDataByNickname(String nickname) {
        return loginDataRepository.existsLoginDataByNickname(nickname);
    }

    @Override
    public boolean existsLoginDataByEmail(String email) {
        return loginDataRepository.existsLoginDataByEmail(email);
    }

    @Override
    public LoginData findLoginDataByEmail(String email) {
        return loginDataRepository.findLoginDataByEmail(email);
    }

    @Override
    public LoginData findLoginDataByNickname(String nickname) {
        return loginDataRepository.findLoginDataByNickname(nickname);
    }

    @Override
    public LoginData saveLoginData(LoginData loginData) {
        loginData.setPassword(this.encrypt(loginData.getPassword()));
        return loginDataRepository.save(loginData);
    }

    @Override
    public LoginData updateLoginData(LoginData loginData) {
        loginData.setPassword(this.encrypt(loginData.getPassword()));
        return loginDataRepository.save(loginData);
    }

    @Override
    public LoginData findLoginDataById(Long loginDataId) {
        return loginDataRepository.findLoginDataById(loginDataId);
    }


}
