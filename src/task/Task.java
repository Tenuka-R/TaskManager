package task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate deadline;
    private Priority priority;
    private boolean completed;

    public Task(int id, String title, String description, LocalDate deadline, Priority priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = completed;
    }

    public Task(String title, String description, LocalDate deadline, Priority priority) {
        this.id = -1;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = false;
    }

    @Override
    public String toString() {return this.title;}

    public int getId() {return this.id;}
    public String getTitle() {return this.title;}
    public String getDescription() {return this.description;}
    public LocalDate getDeadline() {return this.deadline;}
    public String getPriority() {return this.priority.toString();}
    public boolean getCompleted() {return this.completed;}

    public float getScore() {
        long daysRemaining = Math.max(this.getDaysBetween(), 0);
        return 1f / (this.getPriorityWeight() + daysRemaining);
    }

    public float getPriorityWeight() {

        if (this.priority.equals(Priority.high)) {
            return 1;
        }
        if (this.priority.equals(Priority.medium)) {
            return 2;
        }
        if (this.priority.equals(Priority.low)) {
            return 3;
        }
        else {return 0;}
    }

    public long getDaysBetween() {
        LocalDate today = LocalDate.now();
        return today.until(this.deadline, ChronoUnit.DAYS);
    }

    public void setId(int id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setDeadline(LocalDate deadline) {this.deadline = deadline;}
    public void setPriority(String priority) {this.priority = Priority.valueOf(priority);}

    public String expandView() {
        return "Title: " + title + "\nDescription: " + description + "\nDeadline: " +
                deadline + "\nPriority: " + priority.toString();
    }
}