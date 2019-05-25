package com.x8.brick.adapter;

import com.x8.brick.converter.TaskConverter;
import com.x8.brick.task.Task;
import com.x8.brick.utils.ConvertUtils;

import java.lang.reflect.Type;
import java.util.List;

public class TaskConverterAdapter<T extends Task, TASK> implements TaskAdapter<T, TASK> {

    private List<TaskConverter<T, TASK>> taskConverters;

    public TaskConverterAdapter(List<TaskConverter<T, TASK>> taskConverters) {
        this.taskConverters = taskConverters;
    }

    @Override
    public TASK adapt(T task, Type taskType) {
        return ConvertUtils.convert(task, taskType, taskConverters);
    }
}
