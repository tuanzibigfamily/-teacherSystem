package com.it.ui;

import com.it.pojo.User;
import com.it.pojo.Workload;
import com.it.util.DataUtil;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 雪国小镇 keyfc
 *
 * @author KeyFansClub
 * @packageName com.it.ui
 * @data 2025/6/30 22:17:45
 * @user 团子大家族 Key Fan Club
 * @data 公元2025年 6月30日 周一
 */
/*
 */
public class WorkloadDialog extends JDialog {
    private boolean saved = false;
    private Workload workload;
    private User currentUser;
    // 组件

    private JTextField dateField;//日期
    private JTextField hoursField;// 小时
    private JComboBox<String> teacherComboBox;//老师
    private JTextArea descArea;//描述
    private JTextArea feedbackArea;//反馈

    MainFrame parent;//父窗口


    public WorkloadDialog(JFrame parent, Workload workload, User currentUser) {
        super(parent, workload == null ? "添加工作量" : "编辑工作量", true);
        this.parent = (MainFrame) parent;
        this.currentUser = currentUser;
        this.workload = workload == null ? new Workload() : workload;
        setSize(600, 600);
        setLocationRelativeTo(parent);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        //表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 修改为0行2列的网格布局
        formPanel.add(new JLabel("教师名称:"));
        teacherComboBox = new JComboBox<>();
        if (!currentUser.getUsername().equals("admin")) {
            teacherComboBox.addItem(currentUser.getUsername()); // 非管理员只能看到自己的名字
            teacherComboBox.setSelectedItem(currentUser.getUsername()); // 设置当前用户
            teacherComboBox.setEditable(false); // 禁止修改
            formPanel.add(teacherComboBox);
        } else {
            for (User user : DataUtil.getAllTeachers()) {
                teacherComboBox.addItem(user.getUsername());
            }
            // 管理员允许手动输入
            teacherComboBox.setEditable(true);
            formPanel.add(teacherComboBox);
        }


        //工作日期
        //设置工作日期的文本显示位置
        formPanel.add(new JLabel("工作日期(年份-月份-日期):"));
        dateField = new JTextField(10);//创建文本框
        if (workload != null && workload.getWorkDate() != null)//如果是修改工作项,则将工作项的日期显示在文本框中
        {
            dateField.setText(workload.getWorkDate());//显示工作项的日期
        } else {
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//显示当前日期
        }
        formPanel.add(dateField);


        //工作时间
        formPanel.add(new JLabel("工作小时:"));
        hoursField = new JTextField(10);//创建文本框
        if (workload != null && workload.getHours() != 0)
            hoursField.setText(String.valueOf(workload.getHours()));
        formPanel.add(hoursField);


        //工作描述
        formPanel.add(new JLabel("工作描述:"));
        descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true); // 启用自动换行
        JScrollPane descScrollPane = new JScrollPane(descArea);
        if (workload != null && workload.getDescription() != null)
            descArea.setText(workload.getDescription());
        formPanel.add(descScrollPane);


        // 工作反馈
        formPanel.add(new JLabel("工作反馈:"));
        feedbackArea = new JTextArea(3, 20);
        feedbackArea.setLineWrap(true); // 启用自动换行
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackArea);
        if (workload != null && workload.getFeedback() != null)
            feedbackArea.setText(workload.getFeedback());
        formPanel.add(feedbackScrollPane);


        panel.add(formPanel, BorderLayout.CENTER);//添加表单,表单居中

        //按钮面板
        JPanel buttonPanel = new JPanel();
        JButton saveBth = new JButton("保存");
        saveBth.addActionListener(e -> saveWorkload());//保存按钮点击事件,销毁当前窗口
        JButton cancelBth = new JButton("取消");
        cancelBth.addActionListener(e -> dispose());//取消按钮点击事件,销毁当前窗口
        buttonPanel.add(saveBth);
        buttonPanel.add(cancelBth);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);//添加面板
    }


    private void saveWorkload() {
        try {
            String workDate = dateField.getText();
            float hours = Float.parseFloat(hoursField.getText());
            String description = descArea.getText();
            String teacher = (String) teacherComboBox.getSelectedItem();// 获取教师名称
            workload.setTeacher(teacher);
            workload.setWorkDate(workDate);
            workload.setHours(hours);
            workload.setDescription(description);
            // 设置并保存工作反馈信息
            String feedback = feedbackArea.getText();
            workload.setFeedback(feedback);
            DataUtil.saveWorkload(workload);
            saved = true;
            parent.refreshData();//刷新数据
            dispose();
        } catch (Exception e) {
            e.printStackTrace();//输出异常信息
            JOptionPane.showMessageDialog(this, "保存失败" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


}


