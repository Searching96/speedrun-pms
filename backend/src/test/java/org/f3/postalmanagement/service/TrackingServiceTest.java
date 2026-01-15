package org.f3.postalmanagement.service;

import org.f3.postalmanagement.dto.response.order.TrackingEventResponse;
import org.f3.postalmanagement.dto.response.order.TrackingResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.order.TrackingEvent;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.repository.TrackingEventRepository;
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
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrackingService Path Coverage Tests")
class TrackingServiceTest {

    @Mock
    private TrackingEventRepository trackingEventRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private TrackingService trackingService;

    private Account staffAccount;
    private Order order;
    private Office office;
    private TrackingEvent event;

    @BeforeEach
    void setUp() {
        staffAccount = new Account();
        staffAccount.setId(UUID.randomUUID());
        staffAccount.setUsername("staff@test.com");
        staffAccount.setRole(Role.PO_STAFF);

        office = new Office();
        office.setId(UUID.randomUUID());
        office.setOfficeName("Test Office");

        order = Order.builder()
                .trackingNumber("VN12345678901234567")
                .status(OrderStatus.IN_TRANSIT)
                .senderName("Sender")
                .receiverName("Receiver")
                .senderWardCode("001")
                .receiverWardCode("002")
                .build();
        order.setId(UUID.randomUUID());

        event = TrackingEvent.builder()
                .order(order)
                .status("IN_TRANSIT")
                .description("Package is in transit")
                .locationName("Sorting Center")
                .office(office)
                .eventTime(LocalDateTime.now())
                .build();
        event.setId(UUID.randomUUID());
    }

    // ==================== getTrackingInfo Tests ====================
    @Nested
    @DisplayName("getTrackingInfo()")
    class GetTrackingInfoTests {

        @Test
        @DisplayName("Path 1: Success - Returns tracking info with events")
        void getTrackingInfo_WithEvents_Success() {
            when(orderRepository.findByTrackingNumber("VN12345678901234567"))
                    .thenReturn(Optional.of(order));
            when(trackingEventRepository.findByOrderId(eq(order.getId()), any(Sort.class)))
                    .thenReturn(List.of(event));

            TrackingResponse result = trackingService.getTrackingInfo("VN12345678901234567");

            assertThat(result).isNotNull();
            assertThat(result.getOrder().getTrackingNumber()).isEqualTo("VN12345678901234567");
            assertThat(result.getEvents()).hasSize(1);
        }

        @Test
        @DisplayName("Path 2: Success - Returns tracking info with no events")
        void getTrackingInfo_NoEvents_Success() {
            when(orderRepository.findByTrackingNumber("VN12345678901234567"))
                    .thenReturn(Optional.of(order));
            when(trackingEventRepository.findByOrderId(eq(order.getId()), any(Sort.class)))
                    .thenReturn(List.of());

            TrackingResponse result = trackingService.getTrackingInfo("VN12345678901234567");

            assertThat(result).isNotNull();
            assertThat(result.getEvents()).isEmpty();
        }

        @Test
        @DisplayName("Path 3: Failure - Order not found")
        void getTrackingInfo_OrderNotFound_ThrowsException() {
            when(orderRepository.findByTrackingNumber("INVALID"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> trackingService.getTrackingInfo("INVALID"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Order not found");
        }

        @Test
        @DisplayName("Path 4: Event with null office handled correctly")
        void getTrackingInfo_EventWithNullOffice_Success() {
            TrackingEvent eventWithoutOffice = TrackingEvent.builder()
                    .order(order)
                    .status("PENDING")
                    .description("Order created")
                    .locationName("Online")
                    .office(null)
                    .eventTime(LocalDateTime.now())
                    .build();
            eventWithoutOffice.setId(UUID.randomUUID());

            when(orderRepository.findByTrackingNumber("VN12345678901234567"))
                    .thenReturn(Optional.of(order));
            when(trackingEventRepository.findByOrderId(eq(order.getId()), any(Sort.class)))
                    .thenReturn(List.of(eventWithoutOffice));

            TrackingResponse result = trackingService.getTrackingInfo("VN12345678901234567");

            assertThat(result.getEvents()).hasSize(1);
            assertThat(result.getEvents().get(0).getOfficeId()).isNull();
        }
    }

    // ==================== addTrackingEvent Tests ====================
    @Nested
    @DisplayName("addTrackingEvent()")
    class AddTrackingEventTests {

        @Test
        @DisplayName("Path 1: Success - Add tracking event")
        void addTrackingEvent_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(trackingEventRepository.save(any(TrackingEvent.class))).thenAnswer(inv -> {
                    TrackingEvent e = inv.getArgument(0);
                    e.setId(UUID.randomUUID());
                    return e;
                });

                TrackingEventResponse result = trackingService.addTrackingEvent(
                        order.getId(), "IN_TRANSIT", "Package arrived at hub", "Hub A"
                );

                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo("IN_TRANSIT");
                assertThat(result.getDescription()).isEqualTo("Package arrived at hub");
                assertThat(result.getLocationName()).isEqualTo("Hub A");
                verify(trackingEventRepository).save(any(TrackingEvent.class));
            }
        }

        @Test
        @DisplayName("Path 2: Failure - Order not found")
        void addTrackingEvent_OrderNotFound_ThrowsException() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);
                when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

                assertThatThrownBy(() -> trackingService.addTrackingEvent(
                        UUID.randomUUID(), "IN_TRANSIT", "Description", "Location"
                ))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Order not found");
            }
        }

        @Test
        @DisplayName("Path 3: Success - Event saved with all fields")
        void addTrackingEvent_AllFields_Success() {
            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentAccount).thenReturn(staffAccount);
                when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
                when(trackingEventRepository.save(any(TrackingEvent.class))).thenAnswer(inv -> {
                    TrackingEvent e = inv.getArgument(0);
                    e.setId(UUID.randomUUID());
                    return e;
                });

                TrackingEventResponse result = trackingService.addTrackingEvent(
                        order.getId(), "DELIVERED", "Package delivered to recipient", "Recipient Address"
                );

                assertThat(result.getEventTime()).isNotNull();
                assertThat(result.getId()).isNotNull();
            }
        }
    }
}
