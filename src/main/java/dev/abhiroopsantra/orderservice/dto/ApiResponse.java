package dev.abhiroopsantra.orderservice.dto;

import lombok.*;

import java.util.HashMap;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor public class ApiResponse {
    public String                  errCode;
    public String                  errMessage;
    public HashMap<String, Object> data;
}
