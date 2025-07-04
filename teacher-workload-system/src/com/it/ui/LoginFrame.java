package com.it.ui;

import com.it.pojo.User;
import com.it.util.DataUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 雪国小镇 keyfc
 * 项目上传地址 https://github.com/tuanzibigfamily/-teacherSystem
 * 欢迎访问或者添加新功能
 */
public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton showPasswordButton;
    // 定义图标
    private final ImageIcon iconShow = new ImageIcon("O:\\teacher-workload-system\\image\\显示密码.png");  // 显示密码图标路径
    Image image_IconShow = iconShow.getImage(); // 转换为 Image 对象
    Image scaledImage_IconShow = image_IconShow.getScaledInstance(30, 29, Image.SCALE_SMOOTH); // 缩放至 20x20 像素

    private final ImageIcon iconHide = new ImageIcon("O:\\teacher-workload-system\\image\\隐藏密码.png"); // 隐藏密码图标路径
    Image getImage_IconHide = iconHide.getImage(); // 转换为 Image 对象
    Image scaledImage_IconHide = getImage_IconHide.getScaledInstance(30, 29, Image.SCALE_SMOOTH); // 缩放至 20x20 像素

    // 控制当前是否显示密码
    private boolean isPasswordVisible = false;

    public LoginFrame() {
        this.setTitle("教师工作量管理系统－登录");  // 设置窗体的标题
        this.setSize(400, 300);  // 设置窗体的大小
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置窗体关闭方式
        this.setLocationRelativeTo(null);  // 设置窗口居中显示

        // 设置背景图片
        this.setContentPane(new BackgroundPanel());


        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // 用户名标签和输入框
        JLabel usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        usernameLabel.setForeground(Color.black);
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.BOLD, 18)); // 字号调整为20

        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        passwordLabel.setForeground(Color.black);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.BOLD, 18)); // 字号调整为20

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JButton registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 显示密码按钮
        showPasswordButton = new JButton();
        showPasswordButton.setFocusable(false);
        showPasswordButton.addActionListener(e -> togglePasswordVisibility());
        // 设置按钮格式
        showPasswordButton.setIcon(new ImageIcon(scaledImage_IconHide));
        showPasswordButton.setOpaque(true);
        showPasswordButton.setBackground(new Color(230, 230, 230)); // 背景色
        showPasswordButton.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 边框
        showPasswordButton.setPreferredSize(new Dimension(10, 15)); // 设置大小

        // 设置布局管理器为 null（绝对布局）
        this.setLayout(null);

        // 设置组件的位置和大小
        usernameLabel.setBounds(60, 130, 80, 30);
        usernameField.setBounds(140, 130, 160, 30);

        passwordLabel.setBounds(60, 180, 80, 30);
        passwordField.setBounds(140, 180, 160, 30);

        showPasswordButton.setBounds(300, 180, 50, 29);

        loginButton.setBounds(140, 220, 70, 30);
        registerButton.setBounds(230, 220, 70, 30);


        passwordField.setEchoChar('*'); // 设置掩码字符为 *

        // 添加组件到窗口
        this.add(usernameLabel);
        this.add(usernameField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(showPasswordButton);
        this.add(loginButton);
        this.add(registerButton);

        // 按 "Enter" 键切换到下一个输入框
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> login());

        // 登录按钮事件
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegisterWindow());
        // 显示窗口
        this.setVisible(true);
    }


    // 切换密码的显示状态
    // 切换密码可见性方法
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;


        if (isPasswordVisible) {
            // 显示密码，并切换为闭眼图标
            passwordField.setEchoChar((char) 0); // 取消隐藏字符
            showPasswordButton.setIcon(new ImageIcon(scaledImage_IconShow));

            // 2秒后恢复密码格式
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    passwordField.setEchoChar('*');  // 恢复格式
                    isPasswordVisible = false;  // 同步状态
                    showPasswordButton.setIcon(new ImageIcon(scaledImage_IconHide)); // 恢复图标
                    // 将准星（输入焦点）拉回密码框
                    SwingUtilities.invokeLater(() -> passwordField.requestFocusInWindow());

                }
            }, 2000);
        } else {
            // 隐藏密码，并切换为开眼图标
            passwordField.setEchoChar('*'); // 设置为星号
            passwordField.setFont(new Font("微软雅黑", Font.BOLD, 18));
            showPasswordButton.setIcon(iconShow);
        }
    }


    //打开注册窗口
    private void openRegisterWindow() {
        JFrame registerFrame = new JFrame("注册");
        registerFrame.setSize(400, 300);
        registerFrame.setLocationRelativeTo(this);  // 使注册窗口相对于当前窗口居中
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 创建注册窗口组件
        JLabel registerUsernameLabel = new JLabel("用户名：");
        registerUsernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JTextField registerUsernameField = new JTextField(20);
        registerUsernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel registerPasswordLabel = new JLabel("密码：");
        registerPasswordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JPasswordField registerPasswordField = new JPasswordField(20);
        registerPasswordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel registerConfirmPasswordLabel = new JLabel("确认密码：");
        registerConfirmPasswordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JPasswordField registerConfirmPasswordField = new JPasswordField(20);
        registerConfirmPasswordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JButton registerSubmitButton = new JButton("提交");
        registerSubmitButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 设置注册窗口的布局
        registerFrame.setLayout(null);

        // 设置注册组件的位置和大小
        registerUsernameLabel.setBounds(80, 50, 80, 30);
        registerUsernameField.setBounds(160, 50, 160, 30);

        registerPasswordLabel.setBounds(80, 100, 80, 30);
        registerPasswordField.setBounds(160, 100, 160, 30);

        registerConfirmPasswordLabel.setBounds(80, 150, 100, 30);
        registerConfirmPasswordField.setBounds(160, 150, 160, 30);

        registerSubmitButton.setBounds(160, 200, 100, 30);

        // 添加组件到注册窗口
        registerFrame.add(registerUsernameLabel);
        registerFrame.add(registerUsernameField);
        registerFrame.add(registerPasswordLabel);
        registerFrame.add(registerPasswordField);
        registerFrame.add(registerConfirmPasswordLabel);
        registerFrame.add(registerConfirmPasswordField);
        registerFrame.add(registerSubmitButton);

        // 注册按钮点击事件
        registerSubmitButton.addActionListener(e -> {
            String username = registerUsernameField.getText().trim();
            String password = new String(registerPasswordField.getPassword()).trim();
            String confirmPassword = new String(registerConfirmPasswordField.getPassword()).trim();

            // 检查注册信息完整性和匹配性
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "请填写完整的注册信息！", "提示", JOptionPane.WARNING_MESSAGE);
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerFrame, "密码和确认密码不匹配！", "提示", JOptionPane.WARNING_MESSAGE);
            }

            //校验用户名是否存在
            if (username.trim().equals("") || password.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "用户名或密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 检查用户名是否存在
            if (DataUtil.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "用户名已存在！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            //不存在，将用户信息保存到数据库中
            // 创建一个User对象来存储用户信息
            User user = new User();

            // 设置用户的用户名
            user.setUsername(username);
            // 设置用户的密码
            user.setPassword(password);
            // 使用当前时间作为用户ID，确保ID的唯一性
            user.setId(System.currentTimeMillis() + "");
            // 设置用户的角色为教师
            user.setRole("teacher");
            // 调用DataUtil的saveUser方法保存用户信息
            DataUtil.saveUser(user);
            // 显示注册成功的信息对话框
            JOptionPane.showMessageDialog(this, "注册成功！请登录！", "提示", JOptionPane.INFORMATION_MESSAGE);
            registerFrame.dispose();  // 关闭注册窗口
        });
        registerFrame.setVisible(true);
    }

    // 背景图片面板
    private static class BackgroundPanel extends JPanel {
        private final ImageIcon backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("O:\\teacher-workload-system\\image\\登gss录.jpg"); // 这里的"background.jpg"是你的背景图片路径
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
        }
    }


    //登录
    private void login() {
        //拿到用户名

        // 获取用户名输入框中的文本
        String username = usernameField.getText().trim();
        // 将密码输入框中的字符数组转换为字符串
        String password = new String(passwordField.getPassword()).trim();
        // 显示登录成功对话框，包含用户名
        // 显示登录成功消息

        // 根据用户名获取用户对象
        User user = DataUtil.getUserByUsername(username);
        // 检查用户是否存在如果用户不存在，则显示错误消息并返回

        if (user == null) {
            //
            JOptionPane.showMessageDialog(this, "用户名不存在");
            // 清空用户名输入框
            usernameField.setText("");
            // 清空密码输入框
            passwordField.setText("");
            // 将准星（输入焦点）拉回用户名框
            SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
        } else if (!user.getPassword().equals(password)) {
            // 如果密码错误，则显示错误消息并返回
            JOptionPane.showMessageDialog(this, "密码错误");
            // 清空密码输入框
            passwordField.setText("");
            // 将准星（输入焦点）拉回密码框
            SwingUtilities.invokeLater(() -> passwordField.requestFocusInWindow());
        } else {
            JOptionPane.showMessageDialog(this, "用户名" + username + "登录成功！");
            //关闭登录窗口
            LoginFrame.this.dispose();
            // 创建主框架并显示
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
        }

    }

}

