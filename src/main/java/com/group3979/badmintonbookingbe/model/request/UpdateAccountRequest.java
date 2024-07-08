package com.group3979.badmintonbookingbe.model.request;

import com.group3979.badmintonbookingbe.model.response.AuthenticationResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountRequest extends AuthenticationResponse {
    private String password;
}
