package dfomenko.service.impl;

import dfomenko.entity.PlaceKind;
import dfomenko.repository.PlaceKindRepository;
import dfomenko.service.PlaceKindService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class PlaceKindServiceImpl implements PlaceKindService {

    private final PlaceKindRepository placeKindRepository;

    @Override
    public List<PlaceKind> findAllPlaceKinds() {
        return placeKindRepository.findAll();
    }

    @Override
    public PlaceKind findPlaceKindById(Long placeKindId) {
        return placeKindRepository.findPlaceKindById(placeKindId);
    }

    @Override
    public void createPlaceKind(PlaceKind placeKind) {
        placeKindRepository.save(placeKind);
    }

    @Override
    public void updatePlaceKind(PlaceKind placeKind) {
        placeKindRepository.save(placeKind);
    }

    @Transactional
    @Override
    public void deletePlaceKindById(Long placeKindId) {
        placeKindRepository.deletePlaceKindById(placeKindId);
    }

}
