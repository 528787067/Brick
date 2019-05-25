package com.x8.brick.adapter;

import java.lang.reflect.Type;

public interface Adapter<F, T> {
    T adapt(F value, Type type);
}
