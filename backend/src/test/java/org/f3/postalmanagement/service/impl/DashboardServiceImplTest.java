package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.dto.request.user.RegisterSystemAdminRequest;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardServiceImpl Path Coverage Tests")
class DashboardServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Account existingAccount;

    @BeforeEach
    void setUp() {
        existingAccount = new Account();
        existingAccount.setId(UUID.randomUUID());
        existingAccount.setUsername("existing_admin");
        existingAccount.setEmail("existing@test.com");
        existingAccount.setRole(Role.SYSTEM_ADMIN);
    }

    // ==================== registerNewAdmin Tests ====================
    @Nested
    @DisplayName("registerNewAdmin()")
    class RegisterNewAdminTests {

        private RegisterSystemAdminRequest createValidRequest() {
            return RegisterSystemAdminRequest.builder()
                .username("new_admin")
                .password("securePassword123")
                .email("newadmin@test.com")
                .build();
        }

        @Test
        @DisplayName("Path 1: Success - New admin registered")
        void registerNewAdmin_Success() {
            RegisterSystemAdminRequest request = createValidRequest();

            when(accountRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
                Account a = inv.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });

            dashboardService.registerNewAdmin(request);

            verify(accountRepository).save(argThat(account -> 
                account.getRole() == Role.SYSTEM_ADMIN &&
                account.isActive() &&
                account.getUsername().equals("new_admin") &&
                account.getEmail().equals("newadmin@test.com")
            ));
        }

        @Test
        @DisplayName("Path 2: Failure - Username already exists")
        void registerNewAdmin_UsernameExists_ThrowsException() {
            RegisterSystemAdminRequest request = createValidRequest();
            request.setUsername("existing_admin");

            when(accountRepository.findByUsername("existing_admin")).thenReturn(Optional.of(existingAccount));

            assertThatThrownBy(() -> dashboardService.registerNewAdmin(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Username already exists");

            verify(accountRepository, never()).save(any());
        }

        @Test
        @DisplayName("Path 3: Failure - Email already exists")
        void registerNewAdmin_EmailExists_ThrowsException() {
            RegisterSystemAdminRequest request = createValidRequest();
            request.setEmail("existing@test.com");

            when(accountRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(accountRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(existingAccount));

            assertThatThrownBy(() -> dashboardService.registerNewAdmin(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already exists");

            verify(accountRepository, never()).save(any());
        }

        @Test
        @DisplayName("Path 4: Verifies password is encoded")
        void registerNewAdmin_PasswordEncoded() {
            RegisterSystemAdminRequest request = createValidRequest();

            when(accountRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode("securePassword123")).thenReturn("encoded_secure_password");
            when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

            dashboardService.registerNewAdmin(request);

            verify(passwordEncoder).encode("securePassword123");
            verify(accountRepository).save(argThat(account -> 
                account.getPassword().equals("encoded_secure_password")
            ));
        }
    }
}
