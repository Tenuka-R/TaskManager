package ui;

import database.DBOperations;
import helpers.Placeholders;
import task.Task;
import task.TaskMethods;
import ui.components.ActionEditor;
import ui.components.ActionRenderer;
import ui.dialogs.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private JTable dataTable;
    private JPanel subPanel;
    private JButton suggestButton;
    private JButton plusButton;
    private JComboBox sortOptions;
    private JButton statsButton;
    private JTextField searchText;
    private JButton clearSearch;
    private JLabel sortText;

    private List<Task> displayedTasks = new ArrayList<>();
    private final String searchPlaceholder = "Search...";
    private DBOperations dbOperations = new DBOperations();
    private TaskMethods taskMethods = new TaskMethods(dbOperations);

    public MainWindow() {
        setTitle("Task manager");
        setContentPane(mainPanel);
        dataTable.setRowHeight(30);
        dataTable.setFillsViewportHeight(true);
        dataTable.setShowGrid(false);
        dataTable.setIntercellSpacing(new Dimension(0, 0));
        dataTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        dataTable.getTableHeader().setBackground(new Color(0xF0F0F0));
        dataTable.getTableHeader().setPreferredSize(new Dimension(0, 32));


        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                           boolean focus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, selected, focus, row, column);
                String completed = table.getModel().getValueAt(row,4).toString();
                if (selected) {
                    component.setBackground(Colors.LIGHT_BLUE);
                } else if (completed.equals("Yes")) {
                    component.setBackground(Colors.LIGHT_GREEN);
                } else {
                    component.setBackground(Colors.LIGHT_RED);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return component;
            }
        });
        DefaultTableCellRenderer centerHeadings = new DefaultTableCellRenderer();
        centerHeadings.setHorizontalAlignment(SwingConstants.CENTER);
        dataTable.getTableHeader().setDefaultRenderer(centerHeadings);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        loadAllTasks();

        searchText.addActionListener(e -> searchTasks());
        Placeholders.addPlaceholder(searchText, searchPlaceholder);
        clearSearch.addActionListener(e -> resetSearch());
        suggestButton.addActionListener(e -> showNextTask());

        plusButton.addActionListener(e -> {
            CreateOrEdit createEditWindow = new CreateOrEdit(MainWindow.this, null);
            createEditWindow.setVisible(true);
            loadAllTasks();
        });

        sortOptions.addActionListener(e -> sortTasks());
        setVisible(true);

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayStats();
            }
        });
    }

    private void loadAllTasks() {
        refreshTable(dbOperations.getTasks());
    }

    public void toggleComplete(int rowNumber) {
        Task selectedTask = displayedTasks.get(rowNumber);
        dbOperations.toggleComplete(selectedTask);
        loadAllTasks();
    }

    public void deleteTask(int rowNumber) {
        Task selectedTask = displayedTasks.get(rowNumber);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this task?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        dbOperations.deleteTask(selectedTask);
        loadAllTasks();
    }

    public void editTask(int rowNumber) {
        Task selectedTask = displayedTasks.get(rowNumber);
        CreateOrEdit editWindow = new CreateOrEdit(this, selectedTask);
        editWindow.setVisible(true);
        loadAllTasks();
    }

    private void refreshTable(List<Task> tasks) {
        displayedTasks = tasks;
        String[] columnNames = {"Title", "Description", "Deadline", "Priority", "Completed", "Actions"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        for (Task task : tasks) {
            String completed = task.getCompleted() ? "Yes" : "No";
            model.addRow(new Object[]{
                    task.getTitle(), task.getDescription(), task.getDeadline(),
                    task.getPriority(), completed, "Actions"
            });
        }

        dataTable.setModel(model);
        dataTable.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        dataTable.getColumnModel().getColumn(5).setCellEditor(new ActionEditor(this));
        dataTable.getColumnModel().getColumn(5).setPreferredWidth(240);
        dataTable.getColumnModel().getColumn(5).setMinWidth(240);
    }

    private void resetSearch() {
        searchText.setText(searchPlaceholder);
        loadAllTasks();
    }

    private void searchTasks() {
        String inputText = searchText.getText();
        List<Task> filteredTasks = dbOperations.searchTasks(inputText);
        refreshTable(filteredTasks);
    }

    private void sortTasks() {
        String sortField = sortOptions.getSelectedItem().toString();
        if (sortField.equals("(none)")) {
            loadAllTasks();
        } else {
            refreshTable(dbOperations.sortTasks(sortField));
        }
    }

    private void showNextTask() {
        Task nextTask = taskMethods.getNextTask();
        if (nextTask == null) {
            JOptionPane.showMessageDialog(this, "No tasks have been created yet",
                    "No tasks created", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    nextTask.expandView(),
                    "Next suggested task",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void displayStats() {
        StatsDialog.display(this, dbOperations.getStatistics());
    }


    public static void main(String[] args) {
        MainWindow window = new MainWindow();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        dataTable = new JTable();
        scrollPane1.setViewportView(dataTable);
        subPanel = new JPanel();
        subPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(subPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        plusButton = new JButton();
        plusButton.setText("+");
        subPanel.add(plusButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        subPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        searchText = new JTextField();
        searchText.setText("Search...");
        subPanel.add(searchText, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        suggestButton = new JButton();
        suggestButton.setText("Suggest next");
        subPanel.add(suggestButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearSearch = new JButton();
        clearSearch.setText("Reset search");
        subPanel.add(clearSearch, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sortText = new JLabel();
        sortText.setText("Sort by:");
        subPanel.add(sortText, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sortOptions = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("(none)");
        defaultComboBoxModel1.addElement("title");
        defaultComboBoxModel1.addElement("description");
        defaultComboBoxModel1.addElement("priority");
        defaultComboBoxModel1.addElement("deadline");
        defaultComboBoxModel1.addElement("completed");
        sortOptions.setModel(defaultComboBoxModel1);
        subPanel.add(sortOptions, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}