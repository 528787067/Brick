package com.x8.brick.filter;

import java.util.List;

public class FilterChain<FILTER extends Filter<T>, T> implements Filter.Chain<T> {

    private List<FILTER> filters;

    public FilterChain(List<FILTER> filters) {
        this.filters = filters;
    }

    @Override
    public T doFilter(T data) {
        if (filters != null && filters.size() > 0) {
            FILTER filter = filters.get(0);
            FilterChain<FILTER, T> next = new FilterChain<>(filters.subList(1, filters.size()));
            return filter.doFilter(data, next);
        }
        return data;
    }
}
