package nl.rabobank.customerstatementprocessor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerStatement {

  @NotNull(message = "Enter the transaction reference")
  @ApiModelProperty(value = "A numeric value", required = true)
  @JsonProperty("transaction_reference")
  private Long reference;

  @NotBlank(message = "Enter the account number (IBAN)")
  @ApiModelProperty(value = "An IBAN", required = true)
  @JsonProperty("account_number")
  private String account;

  @NotBlank(message = "Enter the description")
  @ApiModelProperty(value = "Free text description", required = true)
  private String description;

  @NotNull(message = "Enter the start balance")
  @Digits(integer = Integer.MAX_VALUE, fraction = 2)
  @JsonProperty("start_balance")
  @ApiModelProperty(value = "The starting balance in Euros", required = true)
  private BigDecimal startBalance;

  @NotNull(message = "Enter the mutation")
  @Digits(integer = Integer.MAX_VALUE, fraction = 2)
  @ApiModelProperty(value = "Either an addition (+) or a deduction (-)", required = true)
  private BigDecimal mutation;

  @NotNull(message = "Enter the end balance")
  @Digits(integer = Integer.MAX_VALUE, fraction = 2)
  @JsonProperty("end_balance")
  @ApiModelProperty(value = "The end balance in Euros", required = true)
  private BigDecimal endBalance;

  @Override
  public boolean equals(Object o) {
    boolean isEqual;
    if (this == o) {
      isEqual = true;
    } else if (o == null || getClass() != o.getClass()) {
      isEqual = false;
    } else {
      CustomerStatement customerStatement = (CustomerStatement) o;
      isEqual = reference.equals(customerStatement.reference);
    }
    return isEqual;
  }

  @Override
  public int hashCode() {
    return Objects.hash(reference);
  }
}
