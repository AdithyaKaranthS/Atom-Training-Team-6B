import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Transaction {
    private final int id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;

    public Transaction(int id, TransactionType type, BigDecimal amount, BigDecimal balanceAfter) {
        this.id = id;
        this.type = type;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.balanceAfter = balanceAfter.setScale(2, RoundingMode.HALF_UP);
    }

    public int getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String toCsvRow() {
        return id + "," + type + "," + amount.toPlainString() + "," + balanceAfter.toPlainString();
    }
}
