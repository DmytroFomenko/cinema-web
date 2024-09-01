package dfomenko.service;


import dfomenko.entity.BillToPay;

import java.util.List;

public interface BillToPayService {

    List<BillToPay> findAllBillToPays();
    BillToPay findBillToPayById(Long billToPayId);
    void createBillToPay(BillToPay billToPay);
    void updateBillToPay(BillToPay billToPay);
    void deleteBillToPayById(Long billToPayId);
    boolean existsBillToPayByName(String billToPayName);
    boolean existsBillToPayByNameAndIdNot(String billToPayName, Long billToPayId);
    boolean existsBillToPayByBillToPay(String billToPayBillToPay);

}
