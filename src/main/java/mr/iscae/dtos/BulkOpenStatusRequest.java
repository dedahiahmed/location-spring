package mr.iscae.dtos;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOpenStatusRequest {
    private List<Long> pharmacyIds;
    private boolean isOpenTonight;
}
