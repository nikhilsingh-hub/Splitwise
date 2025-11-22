package splitwise.lld.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupResponseDto {
    private Long id;
    private String name;
    private UserResponseDto createdBy;
    private List<UserResponseDto> members;
}
