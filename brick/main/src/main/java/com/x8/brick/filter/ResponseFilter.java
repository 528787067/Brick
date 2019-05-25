package com.x8.brick.filter;

import com.x8.brick.parameter.Response;

public interface ResponseFilter<RESPONSE extends Response> extends Filter<RESPONSE> {
    @Override
    RESPONSE doFilter(RESPONSE response, Chain<RESPONSE> chain);
}
