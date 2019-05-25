package com.x8.brick.converter;

import com.x8.brick.task.Task;

import java.lang.reflect.Type;

public interface TaskConverter<T extends Task, TASK> extends Converter<T, TASK> {
    @Override
    TASK convert(T task, Type taskType);
}
