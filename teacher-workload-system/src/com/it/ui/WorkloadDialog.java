package com.it.ui;

import cn.hutool.core.util.IdUtil;
import com.it.pojo.User;
import com.it.pojo.Workload;
import com.it.util.DataUtil;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 雪国小镇 keyfc
 * 项目上传地址 https://github.com/tuanzibigfamily/-teacherSystem
 * 欢迎访问或者添加新功能
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
    private Workload workload;
    private final User currentUser;
    // 组件

    private final JTextField dateField;//日期
    private final JTextField hoursField;// 小时
    private final JComboBox<String> teacherComboBox;//老师
    private final JTextArea descArea;//描述
    private final JTextArea feedbackArea; // 工作反馈输入框

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
        } else {

            for (User user : Objects.requireNonNull(DataUtil.getAllTeachers())) {
                teacherComboBox.addItem(user.getUsername());
            }
            // 如果是编辑模式，则设置默认选中为 workload 中的教师
            if (workload != null && workload.getTeacher() != null) {
                teacherComboBox.setSelectedItem(workload.getTeacher());
            }


            // 管理员允许手动输入
            teacherComboBox.setEditable(true);
        }
        formPanel.add(teacherComboBox, BorderLayout.CENTER);


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


        // 工作描述
        formPanel.add(new JLabel("工作描述:"));
        descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true); // 启用自动换行
        JScrollPane descScrollPane = new JScrollPane(descArea);

        if (workload != null && workload.getDescription() != null) {
            descArea.setText(workload.getDescription());
        }

        formPanel.add(descScrollPane); // ✅ 添加 JScrollPane 而不是 JTextArea
        panel.add(formPanel, BorderLayout.CENTER); // 添加表单，表单居中


        // 工作反馈
        formPanel.add(new JLabel("工作反馈:"));
        feedbackArea = new JTextArea(3, 20);
        feedbackArea.setLineWrap(true); // 启用自动换行
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackArea);

        if (workload != null && workload.getFeedback() != null) {
            feedbackArea.setText(workload.getFeedback());
        }
        formPanel.add(feedbackScrollPane);
        panel.add(formPanel, BorderLayout.CENTER);

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
        // 获取表单数据
        String teacher = (String) teacherComboBox.getSelectedItem(); // ✅ 可能是一个已存在的教师或新名字
        String workDate = dateField.getText().trim();
        float hours;
        try {
            hours = Float.parseFloat(hoursField.getText().trim());
            if (hours <= 0) {
                JOptionPane.showMessageDialog(this, "工作小时必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的工作小时数", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String description = descArea.getText().trim();
        String feedback = feedbackArea.getText().trim();

        // 校验必填字段
        if (teacher.isEmpty() || workDate.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整信息", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  判断是否为管理员，并检查教师是否存在
        if ("admin".equals(currentUser.getRole())) {
            boolean exists = false;
            for (User user : Objects.requireNonNull(DataUtil.getAllUsers())) {
                if (teacher.equals(user.getUsername())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "检测到新教师名 [" + teacher + "]，是否注册新用户？",
                        "新教师确认",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    // 弹出输入框让用户输入密码
                    String password = JOptionPane.showInputDialog(
                            this,
                            "请输入新教师的密码：",
                            "设置密码",
                            JOptionPane.PLAIN_MESSAGE
                    );

                    // 检查密码是否为空
                    if (password == null || password.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 使用用户输入的密码注册新用户
                    User newUser = new User();
                    newUser.setUsername(teacher);
                    newUser.setPassword(password); // 设置为用户输入的密码
                    newUser.setRole("teacher");
                    newUser.setId(IdUtil.simpleUUID());

                    DataUtil.saveUser(newUser);
                    JOptionPane.showMessageDialog(this, "已注册新教师：" + teacher);
                }

            }
        }

        // 构建 workload 对象并保存
        if (workload == null) {
            workload = new Workload();
            workload.setId(IdUtil.simpleUUID());
        }

        workload.setTeacher(teacher);
        workload.setWorkDate(workDate);
        workload.setHours(hours);
        workload.setDescription(description);
        workload.setFeedback(feedback);

        DataUtil.saveWorkload(workload);
        parent.refreshData(); // 刷新主界面表格
        dispose(); // 关闭对话框
    }


}


