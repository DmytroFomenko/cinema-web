package dfomenko.service.impl;

import dfomenko.entity.Place;
import dfomenko.repository.PlaceRepository;
import dfomenko.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public List<Place> findAllPlaces() {
        return placeRepository.findAll();
    }

    @Override
    public List<Place> findPlacesByPlacementId(Long placementId) {
        return placeRepository.findPlacesByPlacementId(placementId);
    }

    @Override
    public Place findPlaceById(Long placeId) {
        return placeRepository.findPlaceById(placeId);
    }

    @Override
    public void createPlace(Place place) {
        placeRepository.save(place);
    }

    @Override
    public void updatePlace(Place place) {
        placeRepository.save(place);
    }

    @Transactional
    @Override
    public void deletePlaceById(Long placeId) {
        placeRepository.deletePlaceById(placeId);
    }

    @Override
    public boolean existsPlaceByPlacementIdAndNumber(Long placementId, String number) {
        return placeRepository.existsPlaceByPlacementIdAndNumber(placementId, number);
    }

    @Override
    public boolean existsPlaceByPlacementId(Long placementId) {
        return placeRepository.existsPlaceByPlacementId(placementId);
    }

    @Override
    public Place findPlaceByPlacementIdAndNumber(Long placementId, String number) {
        return placeRepository.findPlaceByPlacementIdAndNumber(placementId, number);
    }

}
