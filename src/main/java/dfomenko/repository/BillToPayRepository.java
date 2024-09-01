package dfomenko.repository;

import dfomenko.entity.BillToPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BillToPayRepository extends JpaRepository<BillToPay, Long> {
    BillToPay findBillToPayById(Long billToPayId);
    void deleteBillToPayById(Long billToPayId);
    boolean existsBillToPayByName(String billToPayName);
    boolean existsBillToPayByNameAndIdNot(String billToPayName, Long billToPayId);
    boolean existsBillToPayByBillToPay(String billToPayBillToPay);
}
