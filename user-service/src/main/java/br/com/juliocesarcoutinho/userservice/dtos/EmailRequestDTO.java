package br.com.juliocesarcoutinho.userservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> variables;
}
