package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.UserCreateRequest;
import com.bookmanagement.bookmanagementapp.dto.UserRegisterRequest;
import com.bookmanagement.bookmanagementapp.dto.UserResponse;
import com.bookmanagement.bookmanagementapp.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse registerUser(UserRegisterRequest request);

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    List<UserResponse> getAllUsers();
}