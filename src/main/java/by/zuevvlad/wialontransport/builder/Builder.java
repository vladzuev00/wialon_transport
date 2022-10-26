package by.zuevvlad.wialontransport.builder;

@FunctionalInterface
public interface Builder<BuiltObjectType> {
    BuiltObjectType build();
}
