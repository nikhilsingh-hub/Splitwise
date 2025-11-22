package splitwise.lld.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import splitwise.lld.DTOs.GroupRequestDto;
import splitwise.lld.DTOs.GroupResponseDto;
import splitwise.lld.Services.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {
    
    @Autowired
    private GroupService groupService;
    
    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(@Valid @RequestBody GroupRequestDto groupRequestDto) {
        GroupResponseDto createdGroup = groupService.createGroup(groupRequestDto);
        return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroupById(@PathVariable Long id) {
        GroupResponseDto group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }
    
    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> getAllGroups() {
        List<GroupResponseDto> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(
            @PathVariable Long id, 
            @Valid @RequestBody GroupRequestDto groupRequestDto) {
        GroupResponseDto updatedGroup = groupService.updateGroup(id, groupRequestDto);
        return ResponseEntity.ok(updatedGroup);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}
