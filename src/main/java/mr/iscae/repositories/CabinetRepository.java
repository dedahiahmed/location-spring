package mr.iscae.repositories;

import mr.iscae.entities.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {


    @Override
    List<Cabinet> findAll();

    // Find cabinet by ID
    @Override
    Optional<Cabinet> findById(Long id);

    // Delete cabinet by ID
    @Override
    void deleteById(Long id);

    // Check if cabinet exists by name
    boolean existsByNom(String nom);

    // Check if cabinet exists by location
    @Query("SELECT COUNT(c) > 0 FROM Cabinet c WHERE c.longitude = :longitude AND c.latitude = :latitude")
    boolean existsByLocation(@Param("longitude") Double longitude, @Param("latitude") Double latitude);

    // Check if cabinet exists by name and location
    @Query("SELECT COUNT(c) > 0 FROM Cabinet c WHERE c.nom = :nom AND c.longitude = :longitude AND c.latitude = :latitude")
    boolean existsByNomAndLocation(
            @Param("nom") String nom,
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude
    );

    // Save or update cabinet
    @Override
    <S extends Cabinet> S save(S entity);
}