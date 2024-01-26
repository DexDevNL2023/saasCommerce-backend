package io.dexproject.achatservice.generic.security.crud.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    @NotBlank(message = "Veillez renseignez le mot recherché svp!")
    private String text;
    @NotEmpty
    private List<String> fields = new ArrayList<>();
    @Min(1)
    private int limit;
}