package com.basiclogin.loginsystem.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserListDto {
    private String userName;
    private Role role;

    public UserListDto(User user) {
        this.userName = user.getUserName();
        this.role = user.getRole();
    }

}
