package mr.iscae.specifications;

import mr.iscae.entities.Pharmacy;
import org.springframework.data.jpa.domain.Specification;

public class PharmacySpecification {

    public static Specification<Pharmacy> withName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Pharmacy> withWillaya(String willaya) {
        return (root, query, cb) -> {
            if (willaya == null || willaya.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("willaya"), willaya);
        };
    }

    public static Specification<Pharmacy> withMoughataa(String moughataa) {
        return (root, query, cb) -> {
            if (moughataa == null || moughataa.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("moughataa"), moughataa);
        };
    }

    public static Specification<Pharmacy> isOpenTonight(Boolean isOpenTonight) {
        return (root, query, cb) -> {
            if (isOpenTonight == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("isOpenTonight"), isOpenTonight);
        };
    }
}