package com.basiclogin.loginsystem.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private Integer age;

    public int getAge() {
        LocalDate now = LocalDate.now();
        Period.between(birthDate, now).getYears();
        return age;
    }

    public void setAge() {
        LocalDate now = LocalDate.now();
        this.age = Period.between(birthDate, now).getYears();
    }
}
