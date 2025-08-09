import java.sql.*;
import java.util.Scanner;
class Tree{
    class Node{
        Node right;
        Node left;
        String data;
        Node(String data){
            this.data = data;
            left = null;
            right = null;
        }
    }
    Node root = null;
    Boolean searchTree(String subject){
        Node temp = root;
        if(root==null)
            return false;
        else{
            while (temp!=null){
                int result = subject.compareTo(temp.data);
                if(result==0)
                    return true;
                else if (result<0) {
                    temp=temp.left;
                }
                else{
                    temp=temp.right;
                }
            }
        }
        return false;
    }
    boolean insert(String subject){
        Node n = new Node(subject);
        if(root==null)
            root=n;
        else{
            Node temp = root;
            boolean duplicate = !searchTree(subject);
            if(searchTree(subject)){
                System.out.println("The username is already taken!! Kindly choose a different one");
                return true;
            }
            while (duplicate&&temp!=null){
                if(temp.left==null&&temp.data.compareTo(subject)<0){
                    temp.left = n;
                    return false;
                }
                else if(temp.right==null&&temp.data.compareTo(subject)>0){
                    temp.right=n;
                    return false;
                }
                else{
                    if(subject.compareTo(temp.data)<0)
                        temp=temp.left;
                    else
                        temp=temp.right;
                }
            }
        }
        return false;
    }
}

public class Main {
    static Connection con;
    static Scanner sc = new Scanner(System.in);
    static Item lost;
    static int id;
    static boolean roleIdentification() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        boolean adminOrUser = true;
        System.out.println("---Kindly choose your specific role");
        System.out.println("---1.User");
        System.out.println("---2.Admin");
        int role = 0;
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
                            System.out.println("Enter whether you have lost Or found any thing");
                            String circumstance=sc.next();
                            if(circumstance.equalsIgnoreCase("lost")){
                                Lost();
                            }
                            temp = false;//Updates of User side dashboard to be made here
                            break;
                        case 2: User.registration();
                            temp=false;
                            break;
                        default:
                            System.out.println("Enter proper digit");break;
                    }
                }

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
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        System.out.println("Enter your user Name");
        String userName=sc.next();
        sc.nextLine();
        System.out.println("Enter your password");
        String pass= sc.next();
        String sql="Select userId,name from user where user_Name='"+userName+"' and user_pass='"+pass+"'";
        Statement st= con.createStatement();
        ResultSet rs= st.executeQuery(sql);
        if(rs.next()){
            id = rs.getInt(1);
            System.out.println("Hi "+rs.getString(2)+"!!");
            System.out.println("you have logged in successfully!");
            return true;
        }
        return false;
    }
    static public int insertDetails(Item e) throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        String sql = "INSERT INTO Lostandfound(uid, name, area, date, color, attribute, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, id);
            ps.setString(2, e.name);
            ps.setString(3, e.area);
            ps.setString(4, e.date);
            ps.setString(5, e.color);
            ps.setString(6, e.attribute);
            ps.setString(7, "lost");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    System.out.println("No Id Found");
                    return -1;
                }
            }
    }
    static Item Lost() throws SQLException {
        System.out.println("Kindly enter the category in which your lost item fits: ");
        System.out.println("""
                1.Electronics & Gadgets
                (Mobile phones, tablets, laptops, cameras, chargers, earphones etc)
                
                
                2.Bags & Carriers
                (Suitcases, backpacks, laptop bags, tote bags)
                
                
                3.Clothing & Accessories
                (Jackets, scarves, gloves, caps, jewelry, watches)
                
                
                4.Identity & Documents
                (ID cards, passports, licenses, student cards, certificates)
                
                
                5.Academic Supplies
                Books, notebooks, stationery, calculators
                
                
                6.Hobby & Entertainment Gear
                
                (Game consoles, musical instruments, sports equipment)
                
                
                .7Children’s Belongings
                
                (Toys, lunch boxes, baby bags, pacifiers)
                
                8.Eyewear & Vision Aids
                
                (Glasses, sunglasses, contact lens kits)
                
                
                9.Keys & Access Devices
                
                (House keys, car keys, RFID tags, access cards)""");
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
        Statement st = con.createStatement();
        switch (choice){
            case 1:Electronics e = new Electronics();
                e.setDetails();
                int lid1 = insertDetails(e);//Last generated id
                PreparedStatement pst1 = con.prepareStatement("insert into electronicsandgadgets values(?,?,?)");
                pst1.setInt(1,lid1);
                pst1.setString(2,e.brand);
                pst1.setString(3,e.model);
                pst1.executeUpdate();
            lost= e;break;
            case 2:Bags a = new Bags();
                a.setDetails();
                int lid2 = insertDetails(a);//Last generated id
                PreparedStatement pst2 = con.prepareStatement("insert into bags values(?,?,?)");
                pst2.setInt(1,lid2);
                pst2.setString(2,a.material);
                pst2.setString(3,a.brand);
                pst2.executeUpdate();
            lost=a;
            break;
            case 3: Accessories c = new Accessories();
            c.setDetails();
            int lid3 = insertDetails(c);
            PreparedStatement pst3 = con.prepareStatement("insert into accessories values(?,?)");
            pst3.setInt(1,lid3);
            pst3.setString(2,c.brand);
            pst3.executeUpdate();
            lost=c;
            break;
            case 4:Documents d = new Documents();
            d.setDetails();
            int lid4 = insertDetails(d);
            PreparedStatement pst4 = con.prepareStatement("insert into Documents values(?,?)");
            pst4.setInt(1,lid4);
            pst4.setString(2,d.issueAuthority);
            pst4.executeUpdate();
            lost=d;
            break;
            case 5:AcademicSupplies s = new AcademicSupplies();
            s.setDetails();
                int lid5 = insertDetails(s);
                PreparedStatement pst5 = con.prepareStatement("insert into AcademicSupplies values(?,?)");
                pst5.setInt(1,lid5);
                pst5.setString(2,s.brand);
                pst5.executeUpdate();
            lost=s;
            break;
            case 6:EntertainmentsGears eg = new EntertainmentsGears();
            eg.setDetails();
                int lid6 = insertDetails(eg);
                PreparedStatement pst6 = con.prepareStatement("insert into entertainmentgears values(?,?)");
                pst6.setInt(1,lid6);
                pst6.setString(2,eg.brand);
                pst6.executeUpdate();
            lost=eg;
            break;
            case 7:ChildStuff cs = new ChildStuff();
            cs.setDetails();
                int lid7 = insertDetails(cs);
                PreparedStatement pst7 = con.prepareStatement("insert into Childstuff values(?,?)");
                pst7.setInt(1,lid7);
                pst7.setString(2,cs.brand);
                pst7.executeUpdate();
            lost = cs;
            break;
            case 8:eyeAndVision ev = new eyeAndVision();
            ev.setDetails();
                int lid8 = insertDetails(ev);
                PreparedStatement pst8 = con.prepareStatement("insert into eyeAndvision values(?,?,?)");
                pst8.setInt(1,lid8);
                pst8.setString(2,ev.FrameType);
                pst8.setString(3,ev.lensGrade);
                pst8.setString(4,ev.brand);
                pst8.executeUpdate();
            lost=ev;
            break;
            case 9:Keys k = new Keys();
            k.setDetails();
                int lid9 = insertDetails(k);
                PreparedStatement pst9 = con.prepareStatement("insert into Keys values(?,?)");
                pst9.setInt(1,lid9);
                pst9.setString(2,k.keyType);
                pst9.setString(3,k.brand);
                pst9.executeUpdate();
            lost = k;
            break;
            default:
                System.out.println("Kindly enter proper input");
               Lost();
               break;
        }
        return lost;
    }
//    static void found(){
//        System.out.println("Kindly Enter the details of the thing you have found as instructed");
//        System.out.println("what is the thing you have found(e.g wallet,Mobile etc)?");
//        String type = sc.nextLine();
//        System.out.println("Enter the area you have found it");
//        String area = sc.nextLine();
//        System.out.println("Enter the date(YYYY-MM-DD) ");
//        String date=sc.nextLine();
//        System.out.println("Enter it's color");
//        String color=sc.nextLine();
//        System.out.println("Enter it's brand");
//        String brand=sc.nextLine();
//        System.out.println("Enter (Yes/No) there is a unique attribute");
//        String choice=sc.nextLine();
//    }
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the lost and found Managment System ");
        if(roleIdentification()){
            System.out.println("Hoo");
        };
        //User Login-User side function embedded in nested methods one after one in this method;

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
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        System.out.println("Driver installed");
        String dbUrl = "jdbc:mysql://localhost:3306/project"; //changed part
        String dbUser = "root";
        String dpPass = "";
        con = DriverManager.getConnection(dbUrl,dbUser,dpPass);
        Tree Users = new Tree();
        String sql = "select user_name from user";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
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
        //Make sure this username has not been taken already
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
        Statement st = con.createStatement();
        String sql1 = "insert into user(user_name,Name,mobileNo,email_id,user_pass) values('"+username+"','"+Name+"',"+mobileNo+",'"+email_id+"','"+pass+"')";
    }
    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }
}
class Item {
    Scanner sc = new Scanner(System.in);
    String name;
    String area;
    String date;
    String color;
    String attribute;

    public void setName() {
        name = sc.nextLine();
    }

    public void setArea() {
        System.out.println("Enter the area it might have lost");
        area = sc.nextLine();
    }

    public void setDate() {
        System.out.println("Enter the date when it got lost");
        try {
            date = sc.nextLine();
        } catch (Exception e) {
            System.out.println("enter proper date");
        }
    }

    public void setColor() {
        System.out.println("Enter the color of the lost thing");
        color = sc.nextLine();
    }

    public void setAttribute() {
        System.out.println("Describe any unique quality of your lost item that helps the system to validate its rightful owner");
        attribute =sc.nextLine();
    }
}
class Electronics extends Item{
   String model;
   String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
   public void setDetails(){
       setName();setDate();setArea();setBrand();setColor();
       System.out.println("Enter the model of the electronic device e.g(Dell series laptop:Inspiron 3511)");
       model=sc.nextLine();
       setAttribute();
   }
}
class Bags extends Item{
  String material;
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
  public void setDetails(){
      System.out.println("Enter the category of bag: e.g(shoulder bag,suitcase etc)");
      setName();setDate();setArea();setBrand();setColor();
      System.out.println("Enter the material of the lost bag");
      material=sc.nextLine();
      setAttribute();
  }
}
class Accessories extends Item{
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
    String material;
    public void setDetails(){
        System.out.println("Enter the type of Accessory you lost. e.g(Watch,cloth type(jacket,sweater) etc)");
        setName();setDate();setArea();setBrand();setColor();
        System.out.println("Enter the material of the lost item:(In one word)");
        material= sc.next();
        System.out.println("Enter the condition of the Item(new,old)(In one word):");
        setAttribute();
    }
}
class Documents extends Item{
    String issueAuthority;
    public void setDetails(){
        System.out.println("Enter the name of Document you lost. e.g(Aadhar card,Pan card etc)");
        setName();setDate();setArea();setColor();
        System.out.println("Specify The issuing authority of the lost Document");
        issueAuthority=sc.nextLine();
        setAttribute();
    }
}
class AcademicSupplies extends Item {
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
    public void setDetails() {
        System.out.println("Enter the name of the lost item.e.g(Notebooks,Educational equipments)");
        name = sc.nextLine();
        setDate();
        setArea();
        setBrand();
        setColor();
        setAttribute();
    }
}
class EntertainmentsGears extends Item{
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
    public void setDetails() {
        System.out.println("Enter the name of the lost item.e.g(Musical Instruments,sports equipments)");
        name = sc.nextLine();
        setDate();
        setArea();
        setBrand();
        setColor();
        setAttribute();
    }
}
class ChildStuff extends Item {
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
public void setDetails(){
    System.out.println("Enter the name of the lost item.e.g(Baby stroller,Toys)");
    name = sc.nextLine();
    setDate();
    setArea();
    setBrand();
    setColor();
    setAttribute();
}
}
class eyeAndVision extends Item{
String FrameType;
String lensGrade;
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
    public void setDetails(){
        name = "lens";
        setDate();
        setArea();
        setBrand();
        setColor();
        System.out.println("Specify the Frame type of the lens(e.g Full rim,Half rim)");
        FrameType=sc.nextLine();
        System.out.println("Specify Lens Grade. e.g(Polymer,Glass)");
        lensGrade=sc.nextLine();
    }
}
class Keys extends Item {
    String keyType;
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost key if any(Written on the key.)");
        brand = sc.nextLine();
    }
    public void setDetails() {
        name = "keys";
        setDate();
        setArea();
        setBrand();
        setColor();
        System.out.println("Specify the type of key you have lost(Vehicle keys,Lock keys etc)");
        keyType = sc.nextLine();
        setAttribute();
    }
}
