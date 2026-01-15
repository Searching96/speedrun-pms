package org.f3.postalmanagement.service;

import org.f3.postalmanagement.dto.request.order.CreateRatingRequest;
import org.f3.postalmanagement.dto.response.order.RatingResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.order.DeliveryTask;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.order.ServiceRating;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.enums.TaskStatus;
import org.f3.postalmanagement.enums.TaskType;
import org.f3.postalmanagement.exception.ResourceNotFoundException;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.DeliveryTaskRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.repository.ServiceRatingRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingService Path Coverage Tests")
class RatingServiceTest {

    @Mock
    private ServiceRatingRepository serviceRatingRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DeliveryTaskRepository deliveryTaskRepository;

    @InjectMocks
    private RatingService ratingService;

    private Account customerAccount;
    private Account staffAccount;
    private Account adminAccount;
    private Customer customer;
    private Customer otherCustomer;
    private Employee shipper;
    private Order order;
    private ServiceRating rating;
    private DeliveryTask deliveryTask;

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

        adminAccount = new Account();
        adminAccount.setId(UUID.randomUUID());
        adminAccount.setUsername("admin@test.com");
        adminAccount.setRole(Role.SYSTEM_ADMIN);

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setAccount(customerAccount);

        otherCustomer = new Customer();
        otherCustomer.setId(UUID.randomUUID());

        shipper = new Employee();
        shipper.setId(UUID.randomUUID());
        shipper.setFullName("Test Shipper");

        order = Order.builder()
                .trackingNumber("VN12345678901234567")
                .customer(customer)
                .status(OrderStatus.DELIVERED)
                .build();
        order.setId(UUID.randomUUID());

        rating = ServiceRating.builder()
                .order(order)
                .customer(customer)
                .shipper(shipper)
                .overallRating(5)
                .deliverySpeedRating(5)
                .shipperAttitudeRating(5)
                .comment("Excellent service!")
                .build();
        rating.setId(UUID.randomUUID());

        deliveryTask = DeliveryTask.builder()
                .order(order)
                .shipper(shipper)
                .taskType(TaskType.DELIVERY)
                .status(TaskStatus.COMPLETED)
                .build();
        deliveryTask.setId(UUID.randomUUID());
    }

    // ==================== createRating Tests ====================
    @Nested
    @DisplayName("createRating()")
    class CreateRatingTests {

        private CreateRatingRequest createValidRequest() {
            CreateRatingRequest request = new CreateRatingRequest();
            request.setOrderId(order.getId());
            request.setOverallRating(5);
            request.setDeliverySpeedRating(5);
            request.setShipperAttitudeRating(5);
            request.setComment("Great service!");
            return request;
        }

        @Test
        @DisplayName("Path 1: Success - Customer creates rating with shipper found")
        void createRating_WithShipper_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(serviceRatingRepository.findByOrderId(order.getId())).thenReturn(Optional.empty());
                when(deliveryTaskRepository.findByOrderIdAndTaskTypeAndStatus(
                        order.getId(), TaskType.DELIVERY, TaskStatus.COMPLETED
                )).thenReturn(Optional.of(deliveryTask));
                when(serviceRatingRepository.save(any(ServiceRating.class))).thenAnswer(inv -> {
                    ServiceRating r = inv.getArgument(0);
                    r.setId(UUID.randomUUID());
                    return r;
                });

                CreateRatingRequest request = createValidRequest();

                RatingResponse result = ratingService.createRating(request);

                assertThat(result).isNotNull();
                assertThat(result.getOverallRating()).isEqualTo(5);
                verify(serviceRatingRepository).save(any(ServiceRating.class));
            }
        }

        @Test
        @DisplayName("Path 2: Success - Customer creates rating without shipper (no delivery task)")
        void createRating_WithoutShipper_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(serviceRatingRepository.findByOrderId(order.getId())).thenReturn(Optional.empty());
                when(deliveryTaskRepository.findByOrderIdAndTaskTypeAndStatus(
                        order.getId(), TaskType.DELIVERY, TaskStatus.COMPLETED
                )).thenReturn(Optional.empty());
                when(serviceRatingRepository.save(any(ServiceRating.class))).thenAnswer(inv -> {
                    ServiceRating r = inv.getArgument(0);
                    r.setId(UUID.randomUUID());
                    return r;
                });

                CreateRatingRequest request = createValidRequest();

                RatingResponse result = ratingService.createRating(request);

                assertThat(result).isNotNull();
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Non-customer role")
        void createRating_AsStaff_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);

                CreateRatingRequest request = createValidRequest();

                assertThatThrownBy(() -> ratingService.createRating(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Only customers can rate");
            }
        }

        @Test
        @DisplayName("Path 4: Failure - Customer not found")
        void createRating_CustomerNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                CreateRatingRequest request = createValidRequest();

                assertThatThrownBy(() -> ratingService.createRating(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Customer not found");
            }
        }

        @Test
        @DisplayName("Path 5: Failure - Order not found")
        void createRating_OrderNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

                CreateRatingRequest request = createValidRequest();

                assertThatThrownBy(() -> ratingService.createRating(request))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Order not found");
            }
        }

        @Test
        @DisplayName("Path 6: Failure - Order doesn't belong to customer")
        void createRating_OrderNotOwned_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                
                Order otherOrder = Order.builder()
                        .customer(otherCustomer)
                        .status(OrderStatus.DELIVERED)
                        .build();
                otherOrder.setId(UUID.randomUUID());
                when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(otherOrder));

                CreateRatingRequest request = createValidRequest();

                assertThatThrownBy(() -> ratingService.createRating(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("does not belong to this customer");
            }
        }

        @Test
        @DisplayName("Path 7: Failure - Order not delivered")
        void createRating_OrderNotDelivered_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                
                order.setStatus(OrderStatus.PENDING);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

                CreateRatingRequest request = createValidRequest();

                assertThatThrownBy(() -> ratingService.createRating(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("not DELIVERED");
            }
        }

        @Test
        @DisplayName("Path 8: Failure - Order already rated")
        void createRating_AlreadyRated_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(serviceRatingRepository.findByOrderId(order.getId())).thenReturn(Optional.of(rating));

                CreateRatingRequest request = createValidRequest();

                assertThatThrownBy(() -> ratingService.createRating(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("already been rated");
            }
        }
    }

    // ==================== getRatingByOrderId Tests ====================
    @Nested
    @DisplayName("getRatingByOrderId()")
    class GetRatingByOrderIdTests {

        @Test
        @DisplayName("Path 1: Success - Admin can view any rating")
        void getRatingByOrderId_AsAdmin_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(adminAccount);
                when(serviceRatingRepository.findByOrderId(order.getId())).thenReturn(Optional.of(rating));

                RatingResponse result = ratingService.getRatingByOrderId(order.getId());

                assertThat(result).isNotNull();
                assertThat(result.getOverallRating()).isEqualTo(5);
            }
        }

        @Test
        @DisplayName("Path 2: Success - Customer can view their own rating")
        void getRatingByOrderId_AsOwner_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(serviceRatingRepository.findByOrderId(order.getId())).thenReturn(Optional.of(rating));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));

                RatingResponse result = ratingService.getRatingByOrderId(order.getId());

                assertThat(result).isNotNull();
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Rating not found")
        void getRatingByOrderId_NotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(adminAccount);
                when(serviceRatingRepository.findByOrderId(any(UUID.class))).thenReturn(Optional.empty());

                assertThatThrownBy(() -> ratingService.getRatingByOrderId(UUID.randomUUID()))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Rating not found");
            }
        }

        @Test
        @DisplayName("Path 4: Failure - Customer viewing other's rating")
        void getRatingByOrderId_NotOwner_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                
                // Create rating for different customer
                Order otherOrder = Order.builder()
                        .customer(otherCustomer)
                        .build();
                otherOrder.setId(UUID.randomUUID());
                ServiceRating otherRating = ServiceRating.builder()
                        .order(otherOrder)
                        .customer(otherCustomer)
                        .build();
                otherRating.setId(UUID.randomUUID());
                
                when(serviceRatingRepository.findByOrderId(any(UUID.class))).thenReturn(Optional.of(otherRating));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));

                assertThatThrownBy(() -> ratingService.getRatingByOrderId(UUID.randomUUID()))
                        .isInstanceOf(AccessDeniedException.class)
                        .hasMessageContaining("do not have permission");
            }
        }

        @Test
        @DisplayName("Path 5: Failure - Non-customer/non-admin role")
        void getRatingByOrderId_AsStaff_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);
                when(serviceRatingRepository.findByOrderId(order.getId())).thenReturn(Optional.of(rating));

                assertThatThrownBy(() -> ratingService.getRatingByOrderId(order.getId()))
                        .isInstanceOf(AccessDeniedException.class)
                        .hasMessageContaining("do not have permission");
            }
        }

        @Test
        @DisplayName("Path 6: Failure - Customer not found when checking ownership")
        void getRatingByOrderId_CustomerNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(customerAccount);
                when(serviceRatingRepository.findByOrderId(order.getId())).thenReturn(Optional.of(rating));
                when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> ratingService.getRatingByOrderId(order.getId()))
                        .isInstanceOf(AccessDeniedException.class)
                        .hasMessageContaining("Customer not found");
            }
        }
    }
}
