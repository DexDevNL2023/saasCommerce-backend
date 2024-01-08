package io.dexproject.achatservice.generic.filter.dto.builder;

import io.dexproject.achatservice.generic.filter.dto.Filter;
import io.dexproject.achatservice.generic.filter.dto.InternalOperator;
import io.dexproject.achatservice.generic.filter.dto.ValueType;

public class FilterBuilder implements Builder<Filter> {

    private final Filter filter;

    public static FilterBuilder createFilter(String field) {
        return new FilterBuilder(field);
    }

    private FilterBuilder(String field){
        this.filter = new Filter();
        filter.setField(field);
    }

    public FilterBuilder value(String value) {
        assert filter != null;
        this.filter.setValue(value);
        return this;
    }

    public FilterBuilder operator(InternalOperator internalOperator){
        assert filter != null;
        this.filter.setOperator(internalOperator);
        return this;
    }

    public FilterBuilder type(ValueType type){
        assert filter != null;
        this.filter.setType(type);
        return this;
    }

    @Override
    public Filter build() {
        return this.filter;
    }
}
