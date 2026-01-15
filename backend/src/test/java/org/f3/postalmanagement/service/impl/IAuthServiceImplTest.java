package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.dto.request.auth.CustomerRegisterRequest;
import org.f3.postalmanagement.dto.response.auth.AuthResponse;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.CustomUserDetails;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.jwt.JwtUtil;
import org.f3.postalmanagement.repository.AccountRepository;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IAuthServiceImpl Path Coverage Tests")
class IAuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private IAuthServiceImpl authService;

    private Account customerAccount;
    private Account employeeAccount;
    private Customer customer;
    private Employee employee;

    @BeforeEach
    void setUp() {
        customerAccount = new Account();
        customerAccount.setId(UUID.randomUUID());
        customerAccount.setUsername("0123456789");
        customerAccount.setPassword("encodedPassword");
        customerAccount.setEmail("customer@test.com");
        customerAccount.setRole(Role.CUSTOMER);

        employeeAccount = new Account();
        employeeAccount.setId(UUID.randomUUID());
        employeeAccount.setUsername("employee@test.com");
        employeeAccount.setPassword("encodedPassword");
        employeeAccount.setRole(Role.PO_STAFF);

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setAccount(customerAccount);
        customer.setFullName("Test Customer");

        employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setAccount(employeeAccount);
        employee.setFullName("Test Employee");
    }

    // ==================== login Tests ====================
    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("Path 1: Success - Customer login with full name from customer")
        void login_AsCustomer_Success() {
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            Authentication authentication = mock(Authentication.class);

            when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(customerAccount));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
            when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.of(customer));

            AuthResponse result = authService.login("0123456789", "password");

            assertThat(result).isNotNull();
            assertThat(result.getToken()).isEqualTo("jwt-token");
            assertThat(result.getRole()).isEqualTo("CUSTOMER");
            assertThat(result.getFullName()).isEqualTo("Test Customer");
        }

        @Test
        @DisplayName("Path 2: Success - Customer login with fallback name (customer not found)")
        void login_AsCustomer_NoProfile_UsesUsername() {
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            Authentication authentication = mock(Authentication.class);

            when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(customerAccount));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
            when(customerRepository.findByAccount(customerAccount)).thenReturn(Optional.empty());

            AuthResponse result = authService.login("0123456789", "password");

            assertThat(result.getFullName()).isEqualTo("0123456789"); // Falls back to username
        }

        @Test
        @DisplayName("Path 3: Success - Employee login with full name from employee")
        void login_AsEmployee_Success() {
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            Authentication authentication = mock(Authentication.class);

            when(accountRepository.findByUsername("employee@test.com")).thenReturn(Optional.of(employeeAccount));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
            when(employeeRepository.findByAccount(employeeAccount)).thenReturn(Optional.of(employee));

            AuthResponse result = authService.login("employee@test.com", "password");

            assertThat(result.getRole()).isEqualTo("PO_STAFF");
            assertThat(result.getFullName()).isEqualTo("Test Employee");
        }

        @Test
        @DisplayName("Path 4: Success - Employee login with fallback name (employee not found)")
        void login_AsEmployee_NoProfile_UsesUsername() {
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            Authentication authentication = mock(Authentication.class);

            when(accountRepository.findByUsername("employee@test.com")).thenReturn(Optional.of(employeeAccount));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
            when(employeeRepository.findByAccount(employeeAccount)).thenReturn(Optional.empty());

            AuthResponse result = authService.login("employee@test.com", "password");

            assertThat(result.getFullName()).isEqualTo("employee@test.com");
        }

        @Test
        @DisplayName("Path 5: Failure - Username not found")
        void login_UsernameNotFound_ThrowsException() {
            when(accountRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login("unknown", "password"))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Invalid username or password");
        }

        @Test
        @DisplayName("Path 6: Failure - Wrong password")
        void login_WrongPassword_ThrowsException() {
            when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(customerAccount));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login("0123456789", "wrongpassword"))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }

    // ==================== register Tests ====================
    @Nested
    @DisplayName("register()")
    class RegisterTests {

        private CustomerRegisterRequest createValidRequest() {
            CustomerRegisterRequest request = new CustomerRegisterRequest();
            request.setUsername("0987654321");
            request.setPassword("password123");
            request.setEmail("newcustomer@test.com");
            request.setFullName("New Customer");
            request.setAddress("123 Test St");
            return request;
        }

        @Test
        @DisplayName("Path 1: Success - New customer registered")
        void register_Success() {
            CustomerRegisterRequest request = createValidRequest();

            when(accountRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
                Account a = inv.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });
            when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

            authService.register(request);

            verify(accountRepository).save(any(Account.class));
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Path 2: Failure - Username already exists")
        void register_UsernameExists_ThrowsException() {
            CustomerRegisterRequest request = createValidRequest();

            when(accountRepository.findByUsername(request.getUsername()))
                    .thenReturn(Optional.of(customerAccount));

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Username already exists");
        }

        @Test
        @DisplayName("Path 3: Failure - Email already exists")
        void register_EmailExists_ThrowsException() {
            CustomerRegisterRequest request = createValidRequest();

            when(accountRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(customerAccount));

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email already exists");
        }
    }
}
