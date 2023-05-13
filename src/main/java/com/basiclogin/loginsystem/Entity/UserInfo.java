package com.basiclogin.loginsystem.Entity;

import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_infos")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer infoId;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "user_name", referencedColumnName = "userName")
    private User user;

    public int getAge() {
        LocalDate now = LocalDate.now();
        return Period.between(birthDate, now).getYears();
    }

    public void setAge() {
        LocalDate now = LocalDate.now();
        this.age = Period.between(birthDate, now).getYears();
    }
}
