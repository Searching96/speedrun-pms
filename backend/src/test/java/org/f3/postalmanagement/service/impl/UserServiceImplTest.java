package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.dto.response.user.CustomerMeResponse;
import org.f3.postalmanagement.dto.response.user.EmployeeMeResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.administrative.AdministrativeRegion;
import org.f3.postalmanagement.entity.administrative.Province;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.enums.OfficeType;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.enums.SubscriptionPlan;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Path Coverage Tests")
class UserServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private Account customerAccount;
    private Account employeeAccount;
    private Account adminAccount;
    private Customer customer;
    private Employee employee;
    private Office office;
    private AdministrativeRegion region;
    private Province province;

    @BeforeEach
    void setUp() {
        customerAccount = new Account();
        customerAccount.setId(UUID.randomUUID());
        customerAccount.setUsername("0123456789");
        customerAccount.setEmail("customer@test.com");
        customerAccount.setRole(Role.CUSTOMER);
        customerAccount.setActive(true);

        employeeAccount = new Account();
        employeeAccount.setId(UUID.randomUUID());
        employeeAccount.setUsername("employee@test.com");
        employeeAccount.setEmail("employee@test.com");
        employeeAccount.setRole(Role.PO_STAFF);
        employeeAccount.setActive(true);

        adminAccount = new Account();
        adminAccount.setId(UUID.randomUUID());
        adminAccount.setUsername("admin");
        adminAccount.setEmail("admin@test.com");
        adminAccount.setRole(Role.SYSTEM_ADMIN);
        adminAccount.setActive(true);

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setAccount(customerAccount);
        customer.setFullName("Test Customer");
        customer.setPhoneNumber("0123456789");
        customer.setAddress("123 Test St");
        customer.setSubscriptionPlan(SubscriptionPlan.BASIC);

        region = new AdministrativeRegion();
        region.setId(1); // AdministrativeRegion uses Integer ID
        region.setName("North Region");

        province = new Province();
        province.setCode("01");
        province.setName("Test Province");

        office = new Office();
        office.setId(UUID.randomUUID());
        office.setOfficeName("Test Office");
        office.setOfficeEmail("office@test.com");
        office.setOfficePhoneNumber("0111222333");
        office.setOfficeAddress("456 Office St");
        office.setOfficeType(OfficeType.WARD_POST);
        office.setRegion(region);
        office.setProvince(province);

        employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setAccount(employeeAccount);
        employee.setFullName("Test Employee");
        employee.setPhoneNumber("0987654321");
        employee.setOffice(office);
    }

    // ==================== fetchMe Tests ====================
    @Nested
    @DisplayName("fetchMe()")
    class FetchMeTests {

        @Test
        @DisplayName("Path 1: Success - Customer role returns CustomerMeResponse")
        void fetchMe_AsCustomer_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));

                Object result = userService.fetchMe();

                assertThat(result).isInstanceOf(CustomerMeResponse.class);
                CustomerMeResponse response = (CustomerMeResponse) result;
                assertThat(response.getRole()).isEqualTo("CUSTOMER");
                assertThat(response.getFullName()).isEqualTo("Test Customer");
                assertThat(response.getSubscriptionPlan()).isEqualTo("BASIC");
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Customer profile not found")
        void fetchMe_CustomerNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> userService.fetchMe())
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Customer record not found");
            }
        }

        @Test
        @DisplayName("Path 3: Success - SYSTEM_ADMIN returns EmployeeMeResponse without office")
        void fetchMe_AsSystemAdmin_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(adminAccount);

                Object result = userService.fetchMe();

                assertThat(result).isInstanceOf(EmployeeMeResponse.class);
                EmployeeMeResponse response = (EmployeeMeResponse) result;
                assertThat(response.getRole()).isEqualTo("SYSTEM_ADMIN");
                assertThat(response.getFullName()).isEqualTo("System Administrator");
                assertThat(response.getOffice()).isNull();
            }
        }

        @Test
        @DisplayName("Path 4: Success - Employee with full office info")
        void fetchMe_AsEmployee_WithOffice_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(employeeAccount);
                when(employeeRepository.findByAccount(employeeAccount)).thenReturn(Optional.of(employee));

                Object result = userService.fetchMe();

                assertThat(result).isInstanceOf(EmployeeMeResponse.class);
                EmployeeMeResponse response = (EmployeeMeResponse) result;
                assertThat(response.getRole()).isEqualTo("PO_STAFF");
                assertThat(response.getFullName()).isEqualTo("Test Employee");
                assertThat(response.getOffice()).isNotNull();
                assertThat(response.getOffice().getName()).isEqualTo("Test Office");
                assertThat(response.getOffice().getRegion()).isNotNull();
                assertThat(response.getOffice().getProvince()).isNotNull();
            }
        }

        @Test
        @DisplayName("Path 5: Failure - Employee record not found")
        void fetchMe_EmployeeNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(employeeAccount);
                when(employeeRepository.findByAccount(employeeAccount)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> userService.fetchMe())
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Employee record not found");
            }
        }

        @Test
        @DisplayName("Path 6: Success - Employee without office")
        void fetchMe_AsEmployee_WithoutOffice_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                employee.setOffice(null);
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(employeeAccount);
                when(employeeRepository.findByAccount(employeeAccount)).thenReturn(Optional.of(employee));

                Object result = userService.fetchMe();

                assertThat(result).isInstanceOf(EmployeeMeResponse.class);
                EmployeeMeResponse response = (EmployeeMeResponse) result;
                assertThat(response.getOffice()).isNull();
            }
        }

        @Test
        @DisplayName("Path 7: Success - Employee with office but no region")
        void fetchMe_AsEmployee_OfficeNoRegion_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                office.setRegion(null);
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(employeeAccount);
                when(employeeRepository.findByAccount(employeeAccount)).thenReturn(Optional.of(employee));

                Object result = userService.fetchMe();

                assertThat(result).isInstanceOf(EmployeeMeResponse.class);
                EmployeeMeResponse response = (EmployeeMeResponse) result;
                assertThat(response.getOffice()).isNotNull();
                assertThat(response.getOffice().getRegion()).isNull();
            }
        }

        @Test
        @DisplayName("Path 8: Success - Employee with office but no province")
        void fetchMe_AsEmployee_OfficeNoProvince_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                office.setProvince(null);
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(employeeAccount);
                when(employeeRepository.findByAccount(employeeAccount)).thenReturn(Optional.of(employee));

                Object result = userService.fetchMe();

                assertThat(result).isInstanceOf(EmployeeMeResponse.class);
                EmployeeMeResponse response = (EmployeeMeResponse) result;
                assertThat(response.getOffice()).isNotNull();
                assertThat(response.getOffice().getProvince()).isNull();
            }
        }
    }
}
