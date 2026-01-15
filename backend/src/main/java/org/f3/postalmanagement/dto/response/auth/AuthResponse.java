package org.f3.postalmanagement.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Authentication response (JWT token)")
public class AuthResponse {

    @Schema(description = "JWT token")
    private String token;

    @Schema(description = "User role (CUSTOMER, SHIPPER, PO_STAFF, etc.)")
    private String role;

    @Schema(description = "User ID (Account or Actor ID)")
    private java.util.UUID id;

    @Schema(description = "Full name of the user")
    private String fullName;
}
