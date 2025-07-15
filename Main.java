import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static void roleIdentification(){
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
                        case 2: registration();
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
    static void registration(){}
    public static void main(String[] args) throws Exception {
//        String driverName = "com.mysql.cj.jdbc.Driver";
//        Class.forName(driverName);
//        System.out.println("Driver installed");
//        String dbUrl = "jdbc:mysql://localhost:3306/Project";
//        String dbUser = "root";
//        String dpPass = "";
//        Connection con = DriverManager.getConnection(dbUrl,dbUser,dpPass);
//        if(con!=null)
//            System.out.println("Cnnection sucess");
//        else
//            System.out.println("Connection unsuccessful");
//        Statement st = con.createStatement();
//        Scanner sc = new Scanner(System.in);
////        String sql1 = "insert into user values(1,'Taher',9173337452,'rangwalataher@gmail.com','taher123')";
////        int r = st.executeUpdate(sql1);
////        if(r>0)
////            System.out.println("Inserted"); Dummy values
//        //The inauguration of my code:
        System.out.println("Welcome to the lost and found Managment System ");
        roleIdentification();
    }
}
class User{
    String username;
    String password;
    Long MobileNo;
    String email_id;
    String fullName;
    public void regitration(){}

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
