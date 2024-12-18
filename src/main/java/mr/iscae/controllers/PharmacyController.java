package mr.iscae.controllers;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.PharmacyDto;
import mr.iscae.entities.Pharmacy;
import mr.iscae.services.PharmacyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PharmacyDto>> getAllPharmacies(Pageable pageable) {
        Page<PharmacyDto> pharmacies = pharmacyService.getAllPharmacies(pageable)
                .map(this::toDto);
        return ResponseEntity.ok(pharmacies);
    }

    @GetMapping("/available")
    public ResponseEntity<List<PharmacyDto>> getAvailablePharmacies() {
        List<PharmacyDto> pharmacies = pharmacyService.getAvailablePharmacies().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pharmacies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDto> getPharmacyById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(pharmacyService.getPharmacyById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PharmacyDto> createPharmacy(@Valid @RequestBody PharmacyDto pharmacyDto) {
        Pharmacy pharmacy = pharmacyService.addPharmacy(pharmacyDto);
        return new ResponseEntity<>(toDto(pharmacy), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PharmacyDto> updatePharmacy(
            @PathVariable Long id,
            @Valid @RequestBody PharmacyDto pharmacyDto) {
        Pharmacy pharmacy = pharmacyService.updatePharmacy(id, pharmacyDto);
        return ResponseEntity.ok(toDto(pharmacy));
    }

    @PatchMapping("/{id}/open-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateOpenStatus(
            @PathVariable Long id,
            @RequestParam boolean isOpenTonight) {
        boolean updated = pharmacyService.updateOpenStatus(id, isOpenTonight);
        if (updated) {
            return ResponseEntity.ok("Open status updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePharmacy(@PathVariable Long id) {
        pharmacyService.deletePharmacy(id);
        return ResponseEntity.noContent().build();
    }

    private PharmacyDto toDto(Pharmacy pharmacy) {
        return PharmacyDto.builder()
                .id(pharmacy.getId())
                .name(pharmacy.getName())
                .longitude(pharmacy.getLongitude())
                .latitude(pharmacy.getLatitude())
                .willaya(pharmacy.getWillaya())
                .moughataa(pharmacy.getMoughataa())
                .img(pharmacy.getImg())
                .isOpenTonight(pharmacy.isOpenTonight())
                .build();
    }

}