package nl.rabobank.customerstatementprocessor.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@JsonInclude(NON_NULL)
public class CommonResponse<T> {

  private String result;

  private List<T> errorRecords;

  public CommonResponse(String result) {
    this.result = result;
    this.errorRecords = Collections.emptyList();
  }

  public CommonResponse(String result, List<T> errorRecords) {
    this(result);
    this.errorRecords = errorRecords;
  }
}
