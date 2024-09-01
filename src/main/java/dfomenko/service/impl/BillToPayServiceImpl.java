package dfomenko.service.impl;

import dfomenko.entity.BillToPay;
import dfomenko.repository.BillToPayRepository;
import dfomenko.service.BillToPayService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class BillToPayServiceImpl implements BillToPayService {

    private final BillToPayRepository billToPayRepository;

    @Override
    public List<BillToPay> findAllBillToPays() {
        return billToPayRepository.findAll();
    }

    @Override
    public BillToPay findBillToPayById(Long billToPayId) {
        return billToPayRepository.findBillToPayById(billToPayId);
    }

    @Override
    public void createBillToPay(BillToPay billToPay) {
        billToPayRepository.save(billToPay);
    }

    @Override
    public void updateBillToPay(BillToPay billToPay) {
        billToPayRepository.save(billToPay);
    }

    @Transactional
    @Override
    public void deleteBillToPayById(Long billToPayId) {
        billToPayRepository.deleteBillToPayById(billToPayId);
    }

    @Override
    public boolean existsBillToPayByName(String billToPayName) {
        return billToPayRepository.existsBillToPayByName(billToPayName);
    }

    @Override
    public boolean existsBillToPayByNameAndIdNot(String billToPayName, Long billToPayId) {
        return billToPayRepository.existsBillToPayByNameAndIdNot(billToPayName, billToPayId);
    }

    @Override
    public boolean existsBillToPayByBillToPay(String billToPayBillToPay) {
        return billToPayRepository.existsBillToPayByBillToPay(billToPayBillToPay);
    }
}
