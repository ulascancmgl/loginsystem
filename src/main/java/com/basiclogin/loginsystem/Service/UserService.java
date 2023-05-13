package com.basiclogin.loginsystem.Service;

import java.util.List;
import java.util.Optional;

import com.basiclogin.loginsystem.Entity.Role;
import com.basiclogin.loginsystem.Entity.User;
import com.basiclogin.loginsystem.Entity.UserInfoDto;
import com.basiclogin.loginsystem.Entity.UserListDto;

public interface UserService {
    public Optional<User> findByUserName(String userName);

    public User registerUser(String userName, String password, Role role);

    public void deleteUser(String userName);

    public void updateUserPassword(String userName, String currentPassword, String newPassword);

    public boolean updateUserName(Integer userId, String newUsername);

    List<UserListDto> findAllUsers();

    public boolean authenticateUser(String userName, String password);

    public void saveUserInfo(String userName, UserInfoDto userInfoDto);

}