package br.com.dio.model;

import java.util.Objects;

public class Space {
    private final Integer expected;
    private final boolean fixed;
    private Integer actual;

    public Space(Integer expected, boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        this.actual = fixed ? expected : null;
    }

    public Integer getExpected() {
        return expected;
    }

    public Integer getActual() {
        return actual;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setActual(Integer actual) {
        if (!fixed) {
            this.actual = actual;
        }
    }

    public void clear() {
        if (!fixed) {
            this.actual = null;
        }
    }

    public boolean isValid() {
        if (actual == null) return true;
        return actual.equals(expected);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Space space = (Space) o;
        return fixed == space.fixed && 
               Objects.equals(expected, space.expected) && 
               Objects.equals(actual, space.actual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expected, fixed, actual);
    }
}
