package com.productmanagement.model.input;

import java.util.List;

import com.productmanagement.model.Role;

import lombok.Data;

@Data
public class UserInput {
	private String name;
    private String email;
    private String password;
    private List<Role> roles;
}
