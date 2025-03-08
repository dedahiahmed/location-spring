package mr.iscae.specifications;

import mr.iscae.entities.Doctor;
import org.springframework.data.jpa.domain.Specification;

public class DoctorSpecification {

    public static Specification<Doctor> withName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Doctor> withSpeciality(String speciality) {
        return (root, query, cb) -> {
            if (speciality == null || speciality.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("speciality"), speciality);
        };
    }
}