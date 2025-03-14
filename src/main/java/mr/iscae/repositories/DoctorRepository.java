package mr.iscae.repositories;

import mr.iscae.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    List<Doctor> findByCabinetId(Long cabinetId);
    boolean existsByNameAndCabinetId(String name, Long cabinetId);
}