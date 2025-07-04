package com.it.ui;

import com.it.pojo.User;
import com.it.pojo.Workload;
import com.it.util.DataUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.it.util.DataUtil.getWorkloadsByTeacherName;
//反馈
/**
 * 雪国小镇 keyfc
 * 项目上传地址 https://github.com/tuanzibigfamily/-teacherSystem
 * 欢迎访问或者添加新功能
 *
/**
 * 主界面
 */
public class MainFrame extends JFrame {
    private final User currentUser;
    private final DefaultTableModel tableModel;
    private final JTable workloadTable;
    private JLabel titleLabel;//时间标签

    JMenuBar menuBar = new JMenuBar();//菜单栏
    JMenu menu = new JMenu("数据处理");
    JMenu menu2 = new JMenu("其他操作");
    JMenu menu3 = new JMenu("当前用户数");
    JMenu menu4 = new JMenu("当前信息条目");
    JMenu menu5 = new JMenu("时间");
    JMenu menu8 = new JMenu("刷新");
    JMenu menu6 = new JMenu("搜索");
    JMenu menu7 = new JMenu("帮助");

    public MainFrame(User user) {
        this.currentUser = user;
        //获取当前角色

        setTitle("教师工作量管理 - 当先用户" + user.getRole() + " - " + user.getUsername());
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //构建工具栏

        JMenuItem item1 = new JMenuItem("添加");
        item1.addActionListener(e -> addWorkload());
        menu.add(item1);

        //添加,刷新功能
        JMenuItem item2 = new JMenuItem("刷新");
        item2.addActionListener(e -> refreshData());
        menu.add(item2);

        JMenuItem item4 = new JMenuItem("打印到文本文档");
        menu.add(item4);
        //打印到文本文档
        item4.addActionListener(e -> exportToTextFile());

        JMenuItem item5 = new JMenuItem("导出为CSV");
        //导出为CSV文件
        item5.addActionListener(e -> exportToCSV());


        // 数据统计
        JMenuItem item6 = new JMenuItem("统计信息");
        item6.addActionListener(e -> showStatistics());
        menu.add(item6);

        // 搜索功能
        JMenuItem item7 = new JMenuItem("搜索");
        item7.addActionListener(e -> performSearch());


        // 帮助文档
        JMenuItem item8 = new JMenuItem("帮助");
        item8.addActionListener(e -> showHelp());


        JMenuItem item3 = new JMenuItem("退出");
        //退出登录
        item3.addActionListener(e -> {
            //销毁当前窗口
            //弹出窗口选择是否要退出
            int result = JOptionPane.showConfirmDialog(this, "是否要退出系统？", "提示", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                //弹出确定退出
                JOptionPane.showMessageDialog(this, user.getRole() + "用户" + user.getUsername() + "退出成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("用户点击了确定");//测试
                this.dispose();//销毁当前窗口
                new LoginFrame();//登录界面
            } else {
                //弹出取消退出
                JOptionPane.showMessageDialog(this, "取消退出！", "提示", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("用户点击了取消");//测试
            }
        });//退出登录


        // 设置图标格式
        setMenuItemIcon(item3, "exit.png");
        menu2.add(item3);
        menu6.add(item7);
        menu2.add(item5);//添加功能
        setMenuItemIcon(item7, "search.png");
        menu2.add(item7);//搜索功能
        setMenuItemIcon(item8, "help.png");
        menu2.add(item8);// 帮助文档

        menu8.add(item2);
        System.out.println("已添加菜单项：" + item2.getText());//测试

        //添加工具栏
        menuBar.add(menu);
        menuBar.add(menu2);
        menuBar.add(menu3);
        menuBar.add(menu4);
        menuBar.add(menu5);
        menuBar.add(menu6);
        menu6.add(item7);
        menuBar.add(menu7);
        menu7.add(item8);
        menuBar.add(menu8);

        this.setJMenuBar(menuBar);

        //todo: 创建数据表格
        tableModel = new DefaultTableModel(new Object[]{"ID", "教师姓名", "日期", "工作时长", "工作量描述", "工作反馈", "操作"}, 0) {
            // 重写isCellEditable方法，使表格不可编辑
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        workloadTable = new JTable(tableModel);
        workloadTable.setRowHeight(25);

        //配置工作负载表格的列渲染器和编辑器，以及设置表格的选择模式。
        TableColumn buttonColumn = workloadTable.getColumnModel().getColumn(6);
        buttonColumn.setCellRenderer(new ButtonPanelRenderer());
        buttonColumn.setCellEditor(new ButtonPanelEditor(workloadTable, this));
        workloadTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(new JScrollPane(workloadTable), BorderLayout.CENTER);

        //加载数据
        refreshData();
    }

    //刷新数据
    public void refreshData() {
        initializeTimeDisplay();
        addTitleLabel(currentUser);
        menu3.setText(GetUserCount(currentUser.getRole()));
        //获取当前用户数
        menu4.setText("当前信息条目数：" + getWorkloadsCount(currentUser.getRole()));
        //获取当前信息数
        //先清空数据
        tableModel.setRowCount(0);
        //获取所有工作负载数据
        //todo: 获取所有数据
        if ("admin".equals(currentUser.getRole())) {
            ArrayList<Workload> list = DataUtil.getAllWorkloads();
            for (Workload workload : list) {
                //添加一行,添加数据,添加到表格模型中
                tableModel.addRow(new Object[]{workload.getId(), workload.getTeacher(),
                        workload.getWorkDate(), workload.getHours(), workload.getDescription(), workload.getFeedback(), ""});
            }
        } else {
            //获取当前用户,获取当前用户所对应的所有数据
            ArrayList<Workload> list = getWorkloadsByTeacherName(currentUser.getUsername());
            for (Workload workload : list) {
                tableModel.addRow(new Object[]{workload.getId(), workload.getTeacher(),
                        workload.getWorkDate(), workload.getHours(), workload.getDescription(), workload.getFeedback(), ""});
            }
        }
    }

    //获取当前工作量数
    public String getWorkloadsCount(String role) {
        //如果角色是管理员，则显示所有教师工作量数
        if (role.equals("admin")) {
            return DataUtil.getAllWorkloads().size() + " |";
        }
        return Objects.requireNonNull(getWorkloadsByTeacherName(currentUser.getUsername())).size() + " |";
    }

    //初始化时间标签
    private void initializeTimeDisplay() {
        // 启动定时器更新时间
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTimeLabel();
            }
        }, 0, 1000); // 每隔一秒更新一次
    }

    /**
     * 更新时间标签的方法
     */
    private void updateTimeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEEE");
        String currentDate = sdf.format(new Date());
        // 替换星期几为指定格式
        String[] weekDays = {"Mon-星期一", "Tue-星期二", "Wed-星期三", "Thu-星期四", "Fri-星期五", "Sat-星期六", "Sun-星期日"};
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String weekDay = weekDays[(dayOfWeek + 5) % 7]; // 调整索引以匹配星期几
        String formattedDate = currentDate.split(" ")[0] + " " + currentDate.split(" ")[1] + "  " + weekDay + " |";
        menu5.setText(formattedDate);
        //设置文本格式
        menu5.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 12));
    }

    /**
     * 添加标题标签
     *
     */
    private void addTitleLabel(User user) {
        if (titleLabel != null) {
            this.remove(titleLabel); // 先移除旧的标题
        }
        if ("admin".equals(currentUser.getRole()) && "admin".equals(user.getRole())) {
            titleLabel = new JLabel("所有教师工作量  " + getTotalAndAverageHours());
        } else {
            titleLabel = new JLabel("我的工作量  " + getTotalAndAverageHours());
        }
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        this.add(titleLabel, BorderLayout.NORTH);
    }

    //获取用户数
    private String GetUserCount(String role) {
        //如果角色是管理员，则显示所有用户数
        if (role.equals("admin")) {
            //获取的用户名不能相同，相同只计算一次
            ArrayList<String> userNames = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(DataUtil.getAllUsers()).size(); i++) {
                User user = DataUtil.getAllUsers().get(i);
                if (!userNames.contains(user.getUsername())) {
                    userNames.add(user.getUsername());
                }
            }
            return "系统用户数：" + userNames.size();
        }
        //不是管理员，则显示当前用户数为1
        return "在线用户数：1";
    }

    /**
     * 刷新工作量表格数据
     */

    //  添加工作量
    private void addWorkload() {
        WorkloadDialog dialog = new WorkloadDialog(this, null, currentUser);
        dialog.setVisible(true);
        //todo: 构建并弹出添加弹出框，完成添加功能
    }


    // todo: 修改工作量
    public void editWorkload(int editedRow) {
        if (editedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要修改的记录");
            return;
        }
        String workloadId = (String) tableModel.getValueAt(editedRow, 0);
        Workload workload = DataUtil.getWorkloadById(workloadId);
        JOptionPane.showMessageDialog(this, "您正在操作教师 [" + workload.getTeacher() + "] 的工作量数据");


        WorkloadDialog dialog = new WorkloadDialog(this, workload, currentUser);
        dialog.setVisible(true);

    }

    // todo: 删除工作量

    public void deleteWorkload(int Row) {
        if (Row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要删除的行");
            return;
        }
        String workloadId = (String) tableModel.getValueAt(Row, 0);
        DataUtil.deleteWorkload(workloadId);

        JOptionPane.showMessageDialog(this, "删除成功！");
        refreshData();
    }


    /**
     * 导出数据到文本文件
     */
    private void exportToTextFile() {
        // 获取当前用户的工作量数据
        ArrayList<Workload> workloads;
        if ("admin".equals(currentUser.getRole())) {
            workloads = DataUtil.getAllWorkloads();
        } else {
            workloads = getWorkloadsByTeacherName(currentUser.getUsername());
        }

        // 构建要写入的内容
        StringBuilder content = new StringBuilder();
        content.append("教师工作量记录\n\n");
        for (Workload workload : workloads) {
            content.append("教师姓名：").append(workload.getTeacher()).append("\n");
            content.append("日期：").append(workload.getWorkDate()).append("\n");
            content.append("工作时长：").append(workload.getHours()).append("小时\n");
            content.append("工作量描述：").append(workload.getDescription()).append("\n");
            content.append("教师工作反馈：").append(workload.getFeedback()).append("\n\n");
        }

        // 弹出文件选择对话框
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("请选择保存路径");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(this);
//如果有重复文件，就另存一份
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();

            String filePath;
            if ("admin".equals(currentUser.getRole())) {
                filePath = selectedDirectory.getAbsolutePath() + "/全体教师工作量记录.txt";
            } else {
                filePath = selectedDirectory.getAbsolutePath() + "/教师" + currentUser.getUsername() + "工作量记录.txt";
            }
            File file = new File(filePath);

            // 如果文件已存在，创建带序号的副本
            int copyNumber = 1;
            while (file.exists()) {
                String baseName;
                String extension;
                String directoryPath = selectedDirectory.getAbsolutePath();

                int dotIndex = file.getName().lastIndexOf(".");
                if (dotIndex > 0) {
                    baseName = file.getName().substring(0, dotIndex);
                    extension = file.getName().substring(dotIndex);
                } else {
                    baseName = file.getName();
                    extension = "";
                }

                if ("admin".equals(currentUser.getRole())) {
                    filePath = directoryPath + "/" + baseName + "_副本" + copyNumber + extension;
                } else {
                    filePath = directoryPath + "/教师" + currentUser.getUsername() + baseName.substring(2) + "_副本" + copyNumber + extension;
                }
                file = new File(filePath);
                copyNumber++;
            }

            try {
                file.createNewFile();
                Files.write(file.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * 导出数据到CSV文件
     */
    private void exportToCSV() {
        // 获取当前用户的工作量数据
        ArrayList<Workload> workloads;
        if ("admin".equals(currentUser.getRole())) {
            workloads = DataUtil.getAllWorkloads();
        } else {
            workloads = getWorkloadsByTeacherName(currentUser.getUsername());
        }

        // 构建CSV内容
        StringBuilder csvContent = new StringBuilder();
        // CSV表头
        csvContent.append("教师姓名,日期,工作时长（小时）,工作量描述,工作反馈\n");

        // 填充数据
        for (Workload workload : workloads) {
            csvContent.append(workload.getTeacher()).append(",")
                    .append(workload.getWorkDate()).append(",")
                    .append(workload.getHours()).append(",")
                    .append(workload.getDescription()).append(",")
                    .append(workload.getFeedback()).append("\n");
        }

        // 弹出文件选择对话框
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("请选择保存路径");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();

            String filePath;
            if ("admin".equals(currentUser.getRole())) {
                filePath = selectedDirectory.getAbsolutePath() + "/全体教师工作量记录.csv";
            } else {
                filePath = selectedDirectory.getAbsolutePath() + "/教师" + currentUser.getUsername() + "工作量记录.csv";
            }

            File file = new File(filePath);
            // 如果文件已存在，创建带序号的副本
            int copyNumber = 1;
            while (file.exists()) {
                String baseName;
                String extension;
                String directoryPath = selectedDirectory.getAbsolutePath();

                int dotIndex = file.getName().lastIndexOf(".");
                if (dotIndex > 0) {
                    baseName = file.getName().substring(0, dotIndex);
                    extension = file.getName().substring(dotIndex);
                } else {
                    baseName = file.getName();
                    extension = "";
                }

                if ("admin".equals(currentUser.getRole())) {
                    filePath = directoryPath + "/" + baseName + "_副本" + copyNumber + extension;
                } else {
                    filePath = directoryPath + "/教师" + currentUser.getUsername() + baseName.substring(2) + "_副本" + copyNumber + extension;
                }
                file = new File(filePath);
                copyNumber++;
            }

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "创建文件失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            // 写入CSV文件
            try (FileWriter writer = new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(new String("\uFEFF".getBytes(java.nio.charset.StandardCharsets.UTF_8))); // 添加UTF-8 BOM头，确保Excel识别为UTF-8编码
                writer.write(csvContent.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            // 打开文件
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "打开文件失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showStatistics() {
        ArrayList<Workload> workloads;
        if ("admin".equals(currentUser.getRole())) {
            workloads = DataUtil.getAllWorkloads();
        } else {
            workloads = getWorkloadsByTeacherName(currentUser.getUsername());
        }

        double totalHours = 0;
        for (Workload workload : workloads) {
            totalHours += workload.getHours();
        }
        double averageHours = workloads.size() == 0 ? 0 : totalHours / workloads.size();
        // 使用 String.format 保留两位小数
        String message = "总工作时长：" + totalHours + " 小时\n" + "平均工作时长：" + String.format("%.2f", averageHours) + " 小时\n" + "条目数：" + workloads.size();
        JOptionPane.showMessageDialog(this, message, "统计信息", JOptionPane.INFORMATION_MESSAGE);
    }

    // 显示帮助文档
    private void showHelp() {
        // 创建临时文本文件并写入使用说明
        try {
            File tempFile = File.createTempFile("帮助文档", ".txt");
            tempFile.deleteOnExit();

            String helpContent = "教师工作量管理系统使用说明:\n\n" +
                    "1. 添加: 点击添加按钮，输入教师工作量信息。\n" +
                    "2. 刷新: 更新表格中的数据。\n" +
                    "3. 打印到文本文档: 将当前数据显示为文本文件。\n" +
                    "4. 导出为CSV: 将当前数据导出为CSV文件。\n" +
                    "5. 统计信息: 显示当前查询条件下教师的工作量总时长和平均时长。\n" +
                    "6. 搜索: 输入关键词（教师姓名、日期或描述）来快速查找特定的记录。\n" +
                    "7. 帮助: 查看本使用说明。\n\n" +
                    "注意事项:\n" +
                    "- 非管理员用户只能查看和搜索自己的数据。\n" +
                    "- 非管理员用户无法编辑其他用户的记录。\n" +
                    "- 所有操作请确保在联网环境下进行，以便数据同步。";

            Files.write(tempFile.toPath(), helpContent.getBytes());

            // 打开临时文件
            Desktop.getDesktop().open(tempFile);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "无法创建帮助文档：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // 搜索功能
    public void performSearch() {
        // 弹出搜索框，让用户选择搜索类型（教师或详细内容）
        String[] options = {"教师姓名", "工作量描述"};
        int choice = JOptionPane.showOptionDialog(this, "请选择搜索类型：", "搜索选项", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == -1) {
            System.out.println("用户取消操作");  // 用户取消操作
            return; // 用户取消操作
        }

        String keyword = JOptionPane.showInputDialog(this, "请输入搜索关键词：", "自定义搜索", JOptionPane.INFORMATION_MESSAGE);
        if (keyword == null || keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "搜索关键词不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 从表格获取数据并直接匹配
        tableModel.setRowCount(0); // 清空表格当前所有行

        // 根据用户选择的搜索类型确定搜索的列：教师姓名对应第1列，工作量描述对应第4列
        int searchColumn = (choice == 0) ? 1 : 4; // choice为0表示选择的是"教师姓名"，否则为"工作量描述"

        // 非管理员只能搜索自己的数据
        if (!currentUser.getRole().equals("admin")) {
            // 遍历当前用户的工作量数据
            for (Workload workload : Objects.requireNonNull(getWorkloadsByTeacherName(currentUser.getUsername()))) {
                searchData(keyword, searchColumn, workload);
            }
        } else {
            // 管理员可以搜索所有数据
            for (Workload workload : DataUtil.getAllWorkloads()) {
                searchData(keyword, searchColumn, workload);
            }
        }

        // 如果没有找到任何结果，提示用户
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "未找到匹配的结果，请尝试其他关键词或联系管理员。", "提示", JOptionPane.INFORMATION_MESSAGE);
            //刷新数据
            refreshData();
        }
    }

    // 搜索数据
    private void searchData(String keyword, int searchColumn, Workload workload) {
        String value = (searchColumn == 1) ? workload.getTeacher() : workload.getDescription();
        if (value != null && !value.isEmpty() && value.contains(keyword)) {
            tableModel.addRow(new Object[]{
                    workload.getId(),
                    workload.getTeacher(),
                    workload.getWorkDate(),
                    workload.getHours(),
                    workload.getDescription(),
                    ""
            });
        }
    }

    //返回总时长和平均时长的字符串
    private String getTotalAndAverageHours() {
        ArrayList<Workload> workloads;
        if ("admin".equals(currentUser.getRole())) {
            workloads = DataUtil.getAllWorkloads();
        } else {
            workloads = getWorkloadsByTeacherName(currentUser.getUsername());
        }
        double totalHours = 0;
        for (Workload workload : workloads) {
            totalHours += workload.getHours();
        }
        double averageHours = workloads.size() == 0 ? 0 : totalHours / workloads.size();
        return "总时长：" + totalHours + " 小时\n" + "  平均时长：" + String.format("%.2f", averageHours) + " 小时";
    }


    /**
     * 设置菜单项的图标
     *
     * @param menuItem 菜单项
     * @param iconName 图标文件名（位于项目的 "image" 文件夹下）
     */
    public static void setMenuItemIcon(JMenuItem menuItem, String iconName) {
        // 构建图标文件的路径
        String imagePath = "O:\\teacher-workload-system\\image\\" + iconName;

        // 创建 ImageIcon 并设置图标大小
        ImageIcon icon = new ImageIcon(imagePath);
        icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT));

        // 设置菜单项的图标
        menuItem.setIcon(icon);
    }
}