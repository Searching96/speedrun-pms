package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.dto.request.employee.ward.CreateShipperRequest;
import org.f3.postalmanagement.dto.request.employee.ward.CreateWardManagerEmployeeRequest;
import org.f3.postalmanagement.dto.request.employee.ward.CreateWardStaffRequest;
import org.f3.postalmanagement.dto.response.employee.EmployeeResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.enums.OfficeType;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.AccountRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
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
@DisplayName("WardManagerServiceImpl Path Coverage Tests")
class WardManagerServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private WardManagerServiceImpl wardManagerService;

    private Account poWardManagerAccount;
    private Account whWardManagerAccount;
    private Account staffAccount;
    private Employee poWardManagerEmployee;
    private Employee whWardManagerEmployee;
    private Office wardPostOffice;
    private Office wardWarehouseOffice;

    @BeforeEach
    void setUp() {
        wardPostOffice = new Office();
        wardPostOffice.setId(UUID.randomUUID());
        wardPostOffice.setOfficeName("Ward Post Office");
        wardPostOffice.setOfficeType(OfficeType.WARD_POST);

        wardWarehouseOffice = new Office();
        wardWarehouseOffice.setId(UUID.randomUUID());
        wardWarehouseOffice.setOfficeName("Ward Warehouse");
        wardWarehouseOffice.setOfficeType(OfficeType.WARD_WAREHOUSE);

        poWardManagerAccount = new Account();
        poWardManagerAccount.setId(UUID.randomUUID());
        poWardManagerAccount.setUsername("po_manager");
        poWardManagerAccount.setRole(Role.PO_WARD_MANAGER);

        whWardManagerAccount = new Account();
        whWardManagerAccount.setId(UUID.randomUUID());
        whWardManagerAccount.setUsername("wh_manager");
        whWardManagerAccount.setRole(Role.WH_WARD_MANAGER);

        staffAccount = new Account();
        staffAccount.setId(UUID.randomUUID());
        staffAccount.setRole(Role.PO_STAFF);

        poWardManagerEmployee = new Employee();
        poWardManagerEmployee.setId(poWardManagerAccount.getId());
        poWardManagerEmployee.setAccount(poWardManagerAccount);
        poWardManagerEmployee.setOffice(wardPostOffice);

        whWardManagerEmployee = new Employee();
        whWardManagerEmployee.setId(whWardManagerAccount.getId());
        whWardManagerEmployee.setAccount(whWardManagerAccount);
        whWardManagerEmployee.setOffice(wardWarehouseOffice);
    }

    // ==================== createStaff Tests ====================
    @Nested
    @DisplayName("createStaff()")
    class CreateStaffTests {

        private CreateWardStaffRequest createRequest() {
            CreateWardStaffRequest request = new CreateWardStaffRequest();
            request.setFullName("New Staff");
            request.setPhoneNumber("0123456789");
            request.setPassword("password");
            request.setEmail("staff@test.com");
            return request;
        }

        @Test
        @DisplayName("Path 1: PO_WARD_MANAGER creates PO_STAFF")
        void createStaff_AsPOManager_CreatesPOStaff() {
            CreateWardStaffRequest request = createRequest();

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.of(poWardManagerEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
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

            EmployeeResponse result = wardManagerService.createStaff(request, poWardManagerAccount);

            assertThat(result.getRole()).isEqualTo("PO_STAFF");
            assertThat(result.getOfficeName()).isEqualTo("Ward Post Office");
        }

        @Test
        @DisplayName("Path 2: WH_WARD_MANAGER creates WH_STAFF")
        void createStaff_AsWHManager_CreatesWHStaff() {
            CreateWardStaffRequest request = createRequest();

            when(employeeRepository.findById(whWardManagerAccount.getId())).thenReturn(Optional.of(whWardManagerEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
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

            EmployeeResponse result = wardManagerService.createStaff(request, whWardManagerAccount);

            assertThat(result.getRole()).isEqualTo("WH_STAFF");
            assertThat(result.getOfficeName()).isEqualTo("Ward Warehouse");
        }

        @Test
        @DisplayName("Path 3: Invalid role cannot create staff")
        void createStaff_InvalidRole_ThrowsException() {
            CreateWardStaffRequest request = createRequest();

            assertThatThrownBy(() -> wardManagerService.createStaff(request, staffAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot determine staff role");
        }

        @Test
        @DisplayName("Path 4: Employee record not found")
        void createStaff_EmployeeNotFound_ThrowsException() {
            CreateWardStaffRequest request = createRequest();

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> wardManagerService.createStaff(request, poWardManagerAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Employee record not found");
        }

        @Test
        @DisplayName("Path 5: Manager has no office")
        void createStaff_NoOffice_ThrowsException() {
            CreateWardStaffRequest request = createRequest();
            poWardManagerEmployee.setOffice(null);

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.of(poWardManagerEmployee));

            assertThatThrownBy(() -> wardManagerService.createStaff(request, poWardManagerAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no assigned office");
        }

        @Test
        @DisplayName("Path 6: Phone number already exists")
        void createStaff_PhoneExists_ThrowsException() {
            CreateWardStaffRequest request = createRequest();

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.of(poWardManagerEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(true);

            assertThatThrownBy(() -> wardManagerService.createStaff(request, poWardManagerAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Phone number already registered");
        }

        @Test
        @DisplayName("Path 7: Email already exists")
        void createStaff_EmailExists_ThrowsException() {
            CreateWardStaffRequest request = createRequest();

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.of(poWardManagerEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> wardManagerService.createStaff(request, poWardManagerAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already registered");
        }

        @Test
        @DisplayName("Path 8: Wrong office type for role")
        void createStaff_WrongOfficeType_ThrowsException() {
            CreateWardStaffRequest request = createRequest();
            poWardManagerEmployee.setOffice(wardWarehouseOffice); // Wrong office type

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.of(poWardManagerEmployee));

            assertThatThrownBy(() -> wardManagerService.createStaff(request, poWardManagerAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be assigned to office of type");
        }
    }

    // ==================== createWardManager Tests ====================
    @Nested
    @DisplayName("createWardManager()")
    class CreateWardManagerTests {

        private CreateWardManagerEmployeeRequest createRequest() {
            CreateWardManagerEmployeeRequest request = new CreateWardManagerEmployeeRequest();
            request.setFullName("New Manager");
            request.setPhoneNumber("0987654321");
            request.setPassword("password");
            request.setEmail("manager@test.com");
            return request;
        }

        @Test
        @DisplayName("Path 1: PO_WARD_MANAGER creates another PO_WARD_MANAGER")
        void createWardManager_AsPOManager_Success() {
            CreateWardManagerEmployeeRequest request = createRequest();

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.of(poWardManagerEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
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

            EmployeeResponse result = wardManagerService.createWardManager(request, poWardManagerAccount);

            assertThat(result.getRole()).isEqualTo("PO_WARD_MANAGER");
        }

        @Test
        @DisplayName("Path 2: WH_WARD_MANAGER creates another WH_WARD_MANAGER")
        void createWardManager_AsWHManager_Success() {
            CreateWardManagerEmployeeRequest request = createRequest();

            when(employeeRepository.findById(whWardManagerAccount.getId())).thenReturn(Optional.of(whWardManagerEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
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

            EmployeeResponse result = wardManagerService.createWardManager(request, whWardManagerAccount);

            assertThat(result.getRole()).isEqualTo("WH_WARD_MANAGER");
        }

        @Test
        @DisplayName("Path 3: Non-manager role cannot create ward manager")
        void createWardManager_AsStaff_ThrowsException() {
            CreateWardManagerEmployeeRequest request = createRequest();

            assertThatThrownBy(() -> wardManagerService.createWardManager(request, staffAccount))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only Ward Managers can create");
        }
    }

    // ==================== createShipper Tests ====================
    @Nested
    @DisplayName("createShipper()")
    class CreateShipperTests {

        private CreateShipperRequest createRequest() {
            CreateShipperRequest request = new CreateShipperRequest();
            request.setFullName("New Shipper");
            request.setPhoneNumber("0111222333");
            request.setPassword("password");
            request.setEmail("shipper@test.com");
            return request;
        }

        @Test
        @DisplayName("Path 1: PO_WARD_MANAGER creates shipper")
        void createShipper_AsPOManager_Success() {
            CreateShipperRequest request = createRequest();

            when(employeeRepository.findById(poWardManagerAccount.getId())).thenReturn(Optional.of(poWardManagerEmployee));
            when(accountRepository.existsByUsername(request.getPhoneNumber())).thenReturn(false);
            when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
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

            EmployeeResponse result = wardManagerService.createShipper(request, poWardManagerAccount);

            assertThat(result.getRole()).isEqualTo("SHIPPER");
        }

        @Test
        @DisplayName("Path 2: WH_WARD_MANAGER cannot create shipper")
        void createShipper_AsWHManager_ThrowsException() {
            CreateShipperRequest request = createRequest();

            assertThatThrownBy(() -> wardManagerService.createShipper(request, whWardManagerAccount))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only PO Ward Managers can create Shippers");
        }

        @Test
        @DisplayName("Path 3: Non-manager role cannot create shipper")
        void createShipper_AsStaff_ThrowsException() {
            CreateShipperRequest request = createRequest();

            assertThatThrownBy(() -> wardManagerService.createShipper(request, staffAccount))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only PO Ward Managers");
        }
    }
}
