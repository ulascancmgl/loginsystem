package com.basiclogin.loginsystem.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basiclogin.loginsystem.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);

    Optional<User> findById(Integer userId);

    User findByuserName(String userName);

}
