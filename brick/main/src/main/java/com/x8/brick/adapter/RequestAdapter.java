package com.x8.brick.adapter;

import java.lang.reflect.Type;

public interface RequestAdapter<OBJECT, REQUEST> extends Adapter<OBJECT, REQUEST> {
    @Override
    REQUEST adapt(OBJECT request, Type type);
}
