package dfomenko.service;

import dfomenko.entity.PlaceKind;

import java.util.List;


public interface PlaceKindService {
    List<PlaceKind> findAllPlaceKinds();
    PlaceKind findPlaceKindById(Long placeKindId);
    void createPlaceKind(PlaceKind placeKind);
    void updatePlaceKind(PlaceKind placeKind);
    void deletePlaceKindById(Long placeKindId);

}
