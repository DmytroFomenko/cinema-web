package dfomenko.repository;

import dfomenko.entity.PlaceKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlaceKindRepository extends JpaRepository<PlaceKind, Long> {
    PlaceKind findPlaceKindById(Long placeKindId);
    void deletePlaceKindById(Long placeKindId);
}
