package mr.iscae.specifications;

import mr.iscae.entities.Cabinet;
import org.springframework.data.jpa.domain.Specification;

public class CabinetSpecification {

    public static Specification<Cabinet> withName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("nom")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Cabinet> withWillaya(String willaya) {
        return (root, query, cb) -> {
            if (willaya == null || willaya.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("willaya"), willaya);
        };
    }

    public static Specification<Cabinet> withMoughataa(String moughataa) {
        return (root, query, cb) -> {
            if (moughataa == null || moughataa.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("moughataa"), moughataa);
        };
    }
}
