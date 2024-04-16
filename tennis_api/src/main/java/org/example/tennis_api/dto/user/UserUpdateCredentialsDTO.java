package org.example.tennis_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateCredentialsDTO {
    private String username;
    private String name;
    private String newPassword;
    private String oldPassword;
}
