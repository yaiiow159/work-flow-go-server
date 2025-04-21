package com.workflowgo.workflowgoserver.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String name;
    private String email;
    private String bio;
    private String phone;
    private String location;
    private String company;
    private String position;
    private String photoURL;
}
