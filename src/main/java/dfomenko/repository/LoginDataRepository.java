package dfomenko.repository;


import dfomenko.entity.LoginData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDataRepository extends JpaRepository<LoginData, Long> {
    LoginData findLoginDataByNickname(String nickname);
    boolean existsLoginDataByNickname(String nickname);
    boolean existsLoginDataByEmail(String email);
    LoginData findLoginDataByEmail(String email);
    LoginData findLoginDataById(Long loginDataId);
}
