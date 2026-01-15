package org.f3.postalmanagement.service;

import org.f3.postalmanagement.dto.request.employee.ward.CreateShipperRequest;
import org.f3.postalmanagement.dto.request.employee.ward.CreateWardManagerEmployeeRequest;
import org.f3.postalmanagement.dto.request.employee.ward.CreateWardStaffRequest;
import org.f3.postalmanagement.dto.request.employee.ward.UpdateEmployeeRequest;
import org.f3.postalmanagement.dto.response.employee.EmployeeResponse;
import org.f3.postalmanagement.entity.actor.Account;

import java.util.List;
import java.util.UUID;

public interface IWardManagerService {

    /**
     * Create a new Staff by Ward Manager.
     * The staff will be created in the same office as the Ward Manager.
     * 
     * PO_WARD_MANAGER creates PO_STAFF in the same WARD_POST.
     * WH_WARD_MANAGER creates WH_STAFF in the same WARD_WAREHOUSE.
     *
     * @param request the staff creation request
     * @param currentAccount the account of the user making the request
     * @return the created employee response
     */
    EmployeeResponse createStaff(CreateWardStaffRequest request, Account currentAccount);

    /**
     * Create a new Ward Manager by existing Ward Manager.
     * The new ward manager will be created in the same office as the current Ward Manager.
     * 
     * PO_WARD_MANAGER creates PO_WARD_MANAGER in the same WARD_POST.
     * WH_WARD_MANAGER creates WH_WARD_MANAGER in the same WARD_WAREHOUSE.
     *
     * @param request the ward manager creation request
     * @param currentAccount the account of the user making the request
     * @return the created employee response
     */
    EmployeeResponse createWardManager(CreateWardManagerEmployeeRequest request, Account currentAccount);

    /**
     * Create a new Shipper by Ward Manager.
     * The shipper will be created in the same office as the Ward Manager.
     * Only PO_WARD_MANAGER can create shippers (in WARD_POST offices).
     *
     * @param request the shipper creation request
     * @param currentAccount the account of the user making the request
     * @return the created employee response
     */
    EmployeeResponse createShipper(CreateShipperRequest request, Account currentAccount);

    /**
     * Get list of employees in the same office as the current Ward Manager.
     *
     * @param currentAccount the account of the user making the request
     * @return list of employees
     */
    List<EmployeeResponse> getEmployees(Account currentAccount);

    /**
     * Get details of a specific employee in the same office as the current Ward Manager.
     *
     * @param employeeId the ID of the employee to retrieve
     * @param currentAccount the account of the user making the request
     * @return employee details
     */
    EmployeeResponse getEmployee(UUID employeeId, Account currentAccount);

    /**
     * Update an employee's details.
     *
     * @param employeeId the ID of the employee to update
     * @param request the update request
     * @param currentAccount the account of the user making the request
     * @return updated employee response
     */
    EmployeeResponse updateEmployee(UUID employeeId, UpdateEmployeeRequest request, Account currentAccount);

    /**
     * Delete an employee.
     *
     * @param employeeId the ID of the employee to delete
     * @param currentAccount the account of the user making the request
     */
    void deleteEmployee(UUID employeeId, Account currentAccount);
}

