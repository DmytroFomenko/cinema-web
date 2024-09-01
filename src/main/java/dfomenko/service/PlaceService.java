package dfomenko.service;

import dfomenko.entity.Place;
import java.util.List;


public interface PlaceService {
    List<Place> findAllPlaces();
    List<Place> findPlacesByPlacementId(Long placementId);
    Place findPlaceById(Long placeId);
    void createPlace(Place place);
    void updatePlace(Place place);
    void deletePlaceById(Long placeId);
    boolean existsPlaceByPlacementIdAndNumber(Long placementId, String number);
    boolean existsPlaceByPlacementId(Long placementId);
    Place findPlaceByPlacementIdAndNumber(Long placementId, String number);
}
