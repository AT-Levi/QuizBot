package uz.pdp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quizz {

    @JsonProperty
    String question;
    @JsonProperty
    String Aoption;
    @JsonProperty
    String Boption;
    @JsonProperty
    String Coption;
    @JsonProperty
    String Doption;
    @JsonProperty
    String rightOption;
}
