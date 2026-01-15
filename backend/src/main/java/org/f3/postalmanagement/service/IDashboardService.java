package org.f3.postalmanagement.service;

import org.f3.postalmanagement.dto.request.user.RegisterSystemAdminRequest;
import org.f3.postalmanagement.dto.response.dashboard.DashboardStatsResponse;

public interface IDashboardService {

    void registerNewAdmin(RegisterSystemAdminRequest request);

    DashboardStatsResponse getSystemStats();

}
