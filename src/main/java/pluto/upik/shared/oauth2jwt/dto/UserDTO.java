package pluto.upik.shared.oauth2jwt.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String role;
    private String name;
    private String username;
}