package org.example.tennis_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSignUpDTO {
    private String username;
    private String password;
    private String name;
    private String userTypeCode;
}
