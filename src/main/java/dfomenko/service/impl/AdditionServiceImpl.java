package dfomenko.service.impl;

import dfomenko.entity.Addition;
import dfomenko.repository.AdditionRepository;
import dfomenko.service.AdditionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AdditionServiceImpl implements AdditionService {

    private final AdditionRepository additionRepository;

    @Override
    public List<Addition> findAllAdditions() {
        return additionRepository.findAll();
    }

    @Override
    public List<Addition> getAllAvailableAdditions() {
        return additionRepository.findAll().stream().filter(Addition::getAvailable).toList();
    }

    @Override
    public Addition findAdditionById(Long additionId) {
        return additionRepository.findAdditionById(additionId);
    }

    @Override
    public void createAddition(Addition addition) {
        additionRepository.save(addition);
    }

    @Override
    public void updateAddition(Addition addition) {
        additionRepository.save(addition);
    }

    @Transactional
    @Override
    public void deleteAdditionById(Long additionId) {
        additionRepository.deleteAdditionById(additionId);
    }

}
