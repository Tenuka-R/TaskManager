package ui.components;

import task.Task;
import ui.MainWindow;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.List;

public class ActionEditor extends AbstractCellEditor implements TableCellEditor {
    private MainWindow window;
    private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
    private int currentRow;

    public ActionEditor(MainWindow window) {
        this.window = window;

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton compelteButton = new JButton("Complete");

        editButton.addActionListener(e -> {
            fireEditingStopped();
            window.editTask(currentRow);
        });

        deleteButton.addActionListener(e -> {
            fireEditingStopped();
            window.deleteTask(currentRow);
        });

        compelteButton.addActionListener(e -> {
            fireEditingStopped();
            window.toggleComplete(currentRow);
        });

        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(compelteButton);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        currentRow = row;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Actions";
    }
}