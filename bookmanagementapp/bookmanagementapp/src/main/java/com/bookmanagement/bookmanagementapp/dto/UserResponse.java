package com.bookmanagement.bookmanagementapp.dto;

import com.bookmanagement.bookmanagementapp.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    public Long id;
    public String username;
    public String email;
    Role role;
}
