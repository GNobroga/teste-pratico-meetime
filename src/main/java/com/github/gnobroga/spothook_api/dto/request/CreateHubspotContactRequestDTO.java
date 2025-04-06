package com.github.gnobroga.spothook_api.dto.request;

import com.github.gnobroga.spothook_api.model.ContactPayload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateHubspotContactRequestDTO extends ContactPayload {
    @NotNull(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(min = 3, message = "First name must be at least 3 characters")
    private String firstName;

    @Size(min = 3, message = "Last name must be at least 3 characters")
    private String lastName;

    @Pattern(regexp = "^[+\\d][\\d\\s().-]{7,}$", message = "Please provide a valid phone number")
    private String phone;

    @Size(min = 2, message = "Company name must be at least 2 characters")
    private String company;

    @Pattern(
        regexp = "^(https?://)?([\\w.-]+)\\.([a-z]{2,})([/\\w.-]*)*/?$",
        message = "Please provide a valid website URL"
    )
    private String website;
}

