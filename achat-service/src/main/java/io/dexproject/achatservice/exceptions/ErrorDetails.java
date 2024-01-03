package io.dexproject.achatservice.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorDetails {
   // private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(String message, String details) {
      //  this.timestamp = new Date();
        this.message =  message;
        this.details = details;
    }

}
