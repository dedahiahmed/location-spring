package mr.iscae.repositories;

import mr.iscae.entities.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    // Check if a Pharmacy exists by name
    boolean existsByName(String name);

    // Check if a Pharmacy exists by location
    @Query("SELECT COUNT(p) > 0 FROM Pharmacy p WHERE p.longitude = :longitude AND p.latitude = :latitude")
    boolean existsByLocation(double longitude, double latitude);

    // Check if a Pharmacy exists by name and location
    @Query("SELECT COUNT(p) > 0 FROM Pharmacy p WHERE p.name = :name AND p.longitude = :longitude AND p.latitude = :latitude")
    boolean existsByNameAndLocation(String name, double longitude, double latitude);

    // Update the 'isOpenTonight' property for a Pharmacy by ID
    @Transactional
    @Modifying
    @Query("UPDATE Pharmacy p SET p.isOpenTonight = :isOpenTonight WHERE p.id = :id")
    int updateIsOpenTonight(Long id, boolean isOpenTonight);

    // Find a Pharmacy by ID
    Optional<Pharmacy> findById(Long id);

    // Delete a Pharmacy
    void delete(Pharmacy pharmacy);
}

