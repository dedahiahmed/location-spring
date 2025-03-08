package mr.iscae.controllers;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.CabinetDto;
import mr.iscae.dtos.PharmacyDto;
import mr.iscae.entities.Cabinet;
import mr.iscae.services.CabinetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cabinets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CabinetController {

    private final CabinetService cabinetService;

    @GetMapping
    public ResponseEntity<Page<Cabinet>> getAllCabinets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String willaya,
            @RequestParam(required = false) String moughataa,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(cabinetService.getAllCabinets(name, willaya, moughataa, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CabinetDto> getCabinetById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(cabinetService.getCabinetById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CabinetDto> createCabinet(@Valid @RequestBody CabinetDto cabinetDto) {
        Cabinet cabinet = cabinetService.addCabinet(cabinetDto);
        return new ResponseEntity<>(toDto(cabinet), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CabinetDto> updateCabinet(
            @PathVariable Long id,
            @Valid @RequestBody CabinetDto cabinetDto) {
        Cabinet cabinet = cabinetService.updateCabinet(id, cabinetDto);
        return ResponseEntity.ok(toDto(cabinet));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCabinet(@PathVariable Long id) {
        cabinetService.deleteCabinet(id);
        return ResponseEntity.noContent().build();
    }

    private CabinetDto toDto(Cabinet cabinet) {
        return CabinetDto.builder()
                .id(cabinet.getId())
                .nom(cabinet.getNom())
                .willaya(cabinet.getWillaya())
                .moughataa(cabinet.getMoughataa())
                .longitude(cabinet.getLongitude())
                .latitude(cabinet.getLatitude())
                .build();
    }

}