package dfomenko.repository;

import dfomenko.entity.Addition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdditionRepository extends JpaRepository<Addition, Long> {
    Addition findAdditionById(Long serviceId);

    void deleteAdditionById(Long additionId);

}
