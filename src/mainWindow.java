package src;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class mainWindow extends JFrame {

    private JPanel mainPanel;
    private JTable dataTable;
    private JPanel subPanel;
    private JButton suggestButton;
    private JTextField searchText;
    private JButton resetButton;
    private JButton completeButton;
    private JButton plusButton;
    private JLabel sortText;
    private JComboBox sortOptions;
    private JButton statsButton;

    private List<Task> displayedTasks = new ArrayList<>();
    private final TaskManager taskManager = new TaskManager();
    private final String searchPlaceholder = "Search...";

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;
        private String action;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();

            button.addActionListener(e -> {
                fireEditingStopped();
                if (action.equals("Edit")) {
                    editTask(currentRow);
                } else if (action.equals("Delete")) {
                    deleteTask(currentRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected,
                                                     int row, int column) {
            currentRow = row;
            action = value.toString();
            button.setText(action);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return action;
        }
    }

    public mainWindow() {
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
                if (selected) {
                    component.setBackground(new Color(0x87CEFA));
                } else {
                    if (row % 2 == 0) {
                        component.setBackground(Color.LIGHT_GRAY);
                    } else {
                        component.setBackground(Color.WHITE);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return component;
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        loadAllTasks();

        searchText.addActionListener(e -> searchTasks());
        Placeholders.addPlaceholder(searchText, searchPlaceholder);
        resetButton.addActionListener(e -> resetSearch());
        suggestButton.addActionListener(e -> showNextTask());
        completeButton.addActionListener(e -> toggleComplete());

        plusButton.addActionListener(e -> {
            CreateOrEdit createEditWindow = new CreateOrEdit(mainWindow.this, null);
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
        refreshTable(taskManager.getTasks());
    }

    public void toggleComplete() {
        int rowNumber = dataTable.getSelectedRow();
        if (rowNumber == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to toggle complete");
            return;
        }
        Task selectedTask = displayedTasks.get(rowNumber);
        taskManager.toggleComplete(selectedTask);
        loadAllTasks();
    }

    public void deleteTask(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= displayedTasks.size()) {
            return;
        }
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

        taskManager.deleteTask(selectedTask);
        loadAllTasks();
    }

    public void editTask(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= displayedTasks.size()) {
            return;
        }
        Task selectedTask = displayedTasks.get(rowNumber);
        CreateOrEdit editWindow = new CreateOrEdit(this, selectedTask);
        editWindow.setVisible(true);
        loadAllTasks();
    }

    private void refreshTable(List<Task> tasks) {
        displayedTasks = tasks;
        String[] columnNames = {"Title", "Description", "Deadline", "Priority", "Completed", "", ""};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };

        for (Task task : tasks) {
            String completed = task.getCompleted() ? "Yes" : "No";
            model.addRow(new Object[]{
                    task.getTitle(), task.getDescription(), task.getDeadline(),
                    task.getPriority(), completed, "Edit", "Delete"
            });
        }

        dataTable.setModel(model);
        dataTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        dataTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        dataTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        dataTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void resetSearch() {
        searchText.setText(searchPlaceholder);
        loadAllTasks();
    }

    private void searchTasks() {
        String inputText = searchText.getText();
        List<Task> filteredTasks = taskManager.searchTasks(inputText);
        refreshTable(filteredTasks);
    }

    private void sortTasks() {
        String sortField = sortOptions.getSelectedItem().toString();
        if (sortField.equals("(none)")) {
            loadAllTasks();
        } else {
            refreshTable(taskManager.sortTasks(sortField));
        }
    }

    private void showNextTask() {
        Task nextTask = taskManager.findNextTask();
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

    private void displayStats () {
        List<Integer> stats = taskManager.getStatistics();
        if (stats.get(0).equals(0)) {
            JOptionPane.showMessageDialog(this, "No tasks have been created yet");
            return;
        }
        JOptionPane.showMessageDialog(this, """
                Total tasks: %d
                High priority: %d
                Medium priority: %s
                Low priority: %d
                """.formatted(stats.get(0), stats.get(1),
                                stats.get(2), stats.get(3)));
    }

    public static void main(String[] args) {
        mainWindow window = new mainWindow();
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
        resetButton = new JButton();
        resetButton.setText("Reset search");
        subPanel.add(resetButton, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        completeButton = new JButton();
        completeButton.setText("Toggle complete");
        subPanel.add(completeButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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