package org.example.tennis_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpDTO {
    private String username;
    private String password;
    private String name;
    private String userTypeCode;
}
