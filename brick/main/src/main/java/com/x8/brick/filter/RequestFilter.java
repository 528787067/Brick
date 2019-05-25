package com.x8.brick.filter;

import com.x8.brick.parameter.Request;

public interface RequestFilter<REQUEST extends Request> extends Filter<REQUEST> {
    @Override
    REQUEST doFilter(REQUEST request, Chain<REQUEST> chain);
}
