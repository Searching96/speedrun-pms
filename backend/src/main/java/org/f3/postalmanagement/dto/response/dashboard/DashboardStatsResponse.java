package org.f3.postalmanagement.dto.response.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalOrders;
    private long totalCustomers;
    private long totalOffices;
    private BigDecimal totalRevenue;
    private Map<String, Long> ordersByStatus;
    private Map<String, Long> recentGrowth; // e.g. "2024-01": 10
}
