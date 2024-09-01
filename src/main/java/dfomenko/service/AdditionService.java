package dfomenko.service;


import dfomenko.entity.Addition;

import java.util.List;

public interface AdditionService {
    List<Addition> findAllAdditions();

    List<Addition> getAllAvailableAdditions();

    Addition findAdditionById(Long additionId);

    void createAddition(Addition addition);

    void updateAddition(Addition addition);

    void deleteAdditionById(Long additionId);

}
