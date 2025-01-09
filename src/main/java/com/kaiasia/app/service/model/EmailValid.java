package com.kaiasia.app.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailValid {
    @Email(message = "invalid email")
    private String email;
}
