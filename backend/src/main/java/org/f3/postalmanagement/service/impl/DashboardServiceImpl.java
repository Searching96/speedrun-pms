package org.f3.postalmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.f3.postalmanagement.dto.request.user.RegisterSystemAdminRequest;
import org.f3.postalmanagement.dto.response.dashboard.DashboardStatsResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.AccountRepository;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.OfficeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.service.IDashboardService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OfficeRepository officeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registerNewAdmin(RegisterSystemAdminRequest request) {
        validateRequest(request);

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setRole(Role.SYSTEM_ADMIN);
        account.setActive(true);
        accountRepository.save(account);

        log.info("Admin registered successfully: {}", request.getUsername());
    }

    @Override
    public DashboardStatsResponse getSystemStats() {
        log.info("Fetching global system stats for admin");
        
        long totalOrders = orderRepository.count();
        long totalCustomers = customerRepository.count();
        long totalOffices = officeRepository.count();
        
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(o -> o.getShippingFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> ordersByStatus = orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

        // Calculate month-over-month growth (simulated)
        Map<String, Long> recentGrowth = new HashMap<>();
        recentGrowth.put("last_month", 18L);
        recentGrowth.put("current_month", 20L);
        recentGrowth.put("growth_percentage", 11L); // ~11% growth

        return DashboardStatsResponse.builder()
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalOffices(totalOffices)
                .totalRevenue(totalRevenue)
                .ordersByStatus(ordersByStatus)
                .recentGrowth(recentGrowth)
                .build();
    }

    private void validateRequest(RegisterSystemAdminRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
