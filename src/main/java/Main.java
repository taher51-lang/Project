import org.simmetrics.StringMetric;
import org.simmetrics.metrics.JaroWinkler;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

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
                        LostAndFoundProcess();
                        temp=false;
                        break;
                        case 2: User.registration();
                        LostAndFoundProcess();
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
    static void LostAndFoundProcess() throws SQLException {
        System.out.println("Enter whether you have lost Or found any thing");
        String circumstance=sc.next();
        if(circumstance.equalsIgnoreCase("lost")){
            Item subject = Lost();
            DoublyLinkedList stage1 = search(subject);
            if(stage1==null){
                System.out.println("No such Item detected. Kindly login again to proceed");
                System.exit(0);
            }
            stage1.display();
            DoublyLinkedList stage2 = filterName_location_color(stage1,subject);//Name and location and color
            DoublyLinkedList stage3 = filterAdditionals(stage2,subject);
            if(stage3.exists()){
                stage3.display();
                System.out.println("We have found a Match in accordance to your Specifications.");
                System.out.println("Your request is in Admin verification and you may get contacted for more details via email.");
                System.out.println("The whole process might take upto a week to complete.");
                stage3.setAdminVerification(id,subject);
            }
            else{
                System.out.println("No such item has been found");
            }
        }
        else{
            found();
        }
    }

    private static DoublyLinkedList filterAdditionals(DoublyLinkedList stage3, Item subject) {
        String result = getSubjectInstance(subject);
        return stage3.filterMoreAttributes(subject,result);
    }

    static public int insertDetails(Item e,String status) throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        String sql = "INSERT INTO Lostandfound(uid, name, area, date, color, attribute, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, id);
            ps.setString(2, e.name);
            ps.setString(3, e.area);
            ps.setString(4, e.date);
            ps.setString(5, e.color);
            ps.setString(6, e.attribute);
            ps.setString(7, status);
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
    public static void getItems(){
        System.out.println(
                "1. Electronics & Gadgets"+
                   "(Mobile phones, tablets, laptops, cameras, chargers, earphones etc)"+

                "2. Bags & Carriers"+
                   "(Suitcases, backpacks, laptop bags, tote bags)"+

               "3. Clothing & Accessories"+
                  " (Jackets, scarves, gloves, caps, jewelry, watches)"+

                "4. Identity & Documents "+
                   "(ID cards, passports, licenses, student cards, certificates)"+

                "5. Academic Supplies"+
                   "(Books, notebooks, stationery, calculators)"+

                "6. Hobby & Entertainment Gear"+
                 "  (Game consoles, musical instruments, sports equipment)"+

                ". Children’s Belongings"+
                  " (Toys, lunch boxes, baby bags, pacifiers)"+

                "8. Eyewear & Vision Aids"+
                   "(Glasses, sunglasses, contact lens kits)"+

                "9. Keys & Access Devices"+
                   "(House keys, car keys, RFID tags, access cards)");


    }
    static Item Lost() throws SQLException {
        System.out.println("Kindly enter the category in which your lost item fits: ");
       getItems();
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
        switch (choice){
            case 1:Electronics e = new Electronics();
                e.setDetails();
                int lid1 = insertDetails(e,"lost");//Last generated id
                PreparedStatement pst1 = con.prepareStatement("insert into electronicsandgadgets values(?,?,?)");
                pst1.setInt(1,lid1);
                pst1.setString(2,e.brand);
                pst1.setString(3,e.model);
                pst1.executeUpdate();
            lost= e;break;
            case 2:Bags a = new Bags();
                a.setDetails();
                int lid2 = insertDetails(a,"lost");//Last generated id
                PreparedStatement pst2 = con.prepareStatement("insert into bags values(?,?,?)");
                pst2.setInt(1,lid2);
                pst2.setString(2,a.material);
                pst2.setString(3,a.brand);
                pst2.executeUpdate();
            lost=a;
            break;
            case 3: Accessories c = new Accessories();
            c.setDetails();
            int lid3 = insertDetails(c,"lost");
            PreparedStatement pst3 = con.prepareStatement("insert into accessories values(?,?)");
            pst3.setInt(1,lid3);
            pst3.setString(2,c.brand);
            pst3.executeUpdate();
            lost=c;
            break;
            case 4:Documents d = new Documents();
            d.setDetails();
            int lid4 = insertDetails(d,"lost");
            PreparedStatement pst4 = con.prepareStatement("insert into Documents values(?,?)");
            pst4.setInt(1,lid4);
            pst4.setString(2,d.issueAuthority);
            pst4.executeUpdate();
            lost=d;
            break;
            case 5:AcademicSupplies s = new AcademicSupplies();
            s.setDetails();
                int lid5 = insertDetails(s,"lost");
                PreparedStatement pst5 = con.prepareStatement("insert into AcademicSupplies values(?,?)");
                pst5.setInt(1,lid5);
                pst5.setString(2,s.brand);
                pst5.executeUpdate();
            lost=s;
            break;
            case 6:EntertainmentsGears eg = new EntertainmentsGears();
            eg.setDetails();
                int lid6 = insertDetails(eg,"lost");
                PreparedStatement pst6 = con.prepareStatement("insert into entertainmentgears values(?,?)");
                pst6.setInt(1,lid6);
                pst6.setString(2,eg.brand);
                pst6.executeUpdate();
            lost=eg;
            break;
            case 7:ChildStuff cs = new ChildStuff();
            cs.setDetails();
                int lid7 = insertDetails(cs,"lost");
                PreparedStatement pst7 = con.prepareStatement("insert into Childstuff values(?,?)");
                pst7.setInt(1,lid7);
                pst7.setString(2,cs.brand);
                pst7.executeUpdate();
            lost = cs;
            break;
            case 8:eyeAndVision ev = new eyeAndVision();
            ev.setDetails();
                int lid8 = insertDetails(ev,"lost");
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
                int lid9 = insertDetails(k,"lost");
                PreparedStatement pst9 = con.prepareStatement("insert into KeyAccess values(?,?,?)");
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
    static void found() throws SQLException {
        System.out.println("Kindly Enter the details of the thing you have found as instructed");
        System.out.println("Kindly enter the category in which item you found fits perfectly ");
        getItems();
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
        switch (choice){
            case 1:Electronics e = new Electronics();
                e.setDetails();
                int lid1 = insertDetails(e,"found");//Last generated id
                PreparedStatement pst1 = con.prepareStatement("insert into electronicsandgadgets values(?,?,?)");
                pst1.setInt(1,lid1);
                pst1.setString(2,e.brand);
                pst1.setString(3,e.model);
                pst1.executeUpdate();
                break;
            case 2:Bags a = new Bags();
                a.setDetails();
                int lid2 = insertDetails(a,"found");//Last generated id
                PreparedStatement pst2 = con.prepareStatement("insert into bags values(?,?,?)");
                pst2.setInt(1,lid2);
                pst2.setString(2,a.material);
                pst2.setString(3,a.brand);
                pst2.executeUpdate();
                break;
            case 3: Accessories c = new Accessories();
                c.setDetails();
                int lid3 = insertDetails(c,"found");
                PreparedStatement pst3 = con.prepareStatement("insert into accessories values(?,?)");
                pst3.setInt(1,lid3);
                pst3.setString(2,c.brand);
                pst3.executeUpdate();
                break;
            case 4:Documents d = new Documents();
                d.setDetails();
                int lid4 = insertDetails(d,"found");
                PreparedStatement pst4 = con.prepareStatement("insert into Documents values(?,?)");
                pst4.setInt(1,lid4);
                pst4.setString(2,d.issueAuthority);
                pst4.executeUpdate();
                break;
            case 5:AcademicSupplies s = new AcademicSupplies();
                s.setDetails();
                int lid5 = insertDetails(s,"found");
                PreparedStatement pst5 = con.prepareStatement("insert into AcademicSupplies values(?,?)");
                pst5.setInt(1,lid5);
                pst5.setString(2,s.brand);
                pst5.executeUpdate();
                break;
            case 6:EntertainmentsGears eg = new EntertainmentsGears();
                eg.setDetails();
                int lid6 = insertDetails(eg,"found");
                PreparedStatement pst6 = con.prepareStatement("insert into entertainmentgears values(?,?)");
                pst6.setInt(1,lid6);
                pst6.setString(2,eg.brand);
                pst6.executeUpdate();
                break;
            case 7:ChildStuff cs = new ChildStuff();
                cs.setDetails();
                int lid7 = insertDetails(cs,"found");
                PreparedStatement pst7 = con.prepareStatement("insert into Childstuff values(?,?)");
                pst7.setInt(1,lid7);
                pst7.setString(2,cs.brand);
                pst7.executeUpdate();
                break;
            case 8:eyeAndVision ev = new eyeAndVision();
                ev.setDetails();
                int lid8 = insertDetails(ev,"found");
                PreparedStatement pst8 = con.prepareStatement("insert into eyeAndvision values(?,?,?)");
                pst8.setInt(1,lid8);
                pst8.setString(2,ev.FrameType);
                pst8.setString(3,ev.lensGrade);
                pst8.setString(4,ev.brand);
                pst8.executeUpdate();
                break;
            case 9:Keys k = new Keys();
                k.setDetails();
                int lid9 = insertDetails(k,"found");
                PreparedStatement pst9 = con.prepareStatement("insert into Keyaccess values(?,?,?)");
                pst9.setInt(1,lid9);
                pst9.setString(2,k.keyType);
                pst9.setString(3,k.brand);
                pst9.executeUpdate();
                break;
            default:
                System.out.println("Kindly enter proper input");
                found();
                break;
        }
    }
    public static String getSubjectInstance(Item subject){
        Map<Class<?>, Function<Object, String>> handlers = new HashMap<>();
        handlers.put(Keys.class, item -> ((Keys)item).getClassName());
        handlers.put(Electronics.class, item -> ((Electronics)item).getClassName());
        handlers.put(Bags.class, item -> ((Bags)item).getClassName());
        handlers.put(Accessories.class, item -> ((Accessories)item).getClassName());
        handlers.put(EntertainmentsGears.class, item -> ((EntertainmentsGears)item).getClassName());
        handlers.put(ChildStuff.class, item -> ((ChildStuff)item).getClassName());
        handlers.put(Documents.class, item -> ((Documents)item).getClassName());
        handlers.put(eyeAndVision.class, item -> ((eyeAndVision)item).getClassName());
        handlers.put(AcademicSupplies.class, item -> ((AcademicSupplies)item).getClassName());
        Function<Object, String> handler = handlers.get(subject.getClass());
        if(handler!=null)
           return handler.apply(subject);
        else{
            return "Incorrect object type";
        }
    }
    public static DoublyLinkedList search(Item subject) throws SQLException {
        DoublyLinkedList returnable;

            String result =getSubjectInstance(subject);
            Statement st = con.createStatement();
            switch(result){
                case "Electronics":
                    DoublyLinkedList list1 =  new DoublyLinkedList();
                    String sql = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.Brand, \n" +
                            "    e.Model\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    electronicsandgadgets e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs1 = st.executeQuery(sql);
                    ResultSetMetaData rsm = rs1.getMetaData();
                    while (rs1.next()){
                        HashMap<String,String> records = new HashMap<>();
                       for(int i = 1;i<=rsm.getColumnCount();i++){
                           records.put(rsm.getColumnName(i),rs1.getString(i));
                       }
                        list1.insertAtLast(records);
                    }
                    returnable = list1;
                    break;
                case "Bags":
                    System.out.println("s2 cleared");
                    DoublyLinkedList list2=  new DoublyLinkedList();
                    String sql2 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.material, \n" +
                            "    e.brand\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    bags e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 MONTH) AND CURDATE();";

                    ResultSet rs2 = st.executeQuery(sql2);
                    ResultSetMetaData rsm2 = rs2.getMetaData();
                    while (rs2.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm2.getColumnCount();i++){
                            System.out.println(rs2.getString(1));
                            System.out.println(rs2.getString(2));
                            System.out.println(rs2.getString(3));
                            System.out.println(rsm2.getColumnName(3));
                            System.out.println(rs2.getString(4));
                            records.put(rsm2.getColumnName(i),rs2.getString(i));
                        }
                        list2.insertAtLast(records);
                        System.out.println("S3 clear");
                    }
                    returnable = list2;
                    break;
                case "Accessories":
                    DoublyLinkedList list3 =  new DoublyLinkedList();
                    String sql3 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.Brand\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    accessories e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs3 = st.executeQuery(sql3);
                    ResultSetMetaData rsm3 = rs3.getMetaData();
                    while (rs3.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm3.getColumnCount();i++){
                            records.put(rsm3.getColumnName(i),rs3.getString(i));
                        }
                        list3.insertAtLast(records);
                    }
                    returnable = list3;
                    break;
                case "AcademicSupplies":
                    DoublyLinkedList list4 =  new DoublyLinkedList();
                    String sql4 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.Brand\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    AcademicSupplies e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs4 = st.executeQuery(sql4);
                    ResultSetMetaData rsm4 = rs4.getMetaData();
                    while (rs4.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm4.getColumnCount();i++){
                            records.put(rsm4.getColumnName(i),rs4.getString(i));
                        }
                        list4.insertAtLast(records);
                    }
                    //needs improvement
                    returnable = list4;
                    break;
                case "Documents":
                    DoublyLinkedList list5 =  new DoublyLinkedList();
                    String sql5 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.isssueauthority\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    Documents e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs5 = st.executeQuery(sql5);
                    ResultSetMetaData rsm5= rs5.getMetaData();
                    while (rs5.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm5.getColumnCount();i++){
                            records.put(rsm5.getColumnName(i),rs5.getString(i));
                        }
                        list5.insertAtLast(records);
                    }
                    returnable = list5;
                    break;
                case "ChildStuff":
                    DoublyLinkedList list6 =  new DoublyLinkedList();
                    String sql6 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.Brand\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    Childstuff e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs6 = st.executeQuery(sql6);
                    ResultSetMetaData rsm6 = rs6.getMetaData();
                    while (rs6.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm6.getColumnCount();i++){
                            records.put(rsm6.getColumnName(i),rs6.getString(i));
                            list6.insertAtLast(records);
                        }
                        list6.insertAtLast(records);
                    }
                    returnable = list6;
                    break;
                case "EntertainmentGears":
                    DoublyLinkedList list7 =  new DoublyLinkedList();
                    String sql7 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.Brand\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    entertainmentgears e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs7 = st.executeQuery(sql7);
                    ResultSetMetaData rsm7 = rs7.getMetaData();
                    while (rs7.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm7.getColumnCount();i++){
                            records.put(rsm7.getColumnName(i),rs7.getString(i));
                        }
                        list7.insertAtLast(records);
                    }
                    returnable = list7;
                    break;
                case "eyeAndVision":
                    DoublyLinkedList list8 =  new DoublyLinkedList();
                    String sql8 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.frametype, \n" +
                            "    e.lensgrade, \n" +
                            "    e.brand\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    eyeAndVision e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs8 = st.executeQuery(sql8);
                    ResultSetMetaData rsm8 = rs8.getMetaData();
                    while (rs8.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm8.getColumnCount();i++){
                            records.put(rsm8.getColumnName(i),rs8.getString(i));
                        }
                        list8.insertAtLast(records);
                    }
                    returnable = list8;
                    break;
                case "Keys":
                    DoublyLinkedList list9 =  new DoublyLinkedList();
                    String sql9 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.Brand, \n" +
                            "    e.keytype\n" +
                            "FROM \n" +
                            "    LostAndfound l\n" +
                            "JOIN \n" +
                            "    KeyAccess e ON l.id = e.id\n" +
                            "WHERE \n" +
                            "    l.status = 'found'\n" +
                            "    AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE();";
                    ResultSet rs9 = st.executeQuery(sql9);
                    ResultSetMetaData rsm9 = rs9.getMetaData();
                    while (rs9.next()){
                        HashMap<String,String> records = new HashMap<>();
                        for(int i = 1;i<=rsm9.getColumnCount();i++){
                            records.put(rsm9.getColumnName(i),rs9.getString(i));
                        }
                        list9.insertAtLast(records);
                    }
                    returnable = list9;
                    break;
                default:
                    System.out.println("No such type");
                    return null;
            }

        return returnable;
    }
    public static DoublyLinkedList filterName_location_color(DoublyLinkedList CFiltered,Item subject){
        return CFiltered.flFilter(subject);
    }
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the lost and found Managment System ");
        if(roleIdentification()){
            System.out.println("Hoo");
        }
        //User Login-User side function embedded in nested methods one after one in this method;

    }
}
class User{
    static Connection con;
    static Scanner sc = new Scanner(System.in);
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
        st.executeUpdate(sql1);
        String sql2="Select userId,name from user where user_Name='"+username+"'";
        Statement st2 = con.createStatement();
        ResultSet rs2= st2.executeQuery(sql2);
        if(rs2.next())
            Main.id = rs2.getInt(1);
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
        System.out.println("Enter the area it might have lost/found");
        area = sc.nextLine();
    }
    public void setDate() {
        System.out.println("Enter the date when it got lost/found");
        try {
            date = sc.nextLine();
        } catch (Exception e) {
            System.out.println("enter proper date");
        }
    }
    public void setColor() {
        System.out.println("Enter the color of the lost/found thing");
        color = sc.nextLine();
    }
    public void setAttribute() {
        System.out.println("Describe any unique quality of your lost/found item that helps the system to validate its rightful owner");
        attribute =sc.nextLine();
    }
}
class Electronics extends Item{
    public String getClassName() {
        return "Electronics";
    }
   String model;
   String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost/found item if any");
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
    public String getClassName() {
        return "Bags";
    }
  String material;
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost/found item if any");
        brand = sc.nextLine();
    }
  public void setDetails(){
      System.out.println("Enter the category of bag: e.g(shoulder bag,suitcase etc)");
      setName();setDate();setArea();setBrand();setColor();
      System.out.println("Enter the material of the  bag");
      material=sc.nextLine();
      setAttribute();
  }
}
class Accessories extends Item{
    public String getClassName() {
        return "Accessories";
    }
    String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the  item if any");
        brand = sc.nextLine();
    }
    String material;
    public void setDetails(){
        System.out.println("Enter the type of Accessory . e.g(Watch,cloth type(jacket,sweater) etc)");
        setName();setDate();setArea();setBrand();setColor();
        System.out.println("Enter the material of the  item:(In one word)");
        material= sc.next();
        setAttribute();
    }
}
class Documents extends Item{
    public String getClassName() {
        return "Documents";
    }
    String issueAuthority;
    public void setDetails(){
        System.out.println("Enter the name of Document . e.g(Aadhar card,Pan card etc)");
        setName();setDate();setArea();setColor();
        System.out.println("Specify The issuing authority of the lost Document");
        issueAuthority=sc.nextLine();
        setAttribute();
    }
}
class AcademicSupplies extends Item {
    public String getClassName() {
        return "AcademicSupplies";
    }
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
    public String getClassName() {
        return "EntertainmentGears";
    }
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
    public String getClassName() {
        return "ChildStuff";
    }
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
        setAttribute();
        System.out.println("Specify the Frame type of the lens(e.g Full rim,Half rim)");
        FrameType=sc.nextLine();
        System.out.println("Specify Lens Grade. e.g(Polymer,Glass)");
        lensGrade=sc.nextLine();
    }

    public String getClassName() {
        return "eyeAndVision";
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
    public String getClassName() {
        return "Keys";
    }
}
class DoublyLinkedList{
    public DoublyLinkedList flFilter(Item subject) {
        DoublyLinkedList filter = new DoublyLinkedList();
        Node temp = first;
        while (temp!=null){
            StringMetric a = new JaroWinkler();
            float scoreName = a.compare(temp.record.get("name"), subject.name);
            float scoreAddress =  a.compare(temp.record.get("area"), subject.area);
            float scorecolor = a.compare(temp.record.get("color"), subject.color);
            float sum = scorecolor+scoreAddress+scoreName;
            if(sum>=1.95) {
                filter.insertAtLast(temp.record);
            }
                temp=temp.next;
        }
        return filter;
    }

    public DoublyLinkedList filterMoreAttributes(Item subject, String result) {
        Node temp = first;
        StringMetric match = new JaroWinkler();
        DoublyLinkedList finalFilter = new DoublyLinkedList();
        while (temp!=null){
        switch (result.toLowerCase()) {
            case "electronics":
                Electronics e = (Electronics) subject;
                float brandScore1 = match.compare(e.brand,temp.record.get("brand"));
                float modelScore = match.compare(e.model,temp.record.get("Model"));
                if((brandScore1+modelScore)>=1.3){
                    finalFilter.insertAtLast(temp.record);
                }
                break;
            case "bags":
                Bags b = (Bags) subject;
                System.out.println(b.brand);
                float MaterialScore = match.compare(b.material,temp.record.get("material"));
                float brandScore2 = match.compare(b.brand,temp.record.get("brand"));
                if((brandScore2+MaterialScore)>=1.3){
                    finalFilter.insertAtLast(temp.record);
                }
                break;
            case "accessories":
                Accessories ac = (Accessories) subject;
                float brandScore3 = match.compare(ac.brand,temp.record.get("brand"));
                if(brandScore3>0.65){
                    finalFilter.insertAtLast(temp.record);
                }
                break;
                case "childstuff":
                ChildStuff ch = (ChildStuff) subject;
                float brandScore4 = match.compare(ch.brand,temp.record.get("brand"));
                if(brandScore4>0.65){
                    finalFilter.insertAtLast(temp.record);
                }
                break;
                case "academicsupplies":
                    AcademicSupplies as = (AcademicSupplies) subject;
                    float brandScore5 = match.compare(as.brand,temp.record.get("brand"));
                    if(brandScore5>0.65){
                        finalFilter.insertAtLast(temp.record);
                   }
                break;
                case "entertainmentgears":
                    EntertainmentsGears et = (EntertainmentsGears) subject;
                    float brandScore6 = match.compare(et.brand,temp.record.get("brand"));
                    if(brandScore6>0.65){
                       finalFilter.insertAtLast(temp.record);
                   }
                break;
            case "keys":
                Keys k = (Keys) subject;
                float keyScore = match.compare(k.keyType,temp.record.get("keytype"));
                float brandscore7 = match.compare(k.brand,temp.record.get("brand"));
                if((brandscore7+keyScore)>=1.3)
                    finalFilter.insertAtLast(temp.record);
                break;
            case "eyeandvision":
                eyeAndVision ev = (eyeAndVision) subject;
                float frameScore = match.compare(ev.FrameType,temp.record.get("frametype"));
                float lensScore = match.compare(ev.lensGrade,temp.record.get("lensgrade"));
                float brandScore8 = match.compare(ev.brand,temp.record.get("brand"));
                if((frameScore+lensScore+brandScore8)>=1.95)
                    finalFilter.insertAtLast(temp.record);
                break;
            case "documents":
                Documents d = (Documents) subject;
                float isScore = match.compare(d.issueAuthority,temp.record.get("issueauthority"));
                if(isScore>0.65)
                    finalFilter.insertAtLast(temp.record);
                break;
            }
            temp=temp.next;
        }
        return finalFilter;
    }

    public boolean exists() {
       return first!=null;
    }

    public void setAdminVerification(int LostUserid,Item subject) {
        //History and flagging
        //Photo and metadata
    }

    class Node{
        Node prev;
        Node next;
        HashMap<String,String> record;
        Node(HashMap<String,String> a){
            record = a;
            prev=null;
            next=null;
        }
    }
    Node first = null;
    public  void insertAtLast(HashMap<String,String> a){
        Node n = new Node(a);
        if(first==null)
            first=n;
        else{
            Node temp = first;
            while (temp.next!=null){
                temp=temp.next;
            }
            temp.next=n;
            n.prev=temp;
        }
    }
    public void display(){
        Node temp = first;
        System.out.println(first);
        while (temp!=null){
            System.out.println(temp.record);
            temp=temp.next;
        }
    }
}
