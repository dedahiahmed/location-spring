package mr.iscae.controllers;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.BulkOpenStatusRequest;
import mr.iscae.dtos.PharmacyDto;
import mr.iscae.entities.Pharmacy;
import mr.iscae.services.PharmacyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
    public ResponseEntity<Page<PharmacyDto>> getAvailablePharmacies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String willaya,
            @RequestParam(required = false) String moughataa,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // Create pageable for sorting and pagination
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // If user coordinates are provided, use proximity-based search
        if (longitude != null && latitude != null) {
            // For proximity-based search, we override the sortBy parameter
            // as results will be sorted by distance regardless
            Page<PharmacyDto> pharmacyDtos = pharmacyService.getAvailablePharmaciesByProximity(
                    name, willaya, moughataa, longitude, latitude, pageable);
            return ResponseEntity.ok(pharmacyDtos);
        } else {
            // Fall back to original method if no coordinates provided
            Page<Pharmacy> pharmacies = pharmacyService.getAvailablePharmacies(name, willaya, moughataa, pageable);
            Page<PharmacyDto> pharmacyDtos = pharmacies.map(this::toDto);
            return ResponseEntity.ok(pharmacyDtos);
        }
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

    @PatchMapping("/open-status/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateOpenStatusBulk(@RequestBody BulkOpenStatusRequest request) {
        try {
            if (request.getPharmacyIds() == null || request.getPharmacyIds().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "No pharmacies selected"
                        ));
            }

            boolean updated = pharmacyService.updateOpenStatusBulk(
                    request.getPharmacyIds(),
                    request.isOpenTonight()
            );

            if (updated) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Updated " + request.getPharmacyIds().size() + " pharmacies successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Some pharmacies could not be found"
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", "Error updating pharmacies: " + e.getMessage()
                    ));
        }
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