package org.f3.postalmanagement.service;

import org.f3.postalmanagement.dto.request.order.CreatePickupRequest;
import org.f3.postalmanagement.dto.response.order.PickupRequestResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.order.PickupRequest;
import org.f3.postalmanagement.enums.PickupStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.enums.TimeSlot;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.repository.PickupRequestRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PickupRequestService Path Coverage Tests")
class PickupRequestServiceTest {

    @Mock
    private PickupRequestRepository pickupRequestRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DeliveryTaskService deliveryTaskService;

    @InjectMocks
    private PickupRequestService pickupRequestService;

    private Account customerAccount;
    private Account staffAccount;
    private Customer customer;
    private Customer otherCustomer;
    private Employee shipper;
    private Order order;
    private PickupRequest pickupRequest;

    @BeforeEach
    void setUp() {
        customerAccount = new Account();
        customerAccount.setId(UUID.randomUUID());
        customerAccount.setUsername("customer@test.com");
        customerAccount.setRole(Role.CUSTOMER);

        staffAccount = new Account();
        staffAccount.setId(UUID.randomUUID());
        staffAccount.setUsername("staff@test.com");
        staffAccount.setRole(Role.PO_STAFF);

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setAccount(customerAccount);
        customer.setFullName("Test Customer");

        otherCustomer = new Customer();
        otherCustomer.setId(UUID.randomUUID());

        shipper = new Employee();
        shipper.setId(UUID.randomUUID());
        shipper.setFullName("Test Shipper");

        order = Order.builder()
                .trackingNumber("VN12345678901234567")
                .customer(customer)
                .build();
        order.setId(UUID.randomUUID());

        pickupRequest = PickupRequest.builder()
                .order(order)
                .customer(customer)
                .pickupAddress("123 Test St")
                .pickupWardCode("001")
                .pickupContactName("Contact")
                .pickupContactPhone("0987654321")
                .status(PickupStatus.PENDING)
                .build();
        pickupRequest.setId(UUID.randomUUID());
    }

    // ==================== createPickupRequest Tests ====================
    @Nested
    @DisplayName("createPickupRequest()")
    class CreatePickupRequestTests {

        private CreatePickupRequest createValidRequest() {
            CreatePickupRequest request = new CreatePickupRequest();
            request.setOrderId(order.getId());
            request.setPickupAddress("123 Test St");
            request.setPickupWardCode("001");
            request.setPickupContactName("Contact");
            request.setPickupContactPhone("0987654321");
            request.setPreferredDate(LocalDate.now().plusDays(1));
            request.setPreferredTimeSlot(TimeSlot.MORNING);
            return request;
        }

        @Test
        @DisplayName("Path 1: Success - Customer creates pickup request")
        void createPickupRequest_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(pickupRequestRepository.save(any(PickupRequest.class))).thenAnswer(inv -> {
                    PickupRequest pr = inv.getArgument(0);
                    pr.setId(UUID.randomUUID());
                    return pr;
                });

                CreatePickupRequest request = createValidRequest();

                PickupRequestResponse result = pickupRequestService.createPickupRequest(request);

                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo("PENDING");
                verify(pickupRequestRepository).save(any(PickupRequest.class));
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Non-customer role")
        void createPickupRequest_AsStaff_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);

                CreatePickupRequest request = createValidRequest();

                assertThatThrownBy(() -> pickupRequestService.createPickupRequest(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Only customers can create pickup requests");
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Customer not found")
        void createPickupRequest_CustomerNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                CreatePickupRequest request = createValidRequest();

                assertThatThrownBy(() -> pickupRequestService.createPickupRequest(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Customer not found");
            }
        }

        @Test
        @DisplayName("Path 4: Failure - Order not found")
        void createPickupRequest_OrderNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

                CreatePickupRequest request = createValidRequest();

                assertThatThrownBy(() -> pickupRequestService.createPickupRequest(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Order not found");
            }
        }

        @Test
        @DisplayName("Path 5: Failure - Order doesn't belong to customer")
        void createPickupRequest_OrderNotOwned_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                
                Order otherOrder = Order.builder()
                        .customer(otherCustomer)
                        .build();
                otherOrder.setId(UUID.randomUUID());
                when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(otherOrder));

                CreatePickupRequest request = createValidRequest();

                assertThatThrownBy(() -> pickupRequestService.createPickupRequest(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("does not belong to this customer");
            }
        }
    }

    // ==================== getMyRequests Tests ====================
    @Nested
    @DisplayName("getMyRequests()")
    class GetMyRequestsTests {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("Path 1: Success - Customer gets their pickup requests")
        void getMyRequests_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(pickupRequestRepository.findByCustomerId(customer.getId(), pageable))
                        .thenReturn(new PageImpl<>(List.of(pickupRequest)));

                Page<PickupRequestResponse> result = pickupRequestService.getMyRequests(pageable);

                assertThat(result.getContent()).hasSize(1);
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Customer not found")
        void getMyRequests_CustomerNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> pickupRequestService.getMyRequests(pageable))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Customer not found");
            }
        }
    }

    // ==================== getPendingRequestsByWard Tests ====================
    @Nested
    @DisplayName("getPendingRequestsByWard()")
    class GetPendingRequestsByWardTests {

        @Test
        @DisplayName("Path 1: Success - Returns pending requests for ward")
        void getPendingRequestsByWard_Success() {
            when(pickupRequestRepository.findByPickupWardCodeAndStatus("001", PickupStatus.PENDING))
                    .thenReturn(List.of(pickupRequest));

            List<PickupRequestResponse> result = pickupRequestService.getPendingRequestsByWard("001");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Path 2: Returns empty list when no pending requests")
        void getPendingRequestsByWard_EmptyResult() {
            when(pickupRequestRepository.findByPickupWardCodeAndStatus("002", PickupStatus.PENDING))
                    .thenReturn(List.of());

            List<PickupRequestResponse> result = pickupRequestService.getPendingRequestsByWard("002");

            assertThat(result).isEmpty();
        }
    }

    // ==================== assignShipper Tests ====================
    @Nested
    @DisplayName("assignShipper()")
    class AssignShipperTests {

        @Test
        @DisplayName("Path 1: Success - Assign shipper to request")
        void assignShipper_Success() {
            when(pickupRequestRepository.findById(pickupRequest.getId()))
                    .thenReturn(Optional.of(pickupRequest));
            when(employeeRepository.findById(shipper.getId()))
                    .thenReturn(Optional.of(shipper));
            when(pickupRequestRepository.save(any(PickupRequest.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            PickupRequestResponse result = pickupRequestService.assignShipper(
                    pickupRequest.getId(), shipper.getId()
            );

            assertThat(result.getStatus()).isEqualTo("ASSIGNED");
            assertThat(result.getAssignedShipperId()).isEqualTo(shipper.getId());
            verify(deliveryTaskService).createPickupTask(any(PickupRequest.class));
        }

        @Test
        @DisplayName("Path 2: Failure - Request not found")
        void assignShipper_RequestNotFound_ThrowsException() {
            when(pickupRequestRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pickupRequestService.assignShipper(UUID.randomUUID(), shipper.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Request not found");
        }

        @Test
        @DisplayName("Path 3: Failure - Shipper not found")
        void assignShipper_ShipperNotFound_ThrowsException() {
            when(pickupRequestRepository.findById(pickupRequest.getId()))
                    .thenReturn(Optional.of(pickupRequest));
            when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pickupRequestService.assignShipper(pickupRequest.getId(), UUID.randomUUID()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Shipper not found");
        }
    }
}
