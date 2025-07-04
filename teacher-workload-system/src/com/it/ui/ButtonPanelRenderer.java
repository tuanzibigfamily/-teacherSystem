package com.it.ui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * 自定义按钮面板渲染器
 */
class ButtonPanelRenderer implements TableCellRenderer {
    private JPanel panel;
    private JButton editBtn;// 修改按钮
    private JButton deleteBtn;//  删除按钮

    /**
     * 构造方法
     */
    public ButtonPanelRenderer() {
        //  1. 创建面板
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        // 2. 创建编辑和删除按钮
        editBtn = new JButton("修改");
        deleteBtn = new JButton("删除");
        // 3. 设置按钮样式
        editBtn.setMargin(new Insets(0, 5, 0, 5));
        deleteBtn.setMargin(new Insets(0, 5, 0, 5));
        // 4. 将按钮添加到面板中
        panel.add(editBtn);
        panel.add(deleteBtn);
    }

    /**
     * 返回一个包含两个按钮的面板
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return panel;
    }
}

// 自定义按钮面板编辑器
class ButtonPanelEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton editBtn;
    private JButton deleteBtn;
    public int editedRow = 0;

    public ButtonPanelEditor(JTable table, MainFrame frame) {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        editBtn = new JButton("修改");
        deleteBtn = new JButton("删除");

        // 获取当前数据行数量
        int rowCount = table.getRowCount();
        // 设置初始编辑行为-1（无效值）
        editedRow = -1;

        // 设置按钮样式
        editBtn.setMargin(new Insets(0, 5, 0, 5));
        deleteBtn.setMargin(new Insets(0, 5, 0, 5));

        //添加按钮事件
        editBtn.addActionListener(e -> {
            fireEditingStopped();// 通知编辑操作已经停止
            frame.editWorkload(editedRow);
        });

        // 删除按钮事件
        deleteBtn.addActionListener(e -> {
            // 数据高危操作，二次确认
            // 提示, 确认删除,
            fireEditingStopped();
            // 通知所有监听器，编辑已经停止，单元格编辑器可以停止编辑并提交更改
            fireEditingStopped();
            // 通常用于通知某个编辑操作已经停止，常用于自定义的单元格编辑器或者组件中，以触发相关的事件处理逻辑。
            //少一个
            int confirm = JOptionPane.showConfirmDialog(table, "确定要删除吗？", "提示", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 检查 editedRow 是否有效
                if (editedRow < 0 || editedRow >= table.getRowCount()) {
                    JOptionPane.showMessageDialog(table, "请选择有效的行");
                    return;
                }
                // 根据行索引获取id，删除数据
                frame.deleteWorkload(editedRow);
                // 重新加载数据并更新 UI
                frame.refreshData();
            }

        });
        panel.add(editBtn);
        panel.add(deleteBtn);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        editedRow = row;
        return panel;
    }

    /**
     * 获取工作量数据的行数
     *
     * @return 返回当前工作量数据的行数
     */

    @Override
    public Object getCellEditorValue() {
        return ""; // 返回值不重要，因为我们直接处理按钮事件
    }


}