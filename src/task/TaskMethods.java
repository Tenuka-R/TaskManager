package task;

import database.DBOperations;

import java.util.List;

public class TaskMethods {
    private final DBOperations dbOperations;

    public TaskMethods(DBOperations dbOperations) {
        this.dbOperations = dbOperations;
    }

    public Task getNextTask() {
        List<Task> pending = dbOperations.getPendingTasks();
        if (pending.isEmpty()) {
            return null;
        }
        Task best = pending.get(0);
        for (Task task : pending) {
            if (task.getScore() > best.getScore()) {
                best = task;
            }
        }
        return best;
    }
}