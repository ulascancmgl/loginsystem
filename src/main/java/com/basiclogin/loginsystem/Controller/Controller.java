package com.basiclogin.loginsystem.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basiclogin.loginsystem.Entity.JwtTokenUtil;
import com.basiclogin.loginsystem.Entity.User;
import com.basiclogin.loginsystem.Entity.UserListDto;
import com.basiclogin.loginsystem.Service.UserService;
import com.basiclogin.loginsystem.Service.UserServiceImpl.UsernameNotChangedException;

import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    private UserService userService;

    public Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ResponseEntity<Void> registerUser() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserData() {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Return the user data as a response
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register/user")
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.registerUser(user.getUserName(), user.getPassword(), user.getRole());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteUser/{userName}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userName) {
        try {
            userService.deleteUser(userName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updatePassword/{userName}")
    public ResponseEntity<Void> updateUserPassword(@PathVariable String userName,
            @RequestBody PasswordChangeRequest request) {
        try {
            userService.updateUserPassword(userName, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/updateUsername/{userId}")
    public ResponseEntity<String> updateUsername(@PathVariable Integer userId,
            @RequestBody UpdateUserNameRequest request) {
        try {
            boolean success = userService.updateUserName(userId, request.getNewUserName());
            if (success) {
                String message = "Username updated successfully.";
                return ResponseEntity.ok().body(message);
            } else {
                LocalDateTime nextChangeTime = LocalDateTime.now().plusMinutes(1);
                return ResponseEntity.ok().body("Cannot change username yet. You can change your username again after "
                        + nextChangeTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(UsernameNotChangedException.class)
    public ResponseEntity<String> handleUsernameNotChangedException(UsernameNotChangedException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getMessage());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserListDto>> getAllUsers() {
        List<UserListDto> userListDto = userService.findAllUsers();
        return ResponseEntity.ok().body(userListDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
        // perform authentication and authorization checks
        boolean isAuthenticated = userService.authenticateUser(user.getUserName(), user.getPassword());

        if (isAuthenticated == false) {
            // Eğer kimlik doğrulama veya yetkilendirme başarısız olursa,
            // kullanıcıya hata mesajı gösterilir ve giriş sayfasına yönlendirilir.
            return ResponseEntity.notFound().build();
        }
        // If successful, generate a JWT token and return it as a response
        String token = JwtTokenUtil.generateToken(user.getUserName());
        return ResponseEntity.ok(token);

    }

}
