package com.modeling.backend.auth.entity;

import lombok.Data;

@Data
public class Role {
    private Long id;
    private String roleName;
    private Boolean status;
}
