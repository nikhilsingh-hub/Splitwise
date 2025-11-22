package splitwise.lld.Controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import splitwise.lld.DTOs.BalanceResponseDto;
import splitwise.lld.DTOs.UserRequestDto;
import splitwise.lld.DTOs.UserResponseDto;
import splitwise.lld.Services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponseDto> getUserBalance(@PathVariable Long id) {
        BalanceResponseDto balance = userService.getUserBalance(id);
        return ResponseEntity.ok(balance);
    }
}
