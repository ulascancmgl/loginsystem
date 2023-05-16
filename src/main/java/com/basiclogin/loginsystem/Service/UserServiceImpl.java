package com.basiclogin.loginsystem.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.basiclogin.loginsystem.Entity.Role;
import com.basiclogin.loginsystem.Entity.User;
import com.basiclogin.loginsystem.Entity.UserInfo;
import com.basiclogin.loginsystem.Entity.UserInfoDto;
import com.basiclogin.loginsystem.Entity.UserListDto;
import com.basiclogin.loginsystem.Repository.UserInfoRepository;
import com.basiclogin.loginsystem.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public UserServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserInfoRepository userInfoRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User registerUser(String userName, String password, Role role) {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(String userName) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public void updateUserPassword(String userName, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isCurrentPasswordCorrect = passwordEncoder.matches(currentPassword, user.getPassword());
            if (isCurrentPasswordCorrect) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            } else {
                throw new RuntimeException("Current password is incorrect");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public boolean updateUserName(Integer userId, String newUsername) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            LocalDateTime lastUsernameChange = user.getLastUserNameChange();
            LocalDateTime currentTime = LocalDateTime.now();
            Duration timeSinceLastChange = Duration.between(lastUsernameChange, currentTime);
            // // long hoursSinceLastChange = timeSinceLastChange.toHours();
            // // if (hoursSinceLastChange >= 24) {
            long minutesSinceLastChange = timeSinceLastChange.toMinutes();
            if (!newUsername.equals(user.getUserName())) {
                if (minutesSinceLastChange >= 1) {
                    user.setUserName(newUsername);
                    user.setLastUserNameChange(currentTime);
                    userRepository.save(user);
                    return true;
                } else {
                    return false;
                }
            } else {
                throw new UsernameNotChangedException("New username is same as old username");
            }
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public class UsernameNotChangedException extends RuntimeException {
        public UsernameNotChangedException(String message) {
            super(message);
        }
    }

    @Override
    public List<UserListDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserListDto mapToUserDto(User user) {
        UserListDto userListDto = new UserListDto();
        userListDto.setUserName(user.getUserName());
        userListDto.setRole(user.getRole());
        return userListDto;
    }

    @Override
    public boolean authenticateUser(String userName, String password) {

        User user = userRepository.findByuserName(userName);

        if (user == null) {
            return false;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void saveUserInfo(String userName, UserInfoDto userInfoDto) {
        User user = userRepository.findByuserName(userName);
        if (user == null) {
            user = new User(userName);
            userRepository.save(user);
        }
        if (!user.getUserInfos().isEmpty()) {
            throw new IllegalStateException("User info already exists for user " + userName);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        userInfo.setFirstName(userInfoDto.getFirstName());
        userInfo.setLastName(userInfoDto.getLastName());
        userInfo.setBirthDate(userInfoDto.getBirthDate());
        userInfo.setGender(userInfoDto.getGender());
        userInfo.setAge();
        userInfoRepository.save(userInfo);
    }

    @Override
    @Transactional
    public void patchUserInfo(String userName, UserInfoDto userInfoUpdates) {
        User user = userRepository.findByuserName(userName);
        if (user == null) {
            throw new IllegalArgumentException("User " + userName + " not found");
        }
        if (user.getUserInfos().isEmpty()) {
            throw new IllegalStateException("User info not found for user " + userName);
        }
        UserInfo userInfo = user.getUserInfos().get(0);

        if (userInfoUpdates.getFirstName() != null) {
            userInfo.setFirstName(userInfoUpdates.getFirstName());
        }
        if (userInfoUpdates.getLastName() != null) {
            userInfo.setLastName(userInfoUpdates.getLastName());
        }
        if (userInfoUpdates.getBirthDate() != null) {
            userInfo.setBirthDate(userInfoUpdates.getBirthDate());
        }
        if (userInfoUpdates.getGender() != null) {
            userInfo.setGender(userInfoUpdates.getGender());
        }

        userInfo.setAge(); // yaş bilgisini güncelle
        userInfoRepository.save(userInfo);
    }

    @Override
    @Transactional
    public UserInfoDto getUserInfo(String userName) {
        User user = userRepository.findByuserName(userName);
        if (user != null && !user.getUserInfos().isEmpty()) {
            UserInfo userInfo = user.getUserInfos().get(0);
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setFirstName(userInfo.getFirstName());
            userInfoDto.setLastName(userInfo.getLastName());
            userInfoDto.setBirthDate(userInfo.getBirthDate());
            userInfoDto.setGender(userInfo.getGender());
            userInfoDto.setAge();

            // Diğer UserInfo alanlarını ayarlayabilirsiniz

            return userInfoDto;
        } else {
            return null;
        }
    }
}
