package database;

import task.Priority;
import task.Task;
import task.TaskStats;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBOperations {

    // READ METHODS

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY id";

        try (Connection connection = DBConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(rowsToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> getPendingTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE completed = false ORDER BY id";

        try (Connection connection = DBConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(rowsToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> sortTasks(String columnName) {
        List<Task> tasks = new ArrayList<>();
        String sql = sortQuery(columnName);

        try (Connection connection = DBConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(rowsToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> searchTasks(String searchTitle) {
        List<Task> matching = new ArrayList<>();
        String sql = "SELECT id, title, description, deadline, priority, completed " +
                "FROM tasks WHERE title ILIKE ? ORDER BY id";

        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + searchTitle + "%");

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    matching.add(rowsToTask(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matching;
    }

    public TaskStats getStatistics() {
        String sql = """
        SELECT 
            COUNT(*) AS total,
            COUNT(*) FILTER (WHERE priority = 'high') AS high_priority,
            COUNT(*) FILTER (WHERE priority = 'medium') AS medium_priority,
            COUNT(*) FILTER (WHERE priority = 'low') AS low_priority,
            COUNT(*) FILTER (WHERE completed = true) AS completed,
            COUNT(*) FILTER (WHERE completed = false) AS pending
        FROM tasks
        """;

        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                return new TaskStats(
                        rs.getInt("total"),
                        rs.getInt("high_priority"),
                        rs.getInt("medium_priority"),
                        rs.getInt("low_priority"),
                        rs.getInt("completed"),
                        rs.getInt("pending")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TaskStats(0, 0, 0, 0, 0, 0);
    }

    // WRITE METHODS

    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, deadline, priority) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setDate(3, Date.valueOf(task.getDeadline()));
            statement.setString(4, task.getPriority());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, deadline = ?, priority = ? WHERE id = ?";

        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setDate(3, Date.valueOf(task.getDeadline()));
            statement.setString(4, task.getPriority());
            statement.setInt(5, task.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void toggleComplete(Task task) {
        String sql = "UPDATE tasks SET completed = NOT completed WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(Task task) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // HELPER METHODS

    private static final List<String> SORT_COLUMNS = List.of(
            "id",
            "title",
            "description",
            "deadline",
            "priority",
            "completed"
    );

    private Task rowsToTask(ResultSet rs) throws SQLException {
        return new Task(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getDate("deadline").toLocalDate(),
            Priority.valueOf(rs.getString("priority")),
            rs.getBoolean("completed")
        );
    }

    private String sortQuery(String sortBy) {
        if (sortBy.equals("priority")) {
            return """
                SELECT * FROM tasks
                ORDER BY
                    CASE priority
                        WHEN 'high' THEN 1
                        WHEN 'medium' THEN 2
                        WHEN 'low' THEN 3
                    END
                """;
        }
        if (!SORT_COLUMNS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort column");
        }
        return "SELECT * FROM tasks ORDER BY " + sortBy;
    }
}
