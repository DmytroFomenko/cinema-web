package dfomenko.service.impl;

import dfomenko.entity.Placement;
import dfomenko.repository.PlacementRepository;
import dfomenko.service.PlacementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class PlacementServiceImpl implements PlacementService {

    private final PlacementRepository placementRepository;

    public Placement findPlacementById(Long placementId) {
        return placementRepository.findPlacementById(placementId);
    }

    @Override
    public Placement findPlacementByName(String placementName) {
        return placementRepository.findPlacementByName(placementName);
    }

    @Override
    public List<Placement> findAllPlacements() {
        return placementRepository.findAll();
    }

    @Override
    public void createPlacement(Placement placement) {
        placementRepository.save(placement);
    }

    @Override
    public void updatePlacement(Placement placement) {
        placementRepository.save(placement);
    }

    @Transactional
    @Override
    public void deletePlacementById(Long placementId) {
        placementRepository.deletePlacementById(placementId);
    }

    @Override
    public void deletePlacementByName(String placementName) {
        placementRepository.deletePlacementByName(placementName);
    }

}
