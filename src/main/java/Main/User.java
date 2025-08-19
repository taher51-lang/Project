package Main;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Scanner;

public class User{
    static Connection con;
    static Scanner sc = new Scanner(System.in);
    public static boolean isEmailLike(String input) {
        return input != null && input.contains("@")&&input.length()>=10;
    }
    static void registration() throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        System.out.println("Driver installed");
        String dbUrl = "jdbc:mysql://localhost:3306/Project"; //changed part
        String dbUser = "root";
        String dpPass = "";
        con = DriverManager.getConnection(dbUrl,dbUser,dpPass);
        String sql = "select user_name from user";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        Tree Users = new Tree();
        while (rs.next()){
            Users.insert(rs.getString(1));
        }
        System.out.println("----Follow the steps to register yourself ");
        System.out.println("Enter your name");
        String Name = sc.nextLine();//Code to be made more readable and maintable using getemail,getname etc
        System.out.println("Create a username for your profile");
        String username = sc.next();
        while (Users.insert(username)){
            username=sc.next();
        }
        System.out.println("Create new password(Minimum length 5)");
        String pass = sc.next();
        String plainPassword = pass;
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        while (true){
            if (pass.length()>=5){
                break;
            }
            else{
                System.out.println("Enter password again");
                pass=sc.next();
            }
        }
        System.out.println("Enter your mobile No(It must start with 8  or 9)");
        long mobileNo = 0;
        boolean temp=true;
        while (temp){
            try{
                mobileNo=sc.nextLong();
                if(mobileNo<=9999999999L&&mobileNo>7999999999L){
                    temp=false;
                }else{
                    System.out.println("invalid input");
                }
            }
            catch (RuntimeException e) {
                System.out.println("Enter numeric input");
                sc.nextLine();
            }
        }
        System.out.println("Enter your email address");
        String email_id = sc.next();
        while (!isEmailLike(email_id)){
            System.out.println("Incorrect emailID");
            email_id= sc.next();
        }
        Statement st = con.createStatement();
        String sql1 = "INSERT INTO user(user_name, Name, mobileNo, email_id, user_pass) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql1);
        stmt.setString(1, username);
        stmt.setString(2, Name);
        stmt.setLong(3, mobileNo);
        stmt.setString(4, email_id);
        stmt.setString(5, hashedPassword);
        stmt.executeUpdate();
        String sql2="Select userId,name from user where user_Name='"+username+"'";
        Statement st2 = con.createStatement();
        ResultSet rs2= st2.executeQuery(sql2);
        if(rs2.next())
            Main.id = rs2.getInt(1);
    }
}