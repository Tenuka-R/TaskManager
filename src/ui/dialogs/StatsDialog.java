package ui.dialogs;

import database.DBOperations;
import task.TaskStats;

import javax.swing.*;
import java.awt.*;

public class StatsDialog {

    public static void display(Component parent, TaskStats stats) {
        if (stats.total() == 0) {
            JOptionPane.showMessageDialog(parent, "There are no pending tasks");
            return;
        }
        JOptionPane.showMessageDialog(parent, """
                Total tasks: %d
                High priority: %d
                Medium priority: %d
                Low priority: %d
                Compelted: %d
                Pending: %d
                """.formatted(stats.total(), stats.highPriority(), stats.mediumPriority(),
                stats.lowPriority(), stats.completed(), stats.pending()));
    }
}
