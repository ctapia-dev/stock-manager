package com.cony.stockmanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String name;

    @Size(max = 100)
    private String contactName;

    @Email(message = "Email inválido")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;
}