package com.x8.brick.filter;

public interface Filter<T> {
    T doFilter(T data, Chain<T> chain);

    interface Chain<T> {
        T doFilter(T data);
    }
}
