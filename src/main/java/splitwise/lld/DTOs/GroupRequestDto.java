package splitwise.lld.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupRequestDto {
    @NotBlank(message = "Group name is required")
    private String name;
    
    @NotNull(message = "Creator ID is required")
    private Long createdById;
    
    @NotEmpty(message = "At least one member is required")
    private List<Long> memberIds;
}
