
package com.it.util;
import java.io.*;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.it.pojo.User;
import com.it.pojo.Workload;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {
    public static final String usersFilePath="O:\\Shixi_Shixi_Shixi\\teacher-workload-system\\lib\\users.txt";
    public static final String workloadsFilePath="O:\\Shixi_Shixi_Shixi\\teacher-workload-system\\lib\\workloads.txt";



    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */

    public static User getUserByUsername(String username) {
        //判断users.txt是否存在
        if (!FileUtil.exist(usersFilePath)) {
            System.out.println("users文件不存在");
            return null;
        }else {
            List<String> usersStrs = FileUtil.readUtf8Lines(usersFilePath);
            ArrayList<User> users = userList2BeanList(usersStrs);
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if (username.equals(user.getUsername())) {
                    return user;
                }
            }
        }

        return null;
    }



    /**
     * 保存用户
     * @param user
     */

    public static void saveUser(User user) {
        //判断users.txt是否存在
        if (!FileUtil.exist(usersFilePath)) {
            System.out.println("users文件不存在,新增文件");
            ArrayList<User> users = new ArrayList<User>();
            users.add(user);
            FileUtil.writeUtf8Lines(userList2StringList(users), usersFilePath);
        }else {
            List<String> usersStrs = FileUtil.readUtf8Lines(usersFilePath);
            ArrayList<User> users = userList2BeanList(usersStrs);

            User userByUsername = getUserByUsername(user.getUsername());
            if (userByUsername != null) {
                System.out.println("用户已存在,不能插入");
                return;
            }

            users.add(user);
            FileUtil.writeUtf8Lines(userList2StringList(users), usersFilePath);
        }
    }

/**
     * 将账户ArrayList<User>---->ArrayList<String>
     * @param users
     * @return
     */

    public static ArrayList<String> userList2StringList(ArrayList<User> users) {
        //1. 创建一个ArrayList<String>容器
        ArrayList<String> arrayList = new ArrayList<String>();
        //2. 遍历users，蒋对象变成字符串
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            //格式如下：ID#用户名#密码#角色
            // 61111111#admin#123456#teacher#
            String userStr = user.getId() + "#"
                    + user.getUsername() + "#"
                    + user.getPassword() + "#"
                    + user.getRole();
            arrayList.add(userStr);
        }
        return arrayList;
    }



    //获取所有的用户列表
    public static ArrayList<User> getAllUsers() {
        //判断users.txt是否存在
        if (!FileUtil.exist(usersFilePath)) {
            System.out.println("users文件不存在");
            return null;
        }else {
            List<String> usersStrs = FileUtil.readUtf8Lines(usersFilePath);
            ArrayList<User> users = userList2BeanList(usersStrs);
            return users;
        }
    }

    /**
     * 获取所有的老师列表
     * @return
     */
    public static ArrayList<User> getAllTeachers() {
        //判断users.txt是否存在
        if (!FileUtil.exist(usersFilePath)) {
            System.out.println("users文件不存在");
            return null;
        }else {
            List<String> usersStrs = FileUtil.readUtf8Lines(usersFilePath);
            ArrayList<User> users = userList2BeanList(usersStrs);
            ArrayList<User> teachers = new ArrayList<User>();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if ("teacher".equals(user.getRole())) {
                    teachers.add(user);
                }
            }
            return teachers;
        }
    }

    /**
     * 将ArrayList<String>---->ArrayList<User>
     *
     * @param usersStrs
     * @return
     */
    public static ArrayList<User> userList2BeanList(List<String> usersStrs) {
        //1. 创建一个ArrayList<User>容器
        ArrayList<User> users = new ArrayList<User>();

        //2. 遍历accounts，蒋对象变成字符串

        for (int i = 0; i < usersStrs.size(); i++) {
            String s = usersStrs.get(i); // 61111111#admin#123456#teacher
            if (s != null && s.length() > 0 && s.contains("#")) {
                //使用#进行切割
                String[] split = s.split("#");
                User user = new User(split[0], split[1], split[2], split[3]);
                users.add(user);

            }
        }
        return users;
    }




    /**
     * 保存工作量到文件
     * @param workload
     */
    public static void saveWorkload(Workload workload) {
        //判断workloads.txt是否存在
        if (!FileUtil.exist(workloadsFilePath)) {
            System.out.println("workloads文件不存在,新增文件");
            ArrayList<Workload> workloads = new ArrayList<Workload>();
            workloads.add(workload);
            FileUtil.writeUtf8Lines(workloadList2StringList(workloads), workloadsFilePath);
        }else {

            if (workload.getId() == null){
                workload.setId(IdUtil.simpleUUID());
                List<String> workloadStrs = FileUtil.readUtf8Lines(workloadsFilePath);
                ArrayList<Workload> workloads = workloadList2BeanList(workloadStrs);
                workloads.add(workload);
                FileUtil.writeUtf8Lines(workloadList2StringList(workloads), workloadsFilePath);
            }else {
               editWorkload(workload);
            }

        }

    }

    //通过用户名获取工作量列表
    public static ArrayList<Workload> getWorkloadsByTeacherName(String teacherName) {
        if (!FileUtil.exist(workloadsFilePath)) {
            System.out.println("workloads文件不存在");
            return null;
        }else {
            //居中显示
            List<String> workloadStrs = FileUtil.readUtf8Lines(workloadsFilePath);
            ArrayList<Workload> workloads = workloadList2BeanList(workloadStrs);
            ArrayList<Workload> workloadsByTeacherName = new ArrayList<Workload>();
            for (int i = 0; i < workloads.size(); i++) {
                Workload workload = workloads.get(i);
                if (workload.getTeacher().equals(teacherName)) {
                    workloadsByTeacherName.add(workload);
                }
            }
            return workloadsByTeacherName;
        }
    }

    //获取所有的工作量列表
    public static ArrayList<Workload> getAllWorkloads() {
        //判断workloads.txt是否存在
        if (!FileUtil.exist(workloadsFilePath)) {
            System.out.println("workloads文件不存在");
            ArrayList<Workload> workloads = new ArrayList<Workload>();
            return workloads;
        }else {
            List<String> workloadStrs = FileUtil.readUtf8Lines(workloadsFilePath);
            ArrayList<Workload> workloads = workloadList2BeanList(workloadStrs);
            return workloads;
        }
    }

    //通过workloadId删除工作量
    public static void deleteWorkload(String workloadId) {
        if (!FileUtil.exist(workloadsFilePath)) {
            System.out.println("workloads文件不存在");
            return;
        }else {
            List<String> workloadStrs = FileUtil.readUtf8Lines(workloadsFilePath);
            ArrayList<Workload> workloads = workloadList2BeanList(workloadStrs);
            for (int i = 0; i < workloads.size(); i++) {
                Workload workload = workloads.get(i);
                if (workload.getId().equals(workloadId)) {
                    workloads.remove(i);
                    FileUtil.writeUtf8Lines(workloadList2StringList(workloads), workloadsFilePath);
                }
            }
        }
    }

  //通过workid获取工作量详情
    public static Workload getWorkloadById(String workloadId) {
        if (!FileUtil.exist(workloadsFilePath)) {
            System.out.println("workloads文件不存在");
            return null;
        }else {
            List<String> workloadStrs = FileUtil.readUtf8Lines(workloadsFilePath);
            ArrayList<Workload> workloads = workloadList2BeanList(workloadStrs);
            for (int i = 0; i < workloads.size(); i++) {
                Workload workload = workloads.get(i);
                if (workload.getId().equals(workloadId)) {
                    return workload;
                }
            }
        }
        return null;
    }

    //修改工作量
    public static void editWorkload(Workload workload) {
        if (!FileUtil.exist(workloadsFilePath)) {
            System.out.println("workloads文件不存在");
            return;
        }else {
            List<String> workloadStrs = FileUtil.readUtf8Lines(workloadsFilePath);
            ArrayList<Workload> workloads = workloadList2BeanList(workloadStrs);
            for (int i = 0; i < workloads.size(); i++) {
                Workload w = workloads.get(i);
                if (w.getId().equals(workload.getId())) {
                    workloads.set(i, workload);
                    FileUtil.writeUtf8Lines(workloadList2StringList(workloads), workloadsFilePath);
                    return;
                }
            }
        }
    }

    public static ArrayList<String> workloadList2StringList(ArrayList<Workload> workloads) {
        //1. 创建一个ArrayList<String>容器
        ArrayList<String> arrayList = new ArrayList<String>();
        //2. 遍历users，蒋对象变成字符串
        for (int i = 0; i < workloads.size(); i++) {
            Workload workload = workloads.get(i);
            //格式如下：工作量id#教师用户名#工作量日期#工作量耗时#工作描述#工作反馈
            // 61111111#admin#2013-11-22#teacher#改卷#反馈内容
            String workloadStr = workload.getId() + "#"
                    + workload.getTeacher() + "#"
                    + workload.getWorkDate() + "#"
                    + workload.getHours() + "#"
                    + workload.getDescription() + "#"
                    + (workload.getFeedback() != null ? workload.getFeedback() : "");
            arrayList.add(workloadStr);
        }
        return arrayList;
    }


    public static ArrayList<Workload> workloadList2BeanList(List<String> workloadStrs) {

        //1. 创建一个ArrayList<Workload>容器
        ArrayList<Workload> workloads = new ArrayList<Workload>();

        //2. 遍历accounts，蒋对象变成字符串

        for (int i = 0; i < workloadStrs.size(); i++) {
            String s = workloadStrs.get(i);  // 61111111#admin#2013-11-22#teacher#改卷
            if (s != null && s.length() > 0 && s.contains("#")) {
                //使用#进行切割
                String[] split = s.split("#");
                Workload workload = new Workload(split[0], split[1], split[2], Float.parseFloat(split[3]), split[4]);
                workloads.add(workload);
            }
        }
        return workloads;
    }

}


