package ui.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ActionRenderer extends JPanel implements TableCellRenderer {
    public ActionRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
        add(new JButton("Edit"));
        add(new JButton("Delete"));
        add(new JButton("Complete"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        return this;
    }
}