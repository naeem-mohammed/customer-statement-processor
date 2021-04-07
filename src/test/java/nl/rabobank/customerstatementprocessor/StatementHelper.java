package nl.rabobank.customerstatementprocessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;

public class StatementHelper {

  // Helper method to create a number of valid Customer Statement Records
  public static List<CustomerStatement> getValidCustomerStatements(int count) {
    return IntStream.range(1, count + 1)
        .mapToObj(StatementHelper::validCustomerStatement)
        .collect(Collectors.toList());
  }

  // Helper method to create a valid Customer Statement Record with given reference
  public static CustomerStatement validCustomerStatement(int reference) {
    BigDecimal startBalance =
        BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble() * 100)
            .setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal mutation =
        BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble() * 100)
            .setScale(2, RoundingMode.HALF_EVEN);
    boolean operation = ThreadLocalRandom.current().nextBoolean();
    if (!operation)
      mutation = mutation.multiply(BigDecimal.valueOf(-1)).setScale(2, RoundingMode.HALF_EVEN);

    BigDecimal endBalance = startBalance.add(mutation);

    return CustomerStatement.builder()
        .reference((long) reference)
        .account("NL")
        .description("random description")
        .startBalance(startBalance)
        .mutation(mutation)
        .endBalance(endBalance)
        .build();
  }
}
