package org.f3.postalmanagement.service;

import org.f3.postalmanagement.dto.request.order.CreateOrderRequest;
import org.f3.postalmanagement.dto.response.order.OrderResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Path Coverage Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TrackingNumberGenerator trackingNumberGenerator;

    @InjectMocks
    private OrderService orderService;

    private Account customerAccount;
    private Account staffAccount;
    private Account shipperAccount;
    private Customer customer;
    private Employee employee;
    private Office office;
    private Order order;

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

        shipperAccount = new Account();
        shipperAccount.setId(UUID.randomUUID());
        shipperAccount.setUsername("shipper@test.com");
        shipperAccount.setRole(Role.SHIPPER);

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setAccount(customerAccount);
        customer.setFullName("Test Customer");

        office = new Office();
        office.setId(UUID.randomUUID());
        office.setOfficeName("Test Office");

        employee = new Employee();
        employee.setId(staffAccount.getId());
        employee.setAccount(staffAccount);
        employee.setOffice(office);

        order = Order.builder()
                .trackingNumber("VN12345678901234567")
                .customer(customer)
                .status(OrderStatus.PENDING)
                .senderName("Sender")
                .senderPhone("0123456789")
                .senderAddress("123 Sender St")
                .senderWardCode("001")
                .receiverName("Receiver")
                .receiverPhone("0987654321")
                .receiverAddress("456 Receiver St")
                .receiverWardCode("002")
                .weightKg(BigDecimal.valueOf(1.5))
                .shippingFee(BigDecimal.valueOf(25000))
                .codAmount(BigDecimal.valueOf(100000))
                .build();
        order.setId(UUID.randomUUID());
    }

    // ==================== createOrder Tests ====================
    @Nested
    @DisplayName("createOrder()")
    class CreateOrderTests {

        private CreateOrderRequest createValidRequest() {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setSenderName("Sender");
            request.setSenderPhone("0123456789");
            request.setSenderAddress("123 Sender St");
            request.setSenderWardCode("001");
            request.setReceiverName("Receiver");
            request.setReceiverPhone("0987654321");
            request.setReceiverAddress("456 Receiver St");
            request.setReceiverWardCode("002");
            request.setWeightKg(BigDecimal.valueOf(1.5));
            request.setShippingFee(BigDecimal.valueOf(25000));
            request.setCodAmount(BigDecimal.valueOf(100000));
            return request;
        }

        @Test
        @DisplayName("Path 1: Success - Customer creates order successfully")
        void createOrder_AsCustomer_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                // Arrange
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(trackingNumberGenerator.generate()).thenReturn("VN12345678901234567");
                when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                    Order o = inv.getArgument(0);
                    o.setId(UUID.randomUUID());
                    return o;
                });

                CreateOrderRequest request = createValidRequest();

                // Act
                OrderResponse response = orderService.createOrder(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getTrackingNumber()).isEqualTo("VN12345678901234567");
                assertThat(response.getStatus()).isEqualTo("PENDING");
                verify(orderRepository).save(any(Order.class));
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Non-customer role attempts to create order")
        void createOrder_AsStaff_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);

                CreateOrderRequest request = createValidRequest();

                assertThatThrownBy(() -> orderService.createOrder(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Only customers can create orders");
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Customer profile not found")
        void createOrder_CustomerProfileNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                CreateOrderRequest request = createValidRequest();

                assertThatThrownBy(() -> orderService.createOrder(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Customer profile not found");
            }
        }
    }

    // ==================== getMyOrders Tests ====================
    @Nested
    @DisplayName("getMyOrders()")
    class GetMyOrdersTests {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("Path 1: Customer gets their orders")
        void getMyOrders_AsCustomer_ReturnsOrders() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.findByCustomerId(customer.getId(), pageable))
                        .thenReturn(new PageImpl<>(List.of(order)));

                Page<OrderResponse> result = orderService.getMyOrders(pageable);

                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getTrackingNumber()).isEqualTo("VN12345678901234567");
            }
        }

        @Test
        @DisplayName("Path 2: Customer profile not found")
        void getMyOrders_CustomerProfileNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.getMyOrders(pageable))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Customer profile not found");
            }
        }

        @Test
        @DisplayName("Path 3: PO_STAFF gets orders from their office")
        void getMyOrders_AsStaff_ReturnsOfficeOrders() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);
                when(employeeRepository.findById(staffAccount.getId())).thenReturn(Optional.of(employee));
                when(orderRepository.findByOriginOfficeId(office.getId(), pageable))
                        .thenReturn(new PageImpl<>(List.of(order)));

                Page<OrderResponse> result = orderService.getMyOrders(pageable);

                assertThat(result.getContent()).hasSize(1);
            }
        }

        @Test
        @DisplayName("Path 4: PO_STAFF employee not found")
        void getMyOrders_StaffEmployeeNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);
                when(employeeRepository.findById(staffAccount.getId())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.getMyOrders(pageable))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Employee not found");
            }
        }

        @Test
        @DisplayName("Path 5: Other roles return empty page")
        void getMyOrders_AsShipper_ReturnsEmptyPage() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);

                Page<OrderResponse> result = orderService.getMyOrders(pageable);

                assertThat(result.getContent()).isEmpty();
            }
        }
    }

    // ==================== getOrderByTrackingNumber Tests ====================
    @Nested
    @DisplayName("getOrderByTrackingNumber()")
    class GetOrderByTrackingNumberTests {

        @Test
        @DisplayName("Path 1: Success - Order found")
        void getOrderByTrackingNumber_OrderExists_ReturnsOrder() {
            when(orderRepository.findByTrackingNumber("VN12345678901234567"))
                    .thenReturn(Optional.of(order));

            OrderResponse response = orderService.getOrderByTrackingNumber("VN12345678901234567");

            assertThat(response).isNotNull();
            assertThat(response.getTrackingNumber()).isEqualTo("VN12345678901234567");
        }

        @Test
        @DisplayName("Path 2: Failure - Order not found")
        void getOrderByTrackingNumber_OrderNotExists_ThrowsException() {
            when(orderRepository.findByTrackingNumber("INVALID"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrderByTrackingNumber("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }

    // ==================== cancelOrder Tests ====================
    @Nested
    @DisplayName("cancelOrder()")
    class CancelOrderTests {

        @Test
        @DisplayName("Path 1: Success - Customer cancels their PENDING order")
        void cancelOrder_CustomerOwnsPendingOrder_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                order.setStatus(OrderStatus.PENDING);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

                OrderResponse response = orderService.cancelOrder(order.getId());

                assertThat(response.getStatus()).isEqualTo("CANCELLED");
            }
        }

        @Test
        @DisplayName("Path 2: Success - Customer cancels AWAITING_PICKUP order")
        void cancelOrder_CustomerOwnsAwaitingPickupOrder_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                order.setStatus(OrderStatus.AWAITING_PICKUP);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

                OrderResponse response = orderService.cancelOrder(order.getId());

                assertThat(response.getStatus()).isEqualTo("CANCELLED");
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Order not found")
        void cancelOrder_OrderNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.cancelOrder(UUID.randomUUID()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Order not found");
            }
        }

        @Test
        @DisplayName("Path 4: Failure - Customer does not own the order")
        void cancelOrder_CustomerDoesNotOwnOrder_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                
                Customer otherCustomer = new Customer();
                otherCustomer.setId(UUID.randomUUID());
                order.setCustomer(otherCustomer);
                
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));

                assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Access denied");
            }
        }

        @Test
        @DisplayName("Path 5: Failure - Customer profile not found during cancel")
        void cancelOrder_CustomerProfileNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Customer profile not found");
            }
        }

        @Test
        @DisplayName("Path 6: Failure - Order not in cancellable status (IN_TRANSIT)")
        void cancelOrder_OrderInTransit_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                order.setStatus(OrderStatus.IN_TRANSIT);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));

                assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Cannot cancel order in status");
            }
        }

        @Test
        @DisplayName("Path 7: Failure - Order not in cancellable status (DELIVERED)")
        void cancelOrder_OrderDelivered_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                order.setStatus(OrderStatus.DELIVERED);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));

                assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Cannot cancel order in status");
            }
        }

        @Test
        @DisplayName("Path 8: Non-customer role can cancel any order in valid status")
        void cancelOrder_AsStaff_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);
                order.setStatus(OrderStatus.PENDING);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

                OrderResponse response = orderService.cancelOrder(order.getId());

                assertThat(response.getStatus()).isEqualTo("CANCELLED");
            }
        }
    }
}
