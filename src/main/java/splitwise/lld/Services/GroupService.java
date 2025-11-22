package splitwise.lld.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import splitwise.lld.DTOs.GroupRequestDto;
import splitwise.lld.DTOs.GroupResponseDto;
import splitwise.lld.DTOs.UserResponseDto;
import splitwise.lld.Exceptions.GroupNotFoundException;
import splitwise.lld.Exceptions.UserNotFoundException;
import splitwise.lld.Repositories.GroupRepository;
import splitwise.lld.models.Group;
import splitwise.lld.models.User;

import java.util.Date;
import java.util.List;

@Service
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserService userService;
    
    public GroupResponseDto createGroup(GroupRequestDto groupRequestDto) {
        Group group = new Group();
        group.setName(groupRequestDto.getName());
        
        // Set creator
        User creator = userService.findById(groupRequestDto.getCreatedById());
        group.setCreated_By(creator);
        
        // Set members
        List<User> members = groupRequestDto.getMemberIds().stream()
                .map(userService::findById)
                .toList();
        group.setMember(members);
        
        group.setCreatedAt(new Date());
        group.setUpdatedAt(new Date());
        
        Group savedGroup = groupRepository.save(group);
        return convertToResponseDto(savedGroup);
    }
    
    public GroupResponseDto getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + id));
        return convertToResponseDto(group);
    }
    
    public List<GroupResponseDto> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    
    public GroupResponseDto updateGroup(Long id, GroupRequestDto groupRequestDto) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + id));
        
        group.setName(groupRequestDto.getName());
        
        // Update members
        List<User> members = groupRequestDto.getMemberIds().stream()
                .map(userService::findById)
                .toList();
        group.setMember(members);
        
        group.setUpdatedAt(new Date());
        
        Group updatedGroup = groupRepository.save(group);
        return convertToResponseDto(updatedGroup);
    }
    
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new GroupNotFoundException("Group not found with id: " + id);
        }
        groupRepository.deleteById(id);
    }
    
    private GroupResponseDto convertToResponseDto(Group group) {
        GroupResponseDto dto = new GroupResponseDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        
        // Convert creator
        UserResponseDto creatorDto = new UserResponseDto();
        creatorDto.setId(group.getCreated_By().getId());
        creatorDto.setName(group.getCreated_By().getName());
        creatorDto.setPhoneNumber(group.getCreated_By().getPhoneNumber());
        creatorDto.setEmail(group.getCreated_By().getEmail());
        dto.setCreatedBy(creatorDto);
        
        // Convert members
        List<UserResponseDto> memberDtos = group.getMember().stream()
                .map(member -> {
                    UserResponseDto memberDto = new UserResponseDto();
                    memberDto.setId(member.getId());
                    memberDto.setName(member.getName());
                    memberDto.setPhoneNumber(member.getPhoneNumber());
                    memberDto.setEmail(member.getEmail());
                    return memberDto;
                })
                .toList();
        dto.setMembers(memberDtos);
        
        return dto;
    }
    
    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + id));
    }
}
