package mr.iscae.controllers;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.DoctorDto;
import mr.iscae.entities.Doctor;
import mr.iscae.services.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctors().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(doctorService.getDoctorById(id)));
    }

    @GetMapping("/cabinet/{cabinetId}")
    public ResponseEntity<List<DoctorDto>> getDoctorsByCabinetId(@PathVariable Long cabinetId) {
        List<DoctorDto> doctors = doctorService.getDoctorsByCabinetId(cabinetId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto doctorDto) {
        Doctor doctor = doctorService.addDoctor(doctorDto);
        return new ResponseEntity<>(toDto(doctor), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorDto doctorDto) {
        Doctor doctor = doctorService.updateDoctor(id, doctorDto);
        return ResponseEntity.ok(toDto(doctor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    private DoctorDto toDto(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .speciality(doctor.getSpeciality())
                .schedule(doctor.getSchedule())
                .cabinetId(doctor.getCabinet().getId())
                .build();
    }

}