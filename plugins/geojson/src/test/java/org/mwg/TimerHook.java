package org.mwg;

import org.mwg.task.TaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;
import org.mwg.task.TaskHookFactory;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by gnain on 15/09/16.
 */
public class TimerHook implements TaskHook {

    class Record {
        public String context;
        public String parentContext;
        public long start;
        public long end;
        public long duration;
        public TaskAction action;
        public int level;
        ArrayList<Record> activities = new ArrayList();

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < level; i++) {
                sb.append("\t");
            }
            sb.append("Duration:" + duration + " -> ");
            if(action != null) {
                sb.append(action.toString() + "\n");
            } else {
                sb.append(context + "\n");
            }
            for(Record r : activities) {
                sb.append(r.toString());
            }
            return sb.toString();
        }

    }

    ArrayList<Record> _history = new ArrayList();
    Stack<Record> _stack = new Stack();
    int _currentLevel = 0;


    public TimerHook() {
    }

    @Override
    public void start(TaskContext initialContext) {
        System.out.println("Start:" + initialContext.toString());
        Record record = new Record();
        record.context = initialContext.toString();
        record.start = System.currentTimeMillis();
        _stack.push(record);
        _history.add(record);
    }

    @Override
    public void beforeAction(TaskAction action, TaskContext context) {

        Record record = new Record();
        record.context = context.toString();
        record.action = action;
        record.start = System.currentTimeMillis();
        record.level = _currentLevel;

        _stack.push(record);

        ArrayList<Record> activityArray = _history.get(_history.size()-1).activities;
        for (int i = 0; i < _currentLevel; i++) {
            activityArray = activityArray.get(activityArray.size()-1).activities;
        }
        activityArray.add(record);
    }

    @Override
    public void afterAction(TaskAction action, TaskContext context) {
        Record record = _stack.pop();
        record.end = System.currentTimeMillis();
        record.duration = record.end - record.start;
    }

    @Override
    public void beforeTask(TaskContext parentContext, TaskContext context) {

        Record record = new Record();
        record.context = context.toString();
        record.parentContext = parentContext.toString();
        record.start = System.currentTimeMillis();
        record.level = _currentLevel;
        _stack.push(record);


        ArrayList<Record> activityArray = _history.get(_history.size()-1).activities;
        for (int i = 0; i < _currentLevel; i++) {
            activityArray = activityArray.get(activityArray.size()-1).activities;
        }
        activityArray.add(record);
        _currentLevel++;
    }

    @Override
    public void afterTask(TaskContext context) {

        Record record = _stack.pop();
        record.end = System.currentTimeMillis();
        record.duration = record.end - record.start;
        _currentLevel--;
    }

    @Override
    public void end(TaskContext finalContext) {
        Record record = _stack.pop();
        record.end = System.currentTimeMillis();
        record.duration = record.end - record.start;
        System.out.println("End" + record.toString());
    }






    public static class TimerHookFactory implements TaskHookFactory {
        public TimerHookFactory() {
        }

        public TimerHook newHook() {
            return new TimerHook();
        }
    }



}