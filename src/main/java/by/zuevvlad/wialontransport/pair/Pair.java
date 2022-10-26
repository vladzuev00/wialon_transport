package by.zuevvlad.wialontransport.pair;

import java.util.Objects;

import static java.util.Objects.hash;

public final class Pair<FirstType, SecondType> {
    private final FirstType first;
    private final SecondType second;

    public Pair(final FirstType first, final SecondType second) {
        this.first = first;
        this.second = second;
    }

    public FirstType getFirst() {
        return this.first;
    }

    public SecondType getSecond() {
        return this.second;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (this.getClass() != otherObject.getClass()) {
            return false;
        }
        final Pair<FirstType, SecondType> other = (Pair<FirstType, SecondType>) otherObject;
        return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
    }

    @Override
    public int hashCode() {
        return hash(this.first, this.second);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[first = " + this.first + ", second = " + this.second + "]";
    }
}
