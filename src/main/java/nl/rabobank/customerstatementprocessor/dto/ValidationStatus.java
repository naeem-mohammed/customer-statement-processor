package nl.rabobank.customerstatementprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ValidationStatus {
  SUCCESSFUL("SUCCESSFUL"),
  DUPLICATE_REFERENCE("DUPLICATE_REFERENCE"),
  INCORRECT_END_BALANCE("INCORRECT_END_BALANCE"),
  DUPLICATE_AND_INCORRECT_END_BALANCE(
      DUPLICATE_REFERENCE.value + "_" + INCORRECT_END_BALANCE.value);

  @Getter private String value;
}
