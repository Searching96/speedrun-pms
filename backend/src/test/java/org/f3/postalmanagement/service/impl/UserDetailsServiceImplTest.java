package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.CustomUserDetails;
import org.f3.postalmanagement.exception.AccountNotFoundException;
import org.f3.postalmanagement.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Path Coverage Tests")
class UserDetailsServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Account account;
    private final String username = "testuser";
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(userId);
        account.setUsername(username);
        account.setPassword("password");
        account.setActive(true);
    }

    @Nested
    @DisplayName("loadUserByUsername()")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Path 1: Success - User found by username")
        void loadUserByUsername_Success() {
            when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));

            UserDetails result = userDetailsService.loadUserByUsername(username);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
            assertThat(result).isInstanceOf(CustomUserDetails.class);
        }

        @Test
        @DisplayName("Path 2: Failure - User not found by username")
        void loadUserByUsername_NotFound_ThrowsException() {
            when(accountRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("loadUserById()")
    class LoadUserByIdTests {

        @Test
        @DisplayName("Path 1: Success - User found by ID")
        void loadUserById_Success() {
            when(accountRepository.findById(userId)).thenReturn(Optional.of(account));

            CustomUserDetails result = userDetailsService.loadUserById(userId);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
        }

        @Test
        @DisplayName("Path 2: Failure - User not found by ID")
        void loadUserById_NotFound_ThrowsException() {
            UUID unknownId = UUID.randomUUID();
            when(accountRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userDetailsService.loadUserById(unknownId))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining("User not found with id");
        }
    }
}
