package mr.iscae.services;

import lombok.RequiredArgsConstructor;
import mr.iscae.dtos.CabinetDto;
import mr.iscae.entities.Cabinet;
import mr.iscae.repositories.CabinetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CabinetService {

    private final CabinetRepository cabinetRepository;

    public Page<Cabinet> getAllCabinets(Pageable pageable) {
        return cabinetRepository.findAll(pageable);
    }

    public Cabinet getCabinetById(Long id) {
        return cabinetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id: " + id));
    }

    @Transactional
    public Cabinet addCabinet(CabinetDto cabinetDto) {
        Cabinet cabinet = toEntity(cabinetDto);
        validateNewCabinet(cabinet);
        return cabinetRepository.save(cabinet);
    }

    private void validateNewCabinet(Cabinet cabinet) {
        if (cabinetRepository.existsByLocation(cabinet.getLongitude(), cabinet.getLatitude())) {
            throw new IllegalArgumentException("A cabinet already exists at the provided location");
        }
        if (cabinetRepository.existsByNomAndLocation(
                cabinet.getNom(), cabinet.getLongitude(), cabinet.getLatitude())) {
            throw new IllegalArgumentException("A cabinet with the same name and location already exists");
        }
    }

    @Transactional
    public Cabinet updateCabinet(Long id, CabinetDto cabinetDto) {
        Cabinet existingCabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id: " + id));

        updateCabinetFields(existingCabinet, cabinetDto);
        return cabinetRepository.save(existingCabinet);
    }

    private void updateCabinetFields(Cabinet existingCabinet, CabinetDto updatedCabinet) {
        if (updatedCabinet.getNom() != null) {
            existingCabinet.setNom(updatedCabinet.getNom());
        }
        if (updatedCabinet.getWillaya() != null) {
            existingCabinet.setWillaya(updatedCabinet.getWillaya());
        }
        if (updatedCabinet.getMoughataa() != null) {
            existingCabinet.setMoughataa(updatedCabinet.getMoughataa());
        }
        if (updatedCabinet.getLongitude() != null) {
            existingCabinet.setLongitude(updatedCabinet.getLongitude());
        }
        if (updatedCabinet.getLatitude() != null) {
            existingCabinet.setLatitude(updatedCabinet.getLatitude());
        }
    }

    @Transactional
    public void deleteCabinet(Long id) {
        if (!cabinetRepository.existsById(id)) {
            throw new EntityNotFoundException("Cabinet not found with id: " + id);
        }
        cabinetRepository.deleteById(id);
    }

    private Cabinet toEntity(CabinetDto dto) {
        Cabinet cabinet = new Cabinet();
        cabinet.setId(dto.getId());
        cabinet.setNom(dto.getNom());
        cabinet.setWillaya(dto.getWillaya());
        cabinet.setMoughataa(dto.getMoughataa());
        cabinet.setLongitude(dto.getLongitude());
        cabinet.setLatitude(dto.getLatitude());
        return cabinet;
    }
}