import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static void roleIdentification() throws SQLException, ClassNotFoundException {
        System.out.println("---Kindly choose your specific role");
        System.out.println("---1.User");
        System.out.println("---2.Admin");
        int role = 0;
        try {
            role = Integer.parseInt(sc.next());
        } catch (NumberFormatException e) {
            System.out.println("---Kindly enter proper numeric input---");
            roleIdentification();
        }
        switch (role){
            case 1:
                System.out.println("Enter 1 if you are a Registered User and 2 if you are a new user");
                boolean temp = true;
                while (temp){
                try {
                    role = Integer.parseInt(sc.next());
                } catch (NumberFormatException e) {
                    System.out.println("Enter numeric input");
                    continue;
                }
                    switch (role){
                        case 1: login();
                            temp=false;
                            break;
                        case 2: User.registration();
                            temp=false;
                            break;
                        default:
                            System.out.println("Enter proper digit");break;
                    }
                }
                break;
            case 2: break;
            default:
                System.out.println("Enter 1 or 2 in accordance to your role");
                roleIdentification();
                break;
        }
    }
    static boolean login(){
        return true;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the lost and found Managment System ");
        roleIdentification();
    }
}
class User{
    static Scanner sc = new Scanner(System.in);
    String username;
    String password;
    Long MobileNo;
    String email_id;
    String Name;
    static void registration() throws ClassNotFoundException, SQLException {
        System.out.println("----Follow the steps to register yourself ");
        System.out.println("Enter your name");
        String Name = sc.nextLine();
        System.out.println("Create a username for your profile");
        String username = sc.next();
        System.out.println("Create new password");
        String pass = sc.next();
        System.out.println("Enter your mobile No(It must start with 8  or 9");
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
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        System.out.println("Driver installed");
        String dbUrl = "jdbc:mysql://localhost:3306/Project";
        String dbUser = "root";
        String dpPass = "";
        Connection con = DriverManager.getConnection(dbUrl,dbUser,dpPass);
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        String sql1 = "insert into user(user_name,Name,mobileNo,email_id,user_pass) values('"+Name+"','"+username+"',"+mobileNo+",'"+email_id+"','"+pass+"')";
        int r = st.executeUpdate(sql1);

    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }
}
class Item {
     String name;
     String location;
     String date;
    public Item(String name, String location, String date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
}
