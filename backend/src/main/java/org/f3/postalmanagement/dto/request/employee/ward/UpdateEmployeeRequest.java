package org.f3.postalmanagement.dto.request.employee.ward;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request to update an existing employee")
public class UpdateEmployeeRequest {

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full name of the employee", example = "John Doe")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    @Schema(description = "Phone number (used as username)", example = "0987654321")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "New password (leave blank to keep current password)", example = "newsecurepassword")
    private String password;
}
