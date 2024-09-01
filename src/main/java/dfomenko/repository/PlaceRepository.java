package dfomenko.repository;

import dfomenko.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findPlacesByPlacementId(Long placementId);

    Place findPlaceById(Long placeId);
    void deletePlaceById(Long placeId);
    boolean existsPlaceByPlacementIdAndNumber(Long placementId, String number);
    boolean existsPlaceByPlacementId(Long placementId);
    Place findPlaceByPlacementIdAndNumber(Long placementId, String number);
}
