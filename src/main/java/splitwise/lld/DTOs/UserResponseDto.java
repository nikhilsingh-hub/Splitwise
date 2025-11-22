package splitwise.lld.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
}
