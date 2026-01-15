package org.f3.postalmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.employee.ward.CreateShipperRequest;
import org.f3.postalmanagement.dto.request.employee.ward.CreateWardManagerEmployeeRequest;
import org.f3.postalmanagement.dto.request.employee.ward.CreateWardStaffRequest;
import org.f3.postalmanagement.dto.request.employee.ward.UpdateEmployeeRequest;
import org.f3.postalmanagement.dto.response.employee.EmployeeResponse;
import org.f3.postalmanagement.entity.ApiResponse;
import org.f3.postalmanagement.entity.actor.CustomUserDetails;
import org.f3.postalmanagement.service.IWardManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ward-manager")
@RequiredArgsConstructor
@Tag(name = "Ward Manager Management", description = "API for Ward Managers (PO_WARD_MANAGER, WH_WARD_MANAGER) to manage employees in their office.")
@SecurityRequirement(name = "bearerAuth")
public class WardManagerController {

    private final IWardManagerService wardManagerService;

    @PostMapping("/employees/staff")
    @PreAuthorize("hasAnyRole('PO_WARD_MANAGER', 'WH_WARD_MANAGER')")
    @Operation(
            summary = "Create a new staff in the same office",
            description = "Create a new staff member in the Ward Manager's office. " +
                    "PO_WARD_MANAGER creates PO_STAFF in the same WARD_POST. " +
                    "WH_WARD_MANAGER creates WH_STAFF in the same WARD_WAREHOUSE."
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> createStaff(
            @Valid @RequestBody CreateWardStaffRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        EmployeeResponse response = wardManagerService.createStaff(request, userDetails.getAccount());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Staff created successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/employees/ward-manager")
    @PreAuthorize("hasAnyRole('PO_WARD_MANAGER', 'WH_WARD_MANAGER')")
    @Operation(
            summary = "Create a new ward manager in the same office",
            description = "Create a new ward manager in the same office. " +
                    "PO_WARD_MANAGER creates PO_WARD_MANAGER in the same WARD_POST. " +
                    "WH_WARD_MANAGER creates WH_WARD_MANAGER in the same WARD_WAREHOUSE."
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> createWardManager(
            @Valid @RequestBody CreateWardManagerEmployeeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        EmployeeResponse response = wardManagerService.createWardManager(request, userDetails.getAccount());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Ward Manager created successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/employees/shipper")
    @PreAuthorize("hasRole('PO_WARD_MANAGER')")
    @Operation(
            summary = "Create a new shipper in the same office",
            description = "Create a new shipper (delivery person) in the Ward Manager's office. " +
                    "Only PO_WARD_MANAGER can create shippers in their WARD_POST."
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> createShipper(
            @Valid @RequestBody CreateShipperRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        EmployeeResponse response = wardManagerService.createShipper(request, userDetails.getAccount());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Shipper created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('PO_WARD_MANAGER', 'WH_WARD_MANAGER')")
    @Operation(
            summary = "Get list of employees in the same office",
            description = "Get a list of all employees (Staff, Shipper, Ward Manager) in the same office."
    )
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployees(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<EmployeeResponse> response = wardManagerService.getEmployees(userDetails.getAccount());

        return ResponseEntity.ok(
                ApiResponse.<List<EmployeeResponse>>builder()
                        .success(true)
                        .message("Employees retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('PO_WARD_MANAGER', 'WH_WARD_MANAGER')")
    @Operation(
            summary = "Get employee details",
            description = "Get details of a specific employee in the same office."
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployee(
            @PathVariable UUID employeeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        EmployeeResponse response = wardManagerService.getEmployee(employeeId, userDetails.getAccount());

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee details retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('PO_WARD_MANAGER', 'WH_WARD_MANAGER')")
    @Operation(
            summary = "Update employee details",
            description = "Update an employee's full name, phone number, and email. Password can also be updated."
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable UUID employeeId,
            @Valid @RequestBody UpdateEmployeeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        EmployeeResponse response = wardManagerService.updateEmployee(employeeId, request, userDetails.getAccount());

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('PO_WARD_MANAGER', 'WH_WARD_MANAGER')")
    @Operation(
            summary = "Delete an employee",
            description = "Soft delete an employee and deactivate their account. Cannot delete self."
    )
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @PathVariable UUID employeeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        wardManagerService.deleteEmployee(employeeId, userDetails.getAccount());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Employee deleted successfully")
                        .build()
        );
    }
}

