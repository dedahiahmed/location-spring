package mr.iscae.services;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.PharmacyDto;
import mr.iscae.entities.Pharmacy;
import mr.iscae.repositories.PharmacyRepository;
import mr.iscae.specifications.PharmacySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

        boolean isNightTime = currentTime.isAfter(LocalTime.of(23, 59))
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
        existingPharmacy.setOpenTonight(existingPharmacy.isOpenTonight());
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
}