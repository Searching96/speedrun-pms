package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.dto.request.employee.province.CreateProvinceAdminRequest;
import org.f3.postalmanagement.dto.request.employee.province.CreateStaffRequest;
import org.f3.postalmanagement.dto.request.employee.province.CreateWardManagerRequest;
import org.f3.postalmanagement.dto.request.office.AssignWardsRequest;
import org.f3.postalmanagement.dto.request.office.CreateWardOfficeRequest;
import org.f3.postalmanagement.dto.response.employee.EmployeeResponse;
import org.f3.postalmanagement.dto.response.office.WardOfficePairResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.administrative.Province;
import org.f3.postalmanagement.entity.administrative.Ward;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.entity.unit.OfficePair;
import org.f3.postalmanagement.entity.unit.WardOfficeAssignment;
import org.f3.postalmanagement.enums.OfficeType;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.*;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProvinceAdminServiceImpl Path Coverage Tests")
class ProvinceAdminServiceImplTest {

    @Mock
    private OfficeRepository officeRepository;
    @Mock
    private OfficePairRepository officePairRepository;
    @Mock
    private WardOfficeAssignmentRepository wardOfficeAssignmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private WardRepository wardRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProvinceAdminServiceImpl provinceAdminService;

    private Account poAdminAccount;
    private Account whAdminAccount;
    private Account otherAccount;
    private Employee poAdminEmployee;
    private Employee whAdminEmployee;
    private Office poProvinceOffice;
    private Office whProvinceOffice;
    private Province province;

    @BeforeEach
    void setUp() {
        province = new Province();
        province.setCode("01");
        province.setName("Hà Nội");

        poProvinceOffice = new Office();
        poProvinceOffice.setId(UUID.randomUUID());
        poProvinceOffice.setOfficeName("PO Province");
        poProvinceOffice.setOfficeType(OfficeType.PROVINCE_POST);
        poProvinceOffice.setProvince(province);

        whProvinceOffice = new Office();
        whProvinceOffice.setId(UUID.randomUUID());
        whProvinceOffice.setOfficeName("WH Province");
        whProvinceOffice.setOfficeType(OfficeType.PROVINCE_WAREHOUSE);
        whProvinceOffice.setProvince(province);

        poAdminAccount = new Account();
        poAdminAccount.setId(UUID.randomUUID());
        poAdminAccount.setRole(Role.PO_PROVINCE_ADMIN);

        whAdminAccount = new Account();
        whAdminAccount.setId(UUID.randomUUID());
        whAdminAccount.setRole(Role.WH_PROVINCE_ADMIN);

        otherAccount = new Account();
        otherAccount.setId(UUID.randomUUID());
        otherAccount.setRole(Role.PO_STAFF);

        poAdminEmployee = new Employee();
        poAdminEmployee.setId(poAdminAccount.getId());
        poAdminEmployee.setAccount(poAdminAccount);
        poAdminEmployee.setOffice(poProvinceOffice);

        whAdminEmployee = new Employee();
        whAdminEmployee.setId(whAdminAccount.getId());
        whAdminEmployee.setAccount(whAdminAccount);
        whAdminEmployee.setOffice(whProvinceOffice);
    }

    @Nested
    @DisplayName("createProvinceAdmin()")
    class CreateProvinceAdminTests {
        @Test
        @DisplayName("Success - PO_PROVINCE_ADMIN creates another PO_PROVINCE_ADMIN")
        void createProvinceAdmin_POSuccess() {
            CreateProvinceAdminRequest request = new CreateProvinceAdminRequest();
            request.setFullName("New Admin");
            request.setPhoneNumber("0123456789");
            request.setPassword("pass");
            request.setEmail("admin@test.com");

            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(accountRepository.existsByUsername(any())).thenReturn(false);
            when(accountRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(employeeRepository.save(any())).thenAnswer(i -> {
                Employee e = i.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = provinceAdminService.createProvinceAdmin(request, poAdminAccount);

            assertThat(result.getRole()).isEqualTo("PO_PROVINCE_ADMIN");
        }

        @Test
        @DisplayName("Success - WH_PROVINCE_ADMIN creates WH_PROVINCE_ADMIN")
        void createProvinceAdmin_WHSuccess() {
            CreateProvinceAdminRequest request = new CreateProvinceAdminRequest();
            request.setFullName("New WH Admin");
            request.setPhoneNumber("0123456799");
            request.setPassword("pass");
            request.setEmail("whadmin@test.com");

            when(employeeRepository.findById(whAdminAccount.getId())).thenReturn(Optional.of(whAdminEmployee));
            when(accountRepository.existsByUsername(any())).thenReturn(false);
            when(accountRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(employeeRepository.save(any())).thenAnswer(i -> {
                Employee e = i.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = provinceAdminService.createProvinceAdmin(request, whAdminAccount);

            assertThat(result.getRole()).isEqualTo("WH_PROVINCE_ADMIN");
        }

        @Test
        @DisplayName("Failure - Unauthorized role")
        void createProvinceAdmin_Unauthorized() {
            assertThatThrownBy(() -> provinceAdminService.createProvinceAdmin(new CreateProvinceAdminRequest(), otherAccount))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("Failure - Invalid office type for Province Admin")
        void createProvinceAdmin_InvalidOfficeType() {
            CreateProvinceAdminRequest request = new CreateProvinceAdminRequest();
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            
            // Set invalid office type
            poProvinceOffice.setOfficeType(OfficeType.PROVINCE_WAREHOUSE);

            assertThatThrownBy(() -> provinceAdminService.createProvinceAdmin(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be assigned to office of type");
            
            // Reset for other tests
            poProvinceOffice.setOfficeType(OfficeType.PROVINCE_POST);
        }

        @Test
        @DisplayName("Failure - Phone number exists")
        void createProvinceAdmin_PhoneExists() {
            CreateProvinceAdminRequest request = new CreateProvinceAdminRequest();
            request.setPhoneNumber("0123456789");
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(accountRepository.existsByUsername("0123456789")).thenReturn(true);

            assertThatThrownBy(() -> provinceAdminService.createProvinceAdmin(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Phone number already registered");
        }

        @Test
        @DisplayName("Failure - Email exists")
        void createProvinceAdmin_EmailExists() {
            CreateProvinceAdminRequest request = new CreateProvinceAdminRequest();
            request.setPhoneNumber("0123456789");
            request.setEmail("exist@test.com");
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(accountRepository.existsByUsername("0123456789")).thenReturn(false);
            when(accountRepository.existsByEmail("exist@test.com")).thenReturn(true);

            assertThatThrownBy(() -> provinceAdminService.createProvinceAdmin(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already registered");
        }
    }

    @Nested
    @DisplayName("createWardManager()")
    class CreateWardManagerTests {
        @Test
        @DisplayName("Success - PO_PROVINCE_ADMIN creates PO_WARD_MANAGER in same province")
        void createWardManager_Success() {
            CreateWardManagerRequest request = new CreateWardManagerRequest();
            request.setOfficeId(UUID.randomUUID());
            request.setFullName("Ward Mgr");
            request.setPhoneNumber("0987654321");
            request.setEmail("wm@test.com");

            Office targetOffice = new Office();
            targetOffice.setId(request.getOfficeId());
            targetOffice.setOfficeType(OfficeType.WARD_POST);
            targetOffice.setProvince(province);

            when(officeRepository.findById(request.getOfficeId())).thenReturn(Optional.of(targetOffice));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(accountRepository.existsByUsername(any())).thenReturn(false);
            when(accountRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(employeeRepository.save(any())).thenAnswer(i -> {
                Employee e = i.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = provinceAdminService.createWardManager(request, poAdminAccount);

            assertThat(result.getRole()).isEqualTo("PO_WARD_MANAGER");
        }

        @Test
        @DisplayName("Success - WH_PROVINCE_ADMIN creates WH_WARD_MANAGER")
        void createWardManager_WHSuccess() {
            CreateWardManagerRequest request = new CreateWardManagerRequest();
            request.setOfficeId(UUID.randomUUID());
            request.setFullName("WH Ward Mgr");
            request.setPhoneNumber("0987999888");
            request.setEmail("whwm@test.com");

            Office targetOffice = new Office();
            targetOffice.setId(request.getOfficeId());
            targetOffice.setOfficeType(OfficeType.WARD_WAREHOUSE);
            targetOffice.setProvince(province);

            when(officeRepository.findById(request.getOfficeId())).thenReturn(Optional.of(targetOffice));
            when(employeeRepository.findById(whAdminAccount.getId())).thenReturn(Optional.of(whAdminEmployee));
            when(accountRepository.existsByUsername(any())).thenReturn(false);
            when(accountRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(employeeRepository.save(any())).thenAnswer(i -> {
                Employee e = i.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = provinceAdminService.createWardManager(request, whAdminAccount);

            assertThat(result.getRole()).isEqualTo("WH_WARD_MANAGER");
        }

        @Test
        @DisplayName("Failure - Different province")
        void createWardManager_DifferentProvince() {
            CreateWardManagerRequest request = new CreateWardManagerRequest();
            request.setOfficeId(UUID.randomUUID());

            Province otherProvince = new Province();
            otherProvince.setCode("02");

            Office targetOffice = new Office();
            targetOffice.setProvince(otherProvince);
            targetOffice.setOfficeType(OfficeType.WARD_POST);

            when(officeRepository.findById(request.getOfficeId())).thenReturn(Optional.of(targetOffice));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));

            assertThatThrownBy(() -> provinceAdminService.createWardManager(request, poAdminAccount))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("only manage offices within your province");
        }

        @Test
        @DisplayName("Failure - Invalid office type for Ward Manager")
        void createWardManager_InvalidOfficeType() {
            CreateWardManagerRequest request = new CreateWardManagerRequest();
            request.setOfficeId(UUID.randomUUID());
            
            Office targetOffice = new Office();
            targetOffice.setId(request.getOfficeId());
            targetOffice.setOfficeType(OfficeType.WARD_WAREHOUSE); // Mismatch for PO admin
            targetOffice.setProvince(province);

            when(officeRepository.findById(request.getOfficeId())).thenReturn(Optional.of(targetOffice));

            assertThatThrownBy(() -> provinceAdminService.createWardManager(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be assigned to office of type");
        }
    }

    @Nested
    @DisplayName("createWardOfficePair()")
    class CreateWardOfficePairTests {
        @Test
        @DisplayName("Success - Creates pair in admin's province")
        void createWardOfficePair_Success() {
            CreateWardOfficeRequest request = new CreateWardOfficeRequest();
            request.setWarehouseName("New WH");
            request.setWarehouseEmail("wh@test.com");
            request.setPostOfficeName("New PO");
            request.setPostOfficeEmail("po@test.com");

            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(province.getCode(), OfficeType.PROVINCE_WAREHOUSE))
                    .thenReturn(List.of(whProvinceOffice));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(province.getCode(), OfficeType.PROVINCE_POST))
                    .thenReturn(List.of(poProvinceOffice));
            when(officeRepository.existsByOfficeEmail(any())).thenReturn(false);
            when(officeRepository.save(any())).thenAnswer(i -> {
                Office o = i.getArgument(0);
                o.setId(UUID.randomUUID());
                return o;
            });
            when(officePairRepository.save(any())).thenAnswer(i -> {
                OfficePair op = i.getArgument(0);
                op.setId(UUID.randomUUID());
                return op;
            });

            WardOfficePairResponse result = provinceAdminService.createWardOfficePair(request, poAdminAccount);

            assertThat(result.getWarehouse().getOfficeName()).isEqualTo("New WH");
            assertThat(result.getPostOffice().getOfficeName()).isEqualTo("New PO");
            verify(officePairRepository).save(any());
        }

        @Test
        @DisplayName("Failure - Provincial parents not found")
        void createWardOfficePair_NoParentFound() {
            CreateWardOfficeRequest request = new CreateWardOfficeRequest();
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(province.getCode(), OfficeType.PROVINCE_WAREHOUSE))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> provinceAdminService.createWardOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No PROVINCE_WAREHOUSE found");
        }

        @Test
        @DisplayName("Failure - Email already exists")
        void createWardOfficePair_EmailExists() {
            CreateWardOfficeRequest request = new CreateWardOfficeRequest();
            request.setWarehouseEmail("wh@test.com");
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(any(), eq(OfficeType.PROVINCE_WAREHOUSE)))
                    .thenReturn(List.of(whProvinceOffice));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(any(), eq(OfficeType.PROVINCE_POST)))
                    .thenReturn(List.of(poProvinceOffice));
            when(officeRepository.existsByOfficeEmail("wh@test.com")).thenReturn(true);

            assertThatThrownBy(() -> provinceAdminService.createWardOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email already exists");
        }
    }

    @Nested
    @DisplayName("assignWardsToOfficePair()")
    class AssignWardsToOfficePairTests {
        @Test
        @DisplayName("Success - Assigns and updates wards")
        void assignWards_Success() {
            AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            request.setWardCodes(List.of("W1"));

            OfficePair pair = new OfficePair();
            pair.setId(request.getOfficePairId());
            pair.setWhOffice(whProvinceOffice); // Using provincial for mock simplicity but usually ward-level
            whProvinceOffice.setOfficeType(OfficeType.WARD_WAREHOUSE);
            pair.setPoOffice(poProvinceOffice);
            poProvinceOffice.setOfficeType(OfficeType.WARD_POST);

            Ward ward = new Ward();
            ward.setCode("W1");
            ward.setProvince(province);

            when(officePairRepository.findById(request.getOfficePairId())).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(wardRepository.findById("W1")).thenReturn(Optional.of(ward));
            when(wardOfficeAssignmentRepository.existsByWardCode("W1")).thenReturn(false);
            when(wardOfficeAssignmentRepository.findByOfficePairId(pair.getId())).thenReturn(Collections.emptyList());

            WardOfficePairResponse result = provinceAdminService.assignWardsToOfficePair(request, poAdminAccount);

            assertThat(result.getAssignedWards()).hasSize(1);
            verify(wardOfficeAssignmentRepository).save(any());
        }

        @Test
        @DisplayName("Failure - Not a WARD_WAREHOUSE")
        void assignWards_InvalidOfficeType() {
            AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            OfficePair pair = new OfficePair();
            pair.setWhOffice(poProvinceOffice); // PROVINCE_POST instead of WARD_WAREHOUSE
            when(officePairRepository.findById(any())).thenReturn(Optional.of(pair));

            assertThatThrownBy(() -> provinceAdminService.assignWardsToOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Warehouse office is not a WARD_WAREHOUSE");
        }

        @Test
        @DisplayName("Failure - Ward assigned to another pair")
        void assignWards_AlreadyAssigned() {
             AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            request.setWardCodes(List.of("W1"));

            OfficePair pair = new OfficePair();
            pair.setId(request.getOfficePairId());
            pair.setWhOffice(whProvinceOffice);
            whProvinceOffice.setOfficeType(OfficeType.WARD_WAREHOUSE);
            pair.setPoOffice(poProvinceOffice);
            poProvinceOffice.setOfficeType(OfficeType.WARD_POST);

            Ward ward = new Ward();
            ward.setCode("W1");
            ward.setProvince(province);

            when(officePairRepository.findById(request.getOfficePairId())).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(wardRepository.findById("W1")).thenReturn(Optional.of(ward));
            when(wardOfficeAssignmentRepository.existsByWardCode("W1")).thenReturn(true);
            when(wardOfficeAssignmentRepository.existsByWardCodeAndOfficePairId("W1", pair.getId())).thenReturn(false);

            assertThatThrownBy(() -> provinceAdminService.assignWardsToOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("is already assigned to another office pair");
        }

        @Test
        @DisplayName("Success - Updates assignments (add/remove)")
        void assignWards_UpdateAssignments() {
            AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            request.setWardCodes(List.of("W2")); // W1 removed, W2 added

            OfficePair pair = new OfficePair();
            pair.setId(request.getOfficePairId());
            pair.setWhOffice(whProvinceOffice);
            whProvinceOffice.setOfficeType(OfficeType.WARD_WAREHOUSE);
            pair.setPoOffice(poProvinceOffice);
            poProvinceOffice.setOfficeType(OfficeType.WARD_POST);

            Ward w1 = new Ward(); w1.setCode("W1"); w1.setProvince(province);
            Ward w2 = new Ward(); w2.setCode("W2"); w2.setProvince(province);

            WardOfficeAssignment existingAssignment = new WardOfficeAssignment();
            existingAssignment.setWard(w1);
            existingAssignment.setOfficePair(pair);

            when(officePairRepository.findById(request.getOfficePairId())).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(wardRepository.findById("W2")).thenReturn(Optional.of(w2));
            when(wardOfficeAssignmentRepository.existsByWardCode("W2")).thenReturn(false);
            when(wardOfficeAssignmentRepository.findByOfficePairId(pair.getId())).thenReturn(List.of(existingAssignment));

            WardOfficePairResponse result = provinceAdminService.assignWardsToOfficePair(request, poAdminAccount);

            assertThat(result.getAssignedWards()).hasSize(1);
            assertThat(result.getAssignedWards().get(0).getWardCode()).isEqualTo("W2");
            verify(wardOfficeAssignmentRepository).save(any());
            verify(wardOfficeAssignmentRepository).delete(existingAssignment);
        }

        @Test
        @DisplayName("Failure - Ward different province")
        void assignWards_DifferentProvince() {
            AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            request.setWardCodes(List.of("W_DIFF"));

            OfficePair pair = new OfficePair();
            pair.setId(request.getOfficePairId());
            pair.setWhOffice(whProvinceOffice);
            whProvinceOffice.setOfficeType(OfficeType.WARD_WAREHOUSE);
            pair.setPoOffice(poProvinceOffice);
            poProvinceOffice.setOfficeType(OfficeType.WARD_POST);

            Ward wDiff = new Ward(); wDiff.setCode("W_DIFF");
            Province other = new Province(); other.setCode("99");
            wDiff.setProvince(other);

            when(officePairRepository.findById(request.getOfficePairId())).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(wardRepository.findById("W_DIFF")).thenReturn(Optional.of(wDiff));

            assertThatThrownBy(() -> provinceAdminService.assignWardsToOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("does not belong to the office's province");
        }
    }

    @Nested
    @DisplayName("getWardOfficePairById()")
    class GetWardOfficePairByIdTests {
        @Test
        @DisplayName("Success - Returns pair by ID")
        void getWardOfficePairById_Success() {
            UUID id = UUID.randomUUID();
            OfficePair pair = new OfficePair();
            pair.setId(id);
            pair.setWhOffice(whProvinceOffice);
            whProvinceOffice.setOfficeType(OfficeType.WARD_WAREHOUSE);
            pair.setPoOffice(poProvinceOffice);
            poProvinceOffice.setOfficeType(OfficeType.WARD_POST);

            when(officePairRepository.findById(id)).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(wardOfficeAssignmentRepository.findByOfficePairId(id)).thenReturn(Collections.emptyList());

            WardOfficePairResponse result = provinceAdminService.getWardOfficePairById(id, poAdminAccount);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("getAvailableWardsForAssignment()")
    class GetAvailableWardsForAssignmentTests {
        @Test
        @DisplayName("Success - Returns wards in province")
        void getAvailableWards_Success() {
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            Ward ward = new Ward();
            ward.setCode("W1");
            ward.setName("Ward 1");
            when(wardRepository.findByProvince_Code(province.getCode())).thenReturn(List.of(ward));
            when(wardOfficeAssignmentRepository.findAllByProvinceCode(province.getCode())).thenReturn(Collections.emptyList());

            var result = provinceAdminService.getAvailableWardsForAssignment(poAdminAccount, province.getCode());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).wardCode()).isEqualTo("W1");
        }

        @Test
        @DisplayName("Failure - Province code mismatch")
        void getAvailableWards_Mismatch() {
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            
            assertThatThrownBy(() -> provinceAdminService.getAvailableWardsForAssignment(poAdminAccount, "99"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("You can only view wards in your province");
        }

        @Test
        @DisplayName("Success - Null province code maps to user province")
        void getAvailableWards_NullProvinceCode() {
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            Ward ward = new Ward();
            ward.setCode("W1");
            ward.setName("Ward 1");
            when(wardRepository.findByProvince_Code(province.getCode())).thenReturn(List.of(ward));
            when(wardOfficeAssignmentRepository.findAllByProvinceCode(province.getCode())).thenReturn(Collections.emptyList());

            var result = provinceAdminService.getAvailableWardsForAssignment(poAdminAccount, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).wardCode()).isEqualTo("W1");
        }
    }

    @Nested
    @DisplayName("createStaff()")
    class CreateStaffTests {
        @Test
        @DisplayName("Success - PO_PROVINCE_ADMIN creates PO_STAFF")
        void createStaff_POSuccess() {
            CreateStaffRequest request = new CreateStaffRequest();
            request.setOfficeId(poProvinceOffice.getId());
            // Role is determined by service
            request.setFullName("Staff");
            request.setPhoneNumber("1122334455");
            request.setEmail("staff@test.com");

            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(officeRepository.findById(poProvinceOffice.getId())).thenReturn(Optional.of(poProvinceOffice));
            when(accountRepository.existsByUsername(any())).thenReturn(false);
            when(accountRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(accountRepository.save(any())).thenAnswer(i -> {
                Account a = i.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });
            when(employeeRepository.save(any())).thenAnswer(i -> {
                Employee e = i.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = provinceAdminService.createStaff(request, poAdminAccount);

            assertThat(result.getRole()).isEqualTo("PO_STAFF");
        }

        @Test
        @DisplayName("Failure - WH_PROVINCE_ADMIN creates PO_STAFF")
        void createStaff_InvalidCrossRole() {
            CreateStaffRequest request = new CreateStaffRequest();
            request.setOfficeId(poProvinceOffice.getId());
            // Role is determined by service

            when(officeRepository.findById(poProvinceOffice.getId())).thenReturn(Optional.of(poProvinceOffice));

            assertThatThrownBy(() -> provinceAdminService.createStaff(request, whAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Success - WH_PROVINCE_ADMIN creates WH_STAFF")
        void createStaff_WHSuccess() {
            CreateStaffRequest request = new CreateStaffRequest();
            request.setOfficeId(whProvinceOffice.getId());
            request.setFullName("WH Staff");
            request.setPhoneNumber("1122334466");
            request.setEmail("whstaff@test.com");

            when(employeeRepository.findById(whAdminAccount.getId())).thenReturn(Optional.of(whAdminEmployee));
            when(officeRepository.findById(whProvinceOffice.getId())).thenReturn(Optional.of(whProvinceOffice));
            when(accountRepository.existsByUsername(any())).thenReturn(false);
            when(accountRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(accountRepository.save(any())).thenAnswer(i -> {
                Account a = i.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });
            when(employeeRepository.save(any())).thenAnswer(i -> {
                Employee e = i.getArgument(0);
                e.setId(UUID.randomUUID());
                return e;
            });

            EmployeeResponse result = provinceAdminService.createStaff(request, whAdminAccount);

            assertThat(result.getRole()).isEqualTo("WH_STAFF");
        }

        @Test
        @DisplayName("Failure - Invalid office type for Staff")
        void createStaff_InvalidOfficeType() {
            CreateStaffRequest request = new CreateStaffRequest();
            request.setOfficeId(UUID.randomUUID());
            
            Office targetOffice = new Office();
            targetOffice.setId(request.getOfficeId());
            targetOffice.setOfficeType(OfficeType.WARD_WAREHOUSE); // Mismatch for PO admin (PO_STAFF cannot go to WARD_WAREHOUSE)

            when(officeRepository.findById(request.getOfficeId())).thenReturn(Optional.of(targetOffice));

            assertThatThrownBy(() -> provinceAdminService.createStaff(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be assigned to office of type");
        }
    }

    @Nested
    @DisplayName("Coverage Gap Tests")
    class CoverageGapTests {

        @Test
        @DisplayName("createWardOfficePair - User office has no province")
        void createWardOfficePair_NoProvince() {
            CreateWardOfficeRequest request = new CreateWardOfficeRequest();
            poProvinceOffice.setProvince(null);
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));

            assertThatThrownBy(() -> provinceAdminService.createWardOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not associated with a province");
            
            // Restore
            poProvinceOffice.setProvince(province);
        }

        @Test
        @DisplayName("createWardOfficePair - No PROVINCE_WAREHOUSE found")
        void createWardOfficePair_NoProvinceWarehouse() {
            CreateWardOfficeRequest request = new CreateWardOfficeRequest();
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(any(), eq(OfficeType.PROVINCE_WAREHOUSE)))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> provinceAdminService.createWardOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No PROVINCE_WAREHOUSE found");
        }

        @Test
        @DisplayName("createWardOfficePair - No PROVINCE_POST found")
        void createWardOfficePair_NoProvincePost() {
            CreateWardOfficeRequest request = new CreateWardOfficeRequest();
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(any(), eq(OfficeType.PROVINCE_WAREHOUSE)))
                    .thenReturn(List.of(whProvinceOffice));
            when(officeRepository.findAllByProvinceCodeAndOfficeType(any(), eq(OfficeType.PROVINCE_POST)))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> provinceAdminService.createWardOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No PROVINCE_POST found");
        }

        @Test
        @DisplayName("getWardOfficePairs - User office has no province")
        void getWardOfficePairs_NoProvince() {
            poProvinceOffice.setProvince(null);
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));

            assertThatThrownBy(() -> provinceAdminService.getWardOfficePairs(poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not associated with a province");

            // Restore
            poProvinceOffice.setProvince(province);
        }

        @Test
        @DisplayName("getAvailableWardsForAssignment - User office has no province")
        void getAvailableWards_NoProvince() {
            poProvinceOffice.setProvince(null);
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));

            assertThatThrownBy(() -> provinceAdminService.getAvailableWardsForAssignment(poAdminAccount, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not associated with a province");

            // Restore
            poProvinceOffice.setProvince(province);
        }

        @Test
        @DisplayName("assignWardsToOfficePair - Office Pair not found")
        void assignWards_OfficePairNotFound() {
            AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            when(officePairRepository.findById(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> provinceAdminService.assignWardsToOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Office pair not found");
        }

        @Test
        @DisplayName("assignWardsToOfficePair - Ward not found")
        void assignWards_WardNotFound() {
            AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            request.setWardCodes(List.of("INVALID"));
            
            Office whOption = new Office(); whOption.setOfficeType(OfficeType.WARD_WAREHOUSE); whOption.setProvince(province);
            Office poOption = new Office(); poOption.setOfficeType(OfficeType.WARD_POST); poOption.setProvince(province);
            OfficePair pair = new OfficePair();
            pair.setId(UUID.randomUUID());
            pair.setWhOffice(whOption);
            pair.setPoOffice(poOption);

            when(officePairRepository.findById(any())).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(wardRepository.findById("INVALID")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> provinceAdminService.assignWardsToOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ward not found");
        }
        
        @Test
        @DisplayName("assignWardsToOfficePair - Ward wrong province")
        void assignWards_WardWrongProvince() {
            AssignWardsRequest request = new AssignWardsRequest();
            request.setOfficePairId(UUID.randomUUID());
            request.setWardCodes(List.of("W_OTHER"));
            
            Office whOption = new Office(); whOption.setOfficeType(OfficeType.WARD_WAREHOUSE); whOption.setProvince(province);
            Office poOption = new Office(); poOption.setOfficeType(OfficeType.WARD_POST); poOption.setProvince(province);
            OfficePair pair = new OfficePair();
            pair.setId(UUID.randomUUID());
            pair.setWhOffice(whOption);
            pair.setPoOffice(poOption);

            when(officePairRepository.findById(any())).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            
            Province other = new Province(); other.setCode("OTHER");
            Ward wrongWard = new Ward(); wrongWard.setCode("W_OTHER"); wrongWard.setProvince(other);
            when(wardRepository.findById("W_OTHER")).thenReturn(Optional.of(wrongWard));

            assertThatThrownBy(() -> provinceAdminService.assignWardsToOfficePair(request, poAdminAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("does not belong to the office's province");
        }
        @Test
        @DisplayName("getWardOfficePairById - Null parent and region")
        void getWardOfficePairById_NullAttributes() {
            UUID pairId = UUID.randomUUID();
            Office wh = new Office();
            wh.setId(UUID.randomUUID());
            wh.setOfficeName("WH");
            wh.setOfficeType(OfficeType.WARD_WAREHOUSE);
            wh.setProvince(province); // Must have province for validation
            wh.setRegion(null);       // Null region
            wh.setParent(null);       // Null parent
            
            Office po = new Office();
            po.setId(UUID.randomUUID());
            po.setOfficeName("PO");
            po.setOfficeType(OfficeType.WARD_POST);
            po.setProvince(province);
            po.setRegion(null);
            po.setParent(null);
            
            OfficePair pair = new OfficePair();
            pair.setId(pairId);
            pair.setWhOffice(wh);
            pair.setPoOffice(po);
            pair.setCreatedAt(java.time.LocalDateTime.now());

            when(officePairRepository.findById(pairId)).thenReturn(Optional.of(pair));
            when(employeeRepository.findById(poAdminAccount.getId())).thenReturn(Optional.of(poAdminEmployee));
            when(wardOfficeAssignmentRepository.findByOfficePairId(pairId)).thenReturn(Collections.emptyList());

            WardOfficePairResponse response = provinceAdminService.getWardOfficePairById(pairId, poAdminAccount);
            
            assertThat(response.getRegionName()).isNull();
            assertThat(response.getWarehouse().getParentOfficeId()).isNull();
            assertThat(response.getPostOffice().getParentOfficeId()).isNull();
        }
    }
}
