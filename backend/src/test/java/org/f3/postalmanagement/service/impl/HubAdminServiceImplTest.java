package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.dto.request.employee.hub.RegisterHubAdminRequest;
import org.f3.postalmanagement.dto.response.employee.EmployeeResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.administrative.AdministrativeRegion;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.enums.OfficeType;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.AccountRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OfficeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HubAdminServiceImpl Path Coverage Tests")
class HubAdminServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HubAdminServiceImpl hubAdminService;

    private Account systemAdminAccount;
    private Account hubAdminAccount;
    private Account staffAccount;
    private Employee hubAdminEmployee;
    private Office hubOffice;
    private Office otherHubOffice;
    private Office wardOffice;
    private AdministrativeRegion region1;
    private AdministrativeRegion region2;

    @BeforeEach
    void setUp() {
        region1 = new AdministrativeRegion();
        region1.setId(1);
        region1.setName("Region 1");

        region2 = new AdministrativeRegion();
        region2.setId(2);
        region2.setName("Region 2");

        hubOffice = new Office();
        hubOffice.setId(UUID.randomUUID());
        hubOffice.setOfficeName("Hub Office 1");
        hubOffice.setOfficeType(OfficeType.HUB);
        hubOffice.setRegion(region1);

        otherHubOffice = new Office();
        otherHubOffice.setId(UUID.randomUUID());
        otherHubOffice.setOfficeName("Hub Office 2");
        otherHubOffice.setOfficeType(OfficeType.HUB);
        otherHubOffice.setRegion(region2);

        wardOffice = new Office();
        wardOffice.setId(UUID.randomUUID());
        wardOffice.setOfficeName("Ward Office");
        wardOffice.setOfficeType(OfficeType.WARD_POST);
        wardOffice.setRegion(region1);

        systemAdminAccount = new Account();
        systemAdminAccount.setId(UUID.randomUUID());
        systemAdminAccount.setRole(Role.SYSTEM_ADMIN);

        hubAdminAccount = new Account();
        hubAdminAccount.setId(UUID.randomUUID());
        hubAdminAccount.setRole(Role.HUB_ADMIN);

        staffAccount = new Account();
        staffAccount.setId(UUID.randomUUID());
        staffAccount.setRole(Role.PO_STAFF);

        hubAdminEmployee = new Employee();
        hubAdminEmployee.setId(hubAdminAccount.getId());
        hubAdminEmployee.setAccount(hubAdminAccount);
        hubAdminEmployee.setOffice(hubOffice);
    }

    private RegisterHubAdminRequest createValidRequest(UUID officeId) {
        RegisterHubAdminRequest request = new RegisterHubAdminRequest();
        request.setOfficeId(officeId);
        request.setPhoneNumber("0123456789");
        request.setPassword("password123");
        request.setEmail("newhubadmin@test.com");
        request.setFullName("New Hub Admin");
        return request;
    }

    // ==================== registerHubAdmin Tests ====================
    @Nested
    @DisplayName("registerHubAdmin()")
    class RegisterHubAdminTests {

        @Test
        @DisplayName("Path 1: Success - SYSTEM_ADMIN registers hub admin for any region")
        void registerHubAdmin_AsSystemAdmin_Success() {
            RegisterHubAdminRequest request = createValidRequest(hubOffice.getId());

            when(officeRepository.findById(hubOffice.getId())).thenReturn(Optional.of(hubOffice));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
                Account a = inv.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });
            when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
                Employee e = inv.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = hubAdminService.registerHubAdmin(request, systemAdminAccount);

            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo("HUB_ADMIN");
            assertThat(result.getOfficeName()).isEqualTo("Hub Office 1");
        }

        @Test
        @DisplayName("Path 2: Success - HUB_ADMIN registers for same region")
        void registerHubAdmin_AsHubAdmin_SameRegion_Success() {
            // Create another hub in the same region
            Office sameRegionHub = new Office();
            sameRegionHub.setId(UUID.randomUUID());
            sameRegionHub.setOfficeName("Same Region Hub");
            sameRegionHub.setOfficeType(OfficeType.HUB);
            sameRegionHub.setRegion(region1);

            RegisterHubAdminRequest request = createValidRequest(sameRegionHub.getId());

            when(officeRepository.findById(sameRegionHub.getId())).thenReturn(Optional.of(sameRegionHub));
            when(employeeRepository.findById(hubAdminAccount.getId())).thenReturn(Optional.of(hubAdminEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
                Account a = inv.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });
            when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
                Employee e = inv.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = hubAdminService.registerHubAdmin(request, hubAdminAccount);

            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo("HUB_ADMIN");
        }

        @Test
        @DisplayName("Path 3: Failure - Office not found")
        void registerHubAdmin_OfficeNotFound_ThrowsException() {
            RegisterHubAdminRequest request = createValidRequest(UUID.randomUUID());

            when(officeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> hubAdminService.registerHubAdmin(request, systemAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Office not found");
        }

        @Test
        @DisplayName("Path 4: Failure - Office is not a HUB")
        void registerHubAdmin_OfficeNotHub_ThrowsException() {
            RegisterHubAdminRequest request = createValidRequest(wardOffice.getId());

            when(officeRepository.findById(wardOffice.getId())).thenReturn(Optional.of(wardOffice));

            assertThatThrownBy(() -> hubAdminService.registerHubAdmin(request, systemAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("is not a HUB");
        }

        @Test
        @DisplayName("Path 5: Failure - HUB_ADMIN for different region")
        void registerHubAdmin_AsHubAdmin_DifferentRegion_ThrowsException() {
            RegisterHubAdminRequest request = createValidRequest(otherHubOffice.getId());

            when(officeRepository.findById(otherHubOffice.getId())).thenReturn(Optional.of(otherHubOffice));
            when(employeeRepository.findById(hubAdminAccount.getId())).thenReturn(Optional.of(hubAdminEmployee));

            assertThatThrownBy(() -> hubAdminService.registerHubAdmin(request, hubAdminAccount))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("only register HUB admins for your own region");
        }

        @Test
        @DisplayName("Path 6: Failure - Unauthorized role (PO_STAFF)")
        void registerHubAdmin_AsStaff_ThrowsException() {
            RegisterHubAdminRequest request = createValidRequest(hubOffice.getId());

            when(officeRepository.findById(hubOffice.getId())).thenReturn(Optional.of(hubOffice));

            assertThatThrownBy(() -> hubAdminService.registerHubAdmin(request, staffAccount))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("Path 7: Failure - Phone number already registered")
        void registerHubAdmin_PhoneExists_ThrowsException() {
            RegisterHubAdminRequest request = createValidRequest(hubOffice.getId());

            when(officeRepository.findById(hubOffice.getId())).thenReturn(Optional.of(hubOffice));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(true);

            assertThatThrownBy(() -> hubAdminService.registerHubAdmin(request, systemAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Phone number already registered");
        }

        @Test
        @DisplayName("Path 8: Failure - Email already registered")
        void registerHubAdmin_EmailExists_ThrowsException() {
            RegisterHubAdminRequest request = createValidRequest(hubOffice.getId());

            when(officeRepository.findById(hubOffice.getId())).thenReturn(Optional.of(hubOffice));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> hubAdminService.registerHubAdmin(request, systemAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already registered");
        }

        @Test
        @DisplayName("Path 9: Failure - HUB_ADMIN employee record not found")
        void registerHubAdmin_AsHubAdmin_EmployeeNotFound_ThrowsException() {
            RegisterHubAdminRequest request = createValidRequest(hubOffice.getId());

            when(officeRepository.findById(hubOffice.getId())).thenReturn(Optional.of(hubOffice));
            when(employeeRepository.findById(hubAdminAccount.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> hubAdminService.registerHubAdmin(request, hubAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Employee record not found");
        }
    }
}
