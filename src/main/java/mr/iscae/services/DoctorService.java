package mr.iscae.services;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.DoctorDto;
import mr.iscae.entities.Cabinet;
import mr.iscae.entities.Doctor;
import mr.iscae.repositories.CabinetRepository;
import mr.iscae.repositories.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final CabinetRepository cabinetRepository;

    public Page<Doctor> getAllDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));
    }

    public List<Doctor> getDoctorsByCabinetId(Long cabinetId) {
        return doctorRepository.findByCabinetId(cabinetId);
    }

    @Transactional
    public Doctor addDoctor(DoctorDto doctorDto) {
        validateNewDoctor(doctorDto);
        Doctor doctor = toEntity(doctorDto);
        return doctorRepository.save(doctor);
    }

    private void validateNewDoctor(DoctorDto doctorDto) {
        if (doctorRepository.existsByNameAndCabinetId(doctorDto.getName(), doctorDto.getCabinetId())) {
            throw new IllegalArgumentException("A doctor with this name already exists in this cabinet");
        }
        if (!cabinetRepository.existsById(doctorDto.getCabinetId())) {
            throw new EntityNotFoundException("Cabinet not found with id: " + doctorDto.getCabinetId());
        }
    }

    @Transactional
    public Doctor updateDoctor(Long id, DoctorDto doctorDto) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));

        updateDoctorFields(existingDoctor, doctorDto);
        return doctorRepository.save(existingDoctor);
    }

    private void updateDoctorFields(Doctor existingDoctor, DoctorDto updatedDoctor) {
        if (updatedDoctor.getName() != null) {
            existingDoctor.setName(updatedDoctor.getName());
        }
        if (updatedDoctor.getSpeciality() != null) {
            existingDoctor.setSpeciality(updatedDoctor.getSpeciality());
        }
        if (updatedDoctor.getSchedule() != null) {
            existingDoctor.setSchedule(updatedDoctor.getSchedule());
        }
        if (updatedDoctor.getCabinetId() != null) {
            Cabinet cabinet = cabinetRepository.findById(updatedDoctor.getCabinetId())
                    .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id: " + updatedDoctor.getCabinetId()));
            existingDoctor.setCabinet(cabinet);
        }
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new EntityNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    private Doctor toEntity(DoctorDto dto) {
        Cabinet cabinet = cabinetRepository.findById(dto.getCabinetId())
                .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id: " + dto.getCabinetId()));

        return Doctor.builder()
                .id(dto.getId())
                .name(dto.getName())
                .speciality(dto.getSpeciality())
                .schedule(dto.getSchedule())
                .cabinet(cabinet)
                .build();
    }
}