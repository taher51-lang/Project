import java.sql.*;
import java.util.Scanner;

public class Main {
    static Connection con;
    static Scanner sc = new Scanner(System.in);
    static boolean roleIdentification() throws SQLException, ClassNotFoundException {
        boolean adminOrUser = true;
        System.out.println("---Kindly choose your specific role");
        System.out.println("---1.User");
        System.out.println("---2.Admin");
        int role = 0;
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");

        try {
            role = Integer.parseInt(sc.next());
        }
        catch (NumberFormatException e) {
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
                        case 1: while (!login()){
                            System.out.println("Incorrect userName or password");
                        }
                            temp=false;
                            System.out.println("Enter whether you have lost Or found any thing");
                            String circumstance=sc.next();
                            if(circumstance.equalsIgnoreCase("lost")){
                                Lost();
                            }
                            break;
                        case 2: User.registration();
                            temp=false;
                            break;
                        default:
                            System.out.println("Enter proper digit");break;
                    }
                }
                ;
                break;
            case 2: adminOrUser=false;break;
            default:
                System.out.println("Enter 1 or 2 in accordance to your role");
                roleIdentification();
                break;
        }
        return adminOrUser;
    }
    static boolean login() throws SQLException {
        System.out.println("Enter your user Name");
        String userName=sc.next();
        sc.nextLine();
        System.out.println("Enter your password");
        String pass= sc.next();
        String sql="Select name from user where user_Name='"+userName+"' and user_pass='"+pass+"'";

        Statement st= con.createStatement();
        ResultSet rs= st.executeQuery(sql);
        if(rs.next()){
            System.out.println("your name:"+rs.getString(1));
            System.out.println("you have logged in successfully!");
            return true;
        }
        return false;
    }
    static Item Lost() throws SQLException {
        System.out.println("Kindly enter the category in which your lost item fits: ");
        System.out.println("1.Electronics & Gadgets\n" +
                "(Mobile phones, tablets, laptops, cameras, chargers, earphones etc)\n" +"\n"+
                "\n2.Bags & Carriers\n" +
                "(Suitcases, backpacks, laptop bags, tote bags)\n" +"\n"+
                "\n3.Clothing & Accessories\n" +
                "(Jackets, scarves, gloves, caps, jewelry, watches)\n" +"\n"+
                "\n4.Identity & Documents\n" +
                "(ID cards, passports, licenses, student cards, certificates)\n" +"\n"+
                "\n5.Financial Items\n" +
                "(Wallets, purses, credit/debit cards, cash envelopes)\n" +"\n"+
                "\n6.Academic Supplies\n" +
                "Books, notebooks, stationery, calculators\n" +"\n"+
                "\n7.Hobby & Entertainment Gear\n" +"\n"+
                "(Game consoles, musical instruments, sports equipment)\n" +"\n"+
                "\n8.Children’s Belongings\n"+"\n"+
                "(Toys, lunch boxes, baby bags, pacifiers)\n" +
                "\n Eyewear & Vision Aids\n" +"\n"+
                "(Glasses, sunglasses, contact lens kits)\n" +"\n"+
                "\n Keys & Access Devices\n" +
                "\n"+"(House keys, car keys, RFID tags, access cards)");
        int choice = 0;
        boolean isValid = true;
        while (isValid){
            try{
                choice=Integer.parseInt(sc.next());
            }
            catch (Exception e){
                System.out.println("Enter numeric input from the lost category menu");
                continue;
            }
            isValid=false;
        }
        System.out.println("Enter The area in which your Item has been lost");
        switch (choice){
            case 1:


        }
    }
    static void found(){
        System.out.println("Kindly Enter the details of the thing you have found as instructed");
        System.out.println("what is the thing you have found(e.g wallet,Mobile etc)?");
        String type = sc.nextLine();
        System.out.println("Enter the area you have found it");
        String area = sc.nextLine();
        System.out.println("Enter the date(YYYY-MM-DD) ");
        String date=sc.nextLine();
        System.out.println("Enter it's color");
        String color=sc.nextLine();
        System.out.println("Enter it's brand");
        String brand=sc.nextLine();
        System.out.println("Enter (Yes/No) there is a unique attribute");
        String choice=sc.nextLine();
    }
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the lost and found Managment System ");
        roleIdentification();
    }
}
class User{
    static Connection con;
    static Scanner sc = new Scanner(System.in);
    String username;
    String password;
    Long MobileNo;
    String email_id;
    String Name;
    static void registration() throws ClassNotFoundException, SQLException {
        System.out.println("----Follow the steps to register yourself ");
        System.out.println("Enter your name");
        String Name = sc.nextLine();//Code to be made more readable and maintable using getemail,getname etc
        System.out.println("Create a username for your profile");
        String username = sc.next();//Make sure this username has not been taken already
        System.out.println("Create new password");
        String pass = sc.next();
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
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        System.out.println("Driver installed");
        String dbUrl = "jdbc:mysql://localhost:3306/project"; //changed part
        String dbUser = "root";
        String dpPass = "";
        con = DriverManager.getConnection(dbUrl,dbUser,dpPass);
        Statement st = con.createStatement();
        Scanner sc = new Scanner(System.in);
        String sql1 = "insert into user(user_name,Name,mobileNo,email_id,user_pass) values('"+username+"','"+Name+"',"+mobileNo+",'"+email_id+"','"+pass+"')";
        int r = st.executeUpdate(sql1);
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }
}
class Item {
    Scanner sc=new Scanner(System.in);
    String name;
    String area;
    String date;
    String color;
    String brand;
    String attribute;
    public Item( String name, String area, String date, String color, String brand, String attribute) {
        this.name = name;
        this.area = area;
        this.date = date;
        this.color = color;
        this.brand = brand;
        this.attribute = attribute;
    }
    public String getName() {
        return name;
    }
    public String getArea() {
        return area;
    }
    public String getDate() {
        return date;
    }
    public String getColor() {
        return color;
    }
    public String getBrand() {
        return brand;
    }
    public String getAttribute() {
        return attribute;
    }
}
class Electronics extends Item{
    public Electronics(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class Bags extends Item{
    public Bags(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class Accessories extends Item{
    public Accessories(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class Documents extends Item{
    public Documents(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class FinancialDocs extends Item{
    public FinancialDocs(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class AcademicSupplies extends Item{
    public AcademicSupplies(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class EntertainmentsGears extends Item{
    public EntertainmentsGears(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class ChildStuff extends Item {
    public ChildStuff(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class eyeAndVision extends Item{
    public eyeAndVision(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}
class Keys extends Item {
    public Keys(String name, String area, String date, String color, String brand, String attribute) {
        super(name, area, date, color, brand, attribute);
    }
}