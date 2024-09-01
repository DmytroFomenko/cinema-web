package dfomenko.repository;

import dfomenko.entity.Placement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlacementRepository extends JpaRepository<Placement, Long> {
    Placement findPlacementById(Long placementId);
    Placement findPlacementByName(String placementName);
    void deletePlacementById(Long placementId);
    void deletePlacementByName(String placementName);
}
