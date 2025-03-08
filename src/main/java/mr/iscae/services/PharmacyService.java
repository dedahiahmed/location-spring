package mr.iscae.services;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.PharmacyDto;
import mr.iscae.entities.Pharmacy;
import mr.iscae.repositories.PharmacyRepository;
import mr.iscae.specifications.PharmacySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    public Page<Pharmacy> getAllPharmacies(Pageable pageable) {
        return pharmacyRepository.findAll(pageable);
    }

    public Page<Pharmacy> getAvailablePharmacies(String name, String willaya, String moughataa, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek currentDay = now.getDayOfWeek();

        boolean isNightTime = currentTime.isAfter(LocalTime.of(23, 0))
                || currentTime.isBefore(LocalTime.of(8, 0));
        boolean isSunday = currentDay == DayOfWeek.SUNDAY;

        // If it's night time or Sunday, we only want pharmacies that are open tonight
        Boolean openTonightFilter = null;
        if (isNightTime || isSunday) {
            openTonightFilter = true;
        }

        Specification<Pharmacy> spec = Specification.where(PharmacySpecification.withName(name))
                .and(PharmacySpecification.withWillaya(willaya))
                .and(PharmacySpecification.withMoughataa(moughataa))
                .and(PharmacySpecification.isOpenTonight(openTonightFilter));

        return pharmacyRepository.findAll(spec, pageable);
    }

    /**
     * Get available pharmacies with distance information and sorted by proximity
     *
     * @param name Optional name filter
     * @param willaya Optional willaya (region) filter
     * @param moughataa Optional moughataa (district) filter
     * @param userLongitude User's longitude coordinate
     * @param userLatitude User's latitude coordinate
     * @param pageable Pagination information
     * @return Page of PharmacyDto with distance information
     */
    public Page<PharmacyDto> getAvailablePharmaciesByProximity(
            String name,
            String willaya,
            String moughataa,
            Double userLongitude,
            Double userLatitude,
            Pageable pageable) {

        // Get the initial list of pharmacies based on filters
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek currentDay = now.getDayOfWeek();

        boolean isNightTime = currentTime.isAfter(LocalTime.of(23, 0))
                || currentTime.isBefore(LocalTime.of(8, 0));
        boolean isSunday = currentDay == DayOfWeek.SUNDAY;

        // If it's night time or Sunday, we only want pharmacies that are open tonight
        Boolean openTonightFilter = null;
        if (isNightTime || isSunday) {
            openTonightFilter = true;
        }

        Specification<Pharmacy> spec = Specification.where(PharmacySpecification.withName(name))
                .and(PharmacySpecification.withWillaya(willaya))
                .and(PharmacySpecification.withMoughataa(moughataa))
                .and(PharmacySpecification.isOpenTonight(openTonightFilter));

        // We need to get all matching pharmacies to sort them by distance
        List<Pharmacy> allMatchingPharmacies = pharmacyRepository.findAll(spec);

        // Convert to DTOs with distance calculation
        List<PharmacyDto> pharmaciesWithDistance = allMatchingPharmacies.stream()
                .map(pharmacy -> {
                    PharmacyDto dto = toDto(pharmacy);
                    Double distance = calculateDistance(
                            userLatitude, userLongitude,
                            pharmacy.getLatitude(), pharmacy.getLongitude());
                    dto.setDistanceInKm(distance);
                    return dto;
                })
                .sorted(Comparator.comparing(PharmacyDto::getDistanceInKm))
                .collect(Collectors.toList());

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), pharmaciesWithDistance.size());

        // Create a sublist for the current page
        List<PharmacyDto> pageContent = start < end
                ? pharmaciesWithDistance.subList(start, end)
                : new ArrayList<>();

        // Return as Page
        return new PageImpl<>(pageContent, pageable, pharmaciesWithDistance.size());
    }

    /**
     * Calculate distance between two geographic coordinates using Haversine formula
     */
    private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE; // Return a large value for sorting purposes
        }

        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km

        return Math.round(distance * 100.0) / 100.0; // Round to 2 decimal places
    }

    @Transactional
    public boolean updateOpenStatusBulk(List<Long> pharmacyIds, boolean isOpenTonight) {
        int updatedRows = pharmacyRepository.updateIsOpenTonightBulk(pharmacyIds, isOpenTonight);
        return updatedRows > 0;
    }

    @Transactional
    public Pharmacy addPharmacy(PharmacyDto pharmacyDto) {
        Pharmacy pharmacy = toEntity(pharmacyDto);
        validateNewPharmacy(pharmacy);
        return pharmacyRepository.save(pharmacy);
    }

    private void validateNewPharmacy(Pharmacy pharmacy) {
        if (pharmacyRepository.existsByLocation(pharmacy.getLongitude(), pharmacy.getLatitude())) {
            throw new IllegalArgumentException("A pharmacy already exists at the provided location");
        }
        if (pharmacyRepository.existsByNameAndLocation(
                pharmacy.getName(), pharmacy.getLongitude(), pharmacy.getLatitude())) {
            throw new IllegalArgumentException("A pharmacy with the same name and location already exists");
        }
    }

    public Pharmacy getPharmacyById(Long id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found with id: " + id));
    }

    public PharmacyDto getPharmacyDtoById(Long id, Double userLongitude, Double userLatitude) {
        Pharmacy pharmacy = getPharmacyById(id);
        PharmacyDto dto = toDto(pharmacy);

        if (userLongitude != null && userLatitude != null) {
            Double distance = calculateDistance(
                    userLatitude, userLongitude,
                    pharmacy.getLatitude(), pharmacy.getLongitude());
            dto.setDistanceInKm(distance);
        }

        return dto;
    }

    @Transactional
    public Pharmacy updatePharmacy(Long id, PharmacyDto pharmacyDto) {
        Pharmacy existingPharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found with id: " + id));

        updatePharmacyFields(existingPharmacy, pharmacyDto);
        return pharmacyRepository.save(existingPharmacy);
    }

    private void updatePharmacyFields(Pharmacy existingPharmacy, PharmacyDto updatedPharmacy) {
        if (updatedPharmacy.getName() != null) {
            existingPharmacy.setName(updatedPharmacy.getName());
        }
        if (updatedPharmacy.getWillaya() != null) {
            existingPharmacy.setWillaya(updatedPharmacy.getWillaya());
        }
        if (updatedPharmacy.getMoughataa() != null) {
            existingPharmacy.setMoughataa(updatedPharmacy.getMoughataa());
        }
        if (updatedPharmacy.getImg() != null) {
            existingPharmacy.setImg(updatedPharmacy.getImg());
        }
        // Fixed bug - now properly uses the DTO value
        existingPharmacy.setOpenTonight(updatedPharmacy.isOpenTonight());

        // Added updates for coordinates
        if (updatedPharmacy.getLongitude() != null) {
            existingPharmacy.setLongitude(updatedPharmacy.getLongitude());
        }
        if (updatedPharmacy.getLatitude() != null) {
            existingPharmacy.setLatitude(updatedPharmacy.getLatitude());
        }
    }

    @Transactional
    public void deletePharmacy(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found with id: " + id));
        pharmacyRepository.delete(pharmacy);
    }

    private Pharmacy toEntity(PharmacyDto dto) {
        return Pharmacy.builder()
                .id(dto.getId())
                .name(dto.getName())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .willaya(dto.getWillaya())
                .moughataa(dto.getMoughataa())
                .img(dto.getImg())
                .isOpenTonight(dto.isOpenTonight())
                .build();
    }

    private PharmacyDto toDto(Pharmacy entity) {
        return PharmacyDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .willaya(entity.getWillaya())
                .moughataa(entity.getMoughataa())
                .img(entity.getImg())
                .isOpenTonight(entity.isOpenTonight())
                // Distance will be set separately when user coordinates are available
                .build();
    }
}