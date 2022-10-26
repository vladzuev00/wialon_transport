package by.zuevvlad.wialontransport.kafka.amountbytesfounder;

@FunctionalInterface
public interface AmountObjectBytesFounder<ObjectType> {
    int find(final ObjectType object);
}
