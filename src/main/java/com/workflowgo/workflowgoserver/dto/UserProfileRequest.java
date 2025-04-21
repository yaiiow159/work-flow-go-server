package com.workflowgo.workflowgoserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
