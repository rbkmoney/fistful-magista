package com.rbkmoney.fistful.magista.dao.impl.field;

import org.jooq.Comparator;
import org.jooq.Field;

import java.util.Collection;

public class CollectionConditionField<T> implements ConditionField<T, Collection<T>> {

    private final Field<T> field;

    private final Collection<T> value;

    private final Comparator comparator;

    public CollectionConditionField(Field<T> field, Collection<T> value, Comparator comparator) {
        this.field = field;
        this.value = value;
        this.comparator = comparator;
    }

    @Override
    public Field<T> getField() {
        return field;
    }

    @Override
    public Collection<T> getValue() {
        return value;
    }

    @Override
    public Comparator getComparator() {
        return comparator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CollectionConditionField<?> that = (CollectionConditionField<?>) o;

        if (field != null ? !field.equals(that.field) : that.field != null) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        return comparator == that.comparator;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (comparator != null ? comparator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CollectionConditionField{" +
                "field=" + field +
                ", value=" + value +
                ", comparator=" + comparator +
                '}';
    }
}
