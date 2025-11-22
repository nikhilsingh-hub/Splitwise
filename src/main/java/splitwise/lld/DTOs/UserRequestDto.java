package splitwise.lld.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    @NotBlank(message = "Name is required")
    private String name;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
}
