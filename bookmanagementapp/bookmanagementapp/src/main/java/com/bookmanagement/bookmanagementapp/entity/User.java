package com.bookmanagement.bookmanagementapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@Column(nullable = false)
    private String username;
@Column(nullable = false, unique = true)
    private String email;

@Column(nullable = false)
    private String password;

@Enumerated(EnumType.STRING)
    Role role;
}
