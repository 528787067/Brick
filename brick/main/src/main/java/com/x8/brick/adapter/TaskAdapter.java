package com.x8.brick.adapter;

import com.x8.brick.task.Task;

import java.lang.reflect.Type;

public interface TaskAdapter<T extends Task, TASK> extends Adapter<T, TASK> {
    @Override
    TASK adapt(T task, Type type);
}
