package org.f3.postalmanagement.service;

import org.f3.postalmanagement.dto.response.order.DeliveryTaskResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.order.DeliveryTask;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.order.PickupRequest;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.enums.TaskStatus;
import org.f3.postalmanagement.enums.TaskType;
import org.f3.postalmanagement.repository.DeliveryTaskRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryTaskService Path Coverage Tests")
class DeliveryTaskServiceTest {

    @Mock
    private DeliveryTaskRepository deliveryTaskRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PickupRequestRepository pickupRequestRepository;

    @InjectMocks
    private DeliveryTaskService deliveryTaskService;

    private Account shipperAccount;
    private Account staffAccount;
    private Employee shipper;
    private Order order;
    private DeliveryTask task;
    private PickupRequest pickupRequest;

    @BeforeEach
    void setUp() {
        shipperAccount = new Account();
        shipperAccount.setId(UUID.randomUUID());
        shipperAccount.setUsername("shipper@test.com");
        shipperAccount.setRole(Role.SHIPPER);

        staffAccount = new Account();
        staffAccount.setId(UUID.randomUUID());
        staffAccount.setUsername("staff@test.com");
        staffAccount.setRole(Role.PO_STAFF);

        shipper = new Employee();
        shipper.setId(shipperAccount.getId());
        shipper.setAccount(shipperAccount);
        shipper.setFullName("Test Shipper");
        shipper.setPhoneNumber("0123456789");

        order = Order.builder()
                .trackingNumber("VN12345678901234567")
                .status(OrderStatus.PENDING)
                .build();
        order.setId(UUID.randomUUID());

        task = DeliveryTask.builder()
                .order(order)
                .shipper(shipper)
                .taskType(TaskType.PICKUP)
                .status(TaskStatus.ASSIGNED)
                .address("123 Test St")
                .wardCode("001")
                .contactName("Contact")
                .contactPhone("0987654321")
                .assignedAt(LocalDateTime.now())
                .build();
        task.setId(UUID.randomUUID());

        pickupRequest = PickupRequest.builder()
                .order(order)
                .assignedShipper(shipper)
                .pickupAddress("123 Test St")
                .pickupWardCode("001")
                .pickupContactName("Contact")
                .pickupContactPhone("0987654321")
                .build();
        pickupRequest.setId(UUID.randomUUID());
    }

    // ==================== createPickupTask Tests ====================
    @Nested
    @DisplayName("createPickupTask()")
    class CreatePickupTaskTests {

        @Test
        @DisplayName("Path 1: Success - Creates pickup task from request")
        void createPickupTask_Success() {
            when(deliveryTaskRepository.save(any(DeliveryTask.class))).thenAnswer(inv -> {
                DeliveryTask t = inv.getArgument(0);
                t.setId(UUID.randomUUID());
                return t;
            });

            DeliveryTask result = deliveryTaskService.createPickupTask(pickupRequest);

            assertThat(result).isNotNull();
            assertThat(result.getTaskType()).isEqualTo(TaskType.PICKUP);
            assertThat(result.getStatus()).isEqualTo(TaskStatus.ASSIGNED);
            verify(deliveryTaskRepository).save(any(DeliveryTask.class));
        }
    }

    // ==================== getMyTasks Tests ====================
    @Nested
    @DisplayName("getMyTasks()")
    class GetMyTasksTests {

        @Test
        @DisplayName("Path 1: Success - Shipper gets their tasks")
        void getMyTasks_AsShipper_ReturnsTasks() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                when(deliveryTaskRepository.findByShipperId(shipperAccount.getId()))
                        .thenReturn(List.of(task));

                List<DeliveryTaskResponse> result = deliveryTaskService.getMyTasks();

                assertThat(result).hasSize(1);
                assertThat(result.get(0).getTaskType()).isEqualTo(TaskType.PICKUP);
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Non-shipper role attempts to get tasks")
        void getMyTasks_AsStaff_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);

                assertThatThrownBy(() -> deliveryTaskService.getMyTasks())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Only shippers can access tasks");
            }
        }
    }

    // ==================== startTask Tests ====================
    @Nested
    @DisplayName("startTask()")
    class StartTaskTests {

        @Test
        @DisplayName("Path 1: Success - Start an ASSIGNED task")
        void startTask_AssignedTask_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                task.setStatus(TaskStatus.ASSIGNED);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));
                when(deliveryTaskRepository.save(any(DeliveryTask.class))).thenAnswer(inv -> inv.getArgument(0));

                DeliveryTaskResponse result = deliveryTaskService.startTask(task.getId());

                assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
                assertThat(task.getStartedAt()).isNotNull();
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Task not found")
        void startTask_TaskNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                when(deliveryTaskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

                assertThatThrownBy(() -> deliveryTaskService.startTask(UUID.randomUUID()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Task not found");
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Task already started (IN_PROGRESS)")
        void startTask_AlreadyInProgress_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                task.setStatus(TaskStatus.IN_PROGRESS);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));

                assertThatThrownBy(() -> deliveryTaskService.startTask(task.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("already started or completed");
            }
        }

        @Test
        @DisplayName("Path 4: Failure - Task already completed")
        void startTask_AlreadyCompleted_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                task.setStatus(TaskStatus.COMPLETED);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));

                assertThatThrownBy(() -> deliveryTaskService.startTask(task.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("already started or completed");
            }
        }

        @Test
        @DisplayName("Path 5: Failure - Access denied (wrong shipper)")
        void startTask_WrongShipper_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                Account otherShipper = new Account();
                otherShipper.setId(UUID.randomUUID());
                otherShipper.setRole(Role.SHIPPER);

                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(otherShipper);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));

                assertThatThrownBy(() -> deliveryTaskService.startTask(task.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Access denied");
            }
        }
    }

    // ==================== completeTask Tests ====================
    @Nested
    @DisplayName("completeTask()")
    class CompleteTaskTests {

        @Test
        @DisplayName("Path 1: Success - Complete PICKUP task")
        void completeTask_PickupTask_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                task.setStatus(TaskStatus.IN_PROGRESS);
                task.setTaskType(TaskType.PICKUP);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));
                when(deliveryTaskRepository.save(any(DeliveryTask.class))).thenAnswer(inv -> inv.getArgument(0));
                when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

                DeliveryTaskResponse result = deliveryTaskService.completeTask(
                        task.getId(), "Package collected", "http://photo.url"
                );

                assertThat(result.getStatus()).isEqualTo("COMPLETED");
                assertThat(order.getStatus()).isEqualTo(OrderStatus.PICKED_UP);
            }
        }

        @Test
        @DisplayName("Path 2: Success - Complete DELIVERY task")
        void completeTask_DeliveryTask_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                task.setStatus(TaskStatus.IN_PROGRESS);
                task.setTaskType(TaskType.DELIVERY);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));
                when(deliveryTaskRepository.save(any(DeliveryTask.class))).thenAnswer(inv -> inv.getArgument(0));
                when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

                DeliveryTaskResponse result = deliveryTaskService.completeTask(
                        task.getId(), "Delivered to recipient", "http://photo.url"
                );

                assertThat(result.getStatus()).isEqualTo("COMPLETED");
                assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Task not IN_PROGRESS (ASSIGNED)")
        void completeTask_NotInProgress_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                task.setStatus(TaskStatus.ASSIGNED);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));

                assertThatThrownBy(() -> deliveryTaskService.completeTask(task.getId(), "Notes", null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("must be IN_PROGRESS");
            }
        }

        @Test
        @DisplayName("Path 4: Failure - Task not found")
        void completeTask_TaskNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                when(deliveryTaskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

                assertThatThrownBy(() -> deliveryTaskService.completeTask(UUID.randomUUID(), "Notes", null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Task not found");
            }
        }
    }

    // ==================== failTask Tests ====================
    @Nested
    @DisplayName("failTask()")
    class FailTaskTests {

        @Test
        @DisplayName("Path 1: Success - Fail a task")
        void failTask_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                task.setStatus(TaskStatus.IN_PROGRESS);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));
                when(deliveryTaskRepository.save(any(DeliveryTask.class))).thenAnswer(inv -> inv.getArgument(0));

                DeliveryTaskResponse result = deliveryTaskService.failTask(task.getId(), "Recipient not home");

                assertThat(result.getStatus()).isEqualTo("FAILED");
                assertThat(task.getNotes()).isEqualTo("Recipient not home");
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Task not found")
        void failTask_TaskNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(shipperAccount);
                when(deliveryTaskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

                assertThatThrownBy(() -> deliveryTaskService.failTask(UUID.randomUUID(), "Reason"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Task not found");
            }
        }

        @Test
        @DisplayName("Path 3: Failure - Access denied (wrong shipper)")
        void failTask_WrongShipper_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                Account otherShipper = new Account();
                otherShipper.setId(UUID.randomUUID());
                otherShipper.setRole(Role.SHIPPER);

                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(otherShipper);
                when(deliveryTaskRepository.findById(task.getId())).thenReturn(Optional.of(task));

                assertThatThrownBy(() -> deliveryTaskService.failTask(task.getId(), "Reason"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Access denied");
            }
        }
    }
}
