package org.f3.postalmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TrackingResponse {
    private OrderResponse order;
    private List<TrackingEventResponse> events;
}
