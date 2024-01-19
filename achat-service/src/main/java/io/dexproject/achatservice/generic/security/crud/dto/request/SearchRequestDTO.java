package io.dexproject.achatservice.generic.security.crud.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class SearchRequestDTO {
    @NotBlank(message = "Veillez renseignez le mot recherché svp!")
    private String text;
    @NotEmpty
    private List<String> fields = new ArrayList<>();
    @Min(1)
    private int limit;
}