package Main;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import org.mindrot.jbcrypt.BCrypt;
public class Main {
    static Connection con;
    static Scanner sc = new Scanner(System.in);
    static Item lost;
    static int id;
    static int Lid;
    static boolean roleIdentification() throws SQLException, ClassNotFoundException, FileNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        boolean adminOrUser = true;
        System.out.println("---Kindly choose your specific role");
        System.out.println("---1.User");
        System.out.println("---2.Admin");
        int role = 0;
        boolean condition1 =true;
        while (condition1) {
            try {
                role = Integer.parseInt(sc.next());
                condition1=false;
            }
            catch (NumberFormatException e) {
                System.out.println("---Kindly enter proper numeric input---");
            }
        }
        boolean condition2 = true;
        while (condition2) {
            switch (role) {
                case 1:
                    condition2=false;
                    System.out.println("Enter 1 if you are a Registered User and 2 if you are a new user");
                    boolean temp = true;
                    while (temp) {
                        try {
                            role = Integer.parseInt(sc.next());
                        } catch (NumberFormatException e) {
                            System.out.println("Enter numeric input");
                            continue;
                        }
                        switch (role) {
                            case 1:
                                while (!login()) {
                                    System.out.println("Incorrect userName or password");
                                }
                                LostAndFoundProcess();
                                temp = false;
                                break;
                            case 2:
                                User.registration();
                                LostAndFoundProcess();
                                temp = false;
                                break;
                            default:
                                System.out.println("Enter proper digit");
                                break;
                        }
                    }
                    break;
                case 2:
                    condition2=false;
                    adminOrUser = false;
                    break;
                default:
                    System.out.println("Enter 1 or 2 in accordance to your role");
                    boolean temp2 = true;
                    while (temp2) {
                        try {
                            role = Integer.parseInt(sc.next());
                            temp2=false;
                        } catch (NumberFormatException e) {
                            System.out.println("Enter numeric input");
                        }
                    }
                    break;
            }
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
        String sql="Select userId,name,user_pass from user where user_Name='"+userName+"'";
        Statement st= con.createStatement();
        ResultSet rs= st.executeQuery(sql);
        if(rs.next()){
            id = rs.getInt(1);
            String storedHash = rs.getString(3);
            if (BCrypt.checkpw(pass, storedHash)) {
                System.out.println("Hi "+rs.getString(2)+"!!");
                System.out.println("you have logged in successfully!");
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    static void LostAndFoundProcess() throws SQLException, FileNotFoundException {
        System.out.println("Enter your choice from the following :");
        System.out.println("1.Report lost item\n2.Report found item\n3.See Rewards\n4.See admin response\n5.See Recovery rate\n6.See average Recovery time\n7.Lost hotspot areas\n8.Exit");
        boolean checkInput = true;
        int choice = 0;
        while (checkInput) {
            try {
                choice = sc.nextInt();
            } catch (Exception e) {
                System.out.println("Enter numeric input");
                sc.next();
                continue;
            }
            checkInput = false;

            switch (choice) {
                case 1:
                    Item subject = Lost();
                    CallableStatement cs =con.prepareCall("call flagSuspiciousUser(?)");
                    cs.setInt(1,id);
                    cs.executeUpdate();
                    DoublyLinkedList stage1 = search(subject);
                    if (stage1 == null) {
                        System.out.println("No such Item detected. Kindly login again to proceed");
                        System.exit(0);
                    }

//                    stage1.display();
                    DoublyLinkedList stage2 = filterName_location_color(stage1, subject);
//                    stage2.display();//Name and location and color
                    DoublyLinkedList stage3 = filterAdditionals(stage2, subject);
//                    stage3.display();
                    if (stage3.first!=null) {
                        stage3.display();
                        System.out.println("We have found a Match in accordance to your Specifications.");
                        System.out.println("Your request is in Admin verification and you may get contacted for more details via email.");
                        System.out.println("The whole process might take upto a week to complete.");
                        stage3.setAdminVerification(Lid, id);
                        addBalance();
                    } else {
                        System.out.println("No such item has been found");
                    }
                    break;
                case 2:
                    found();
                    break;
                case 3:
                    seeRewards(id);
                    break;
                case 4:
                    seeAdminResponse(id);
                    break;
                case 5:
                    RecoveryRate();
                    break;
                case 6:
                    RecoveryTime();
                    break;
                case 7:
                    lossHotspot();
                    break;
                case 8:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Enter between 1 to 8");
                    checkInput=true;
                    break;
            }
        }
    }

    public static void addBalance() throws SQLException {
        System.out.println("Kindly add a Minimum of Rs 250. This will be deducted and given as reward to the person who found and took care of your lost item");
        int funds = sc.nextInt();
        PreparedStatement pst = con.prepareStatement("update user set Wallet = ? where userid = ? ");
        pst.setInt(1,funds);
        pst.setInt(2,id);
        pst.executeUpdate();
    }

    public static void RecoveryTime() throws SQLException {
        CallableStatement cs = con.prepareCall("{call avgRecoveryTime(?)}");
        cs.registerOutParameter(1, java.sql.Types.INTEGER); // Register OUT param
        cs.execute();
        int avg = cs.getInt(1); // Retrieve OUT param
        System.out.println("Average recovery time: " + avg);// Now this should work

    }


    public static void seeAdminResponse(int id) throws SQLException {
        System.out.println("These are the responses from admin on all your requests");
        PreparedStatement pst = con.prepareStatement("select adminresponse from lostandfound where uid = "+id);
        ResultSet rs = pst.executeQuery();
        while (rs.next()){
            System.out.println(rs.getString(1));
        }
    }

    public static void seeRewards(int id) throws SQLException {
        PreparedStatement pst = con.prepareStatement("select rewards from user where userId = "+id);
        ResultSet rs = pst.executeQuery();
        while (rs.next())
            System.out.println(rs.getString(1));
    }

    public static DoublyLinkedList filterAdditionals(DoublyLinkedList stage3, Item subject) {
        String result = getSubjectInstance(subject);
        System.out.println(result);
        return stage3.filterMoreAttributes(subject,result);
    }

     public static int insertDetails(Item e,String status) throws SQLException, FileNotFoundException {
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        String sql = "INSERT INTO Lostandfound(uid, name, area, date, color, attribute, status, plocation,image) VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
         String location[] = e.area.split(",");
            ps.setInt(1, id);
            ps.setString(2, e.name);
            ps.setString(3, e.area);
            ps.setDate(4, Date.valueOf(e.date));
            ps.setString(5, e.color);
            ps.setString(6, e.attribute);
            ps.setString(7, status);
            ps.setString(8, location[location.length-1]);
            System.out.println("Do you have an image of the subject?Enter yes for uploading else press any key.");
            String imageChoice = sc.next();
            sc.nextLine();
            if(imageChoice.equalsIgnoreCase("yes")){
                System.out.println("Enter the file path");
                try{
                ps.setBlob(9,new FileInputStream(sc.nextLine()));
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            else{
                ps.setBlob(9, (Blob) null);
            }
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
                   "(Mobile phones, tablets, laptops, cameras, chargers, earphones etc)\n"+

                "2. Bags & Carriers"+
                   "(Suitcases, backpacks, laptop bags, tote bags)\n"+

               "3. Clothing & Accessories"+
                  " (Jackets, scarves, gloves, caps, jewelry, watches)\n"+

                "4. Identity & Documents "+
                   "(ID cards, passports, licenses, student cards, certificates)\n"+

                "5. Academic Supplies"+
                   "(Books, notebooks, stationery, calculators)\n"+

                "6. Hobby & Entertainment Gear"+
                 "  (Game consoles, musical instruments, sports equipment)\n"+

                "7. Children’s Belongings"+
                  " (Toys, lunch boxes, baby bags, pacifiers)\n"+

                "8. Eyewear & Vision Aids"+
                   "(Glasses, sunglasses, contact lens kits)\n"+

                "9. Main.Keys & Access Devices"+
                   "(House keys, car keys, RFID tags, access cards)\n");


    }
    static Item Lost() throws SQLException, FileNotFoundException {
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
                int lid1 = insertDetails(e,"lost");
                Lid=lid1;//Last generated id
                PreparedStatement pst1 = con.prepareStatement("insert into electronicsandgadgets values(?,?,?)");
                pst1.setInt(1,lid1);
                pst1.setString(2,e.brand);
                pst1.setString(3,e.model);
                pst1.executeUpdate();
            lost= e;break;
            case 2:Bags a = new Bags();
                a.setDetails();
                int lid2 = insertDetails(a,"lost");//Last generated id
                Lid=lid2;
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
            Lid=lid3;
            PreparedStatement pst3 = con.prepareStatement("insert into accessories values(?,?)");
            pst3.setInt(1,lid3);
            pst3.setString(2,c.brand);
            pst3.executeUpdate();
            lost=c;
            break;
            case 4:Documents d = new Documents();
            d.setDetails();
            int lid4 = insertDetails(d,"lost");
            Lid=lid4;
            PreparedStatement pst4 = con.prepareStatement("insert into Documents values(?,?)");
            pst4.setInt(1,lid4);
            pst4.setString(2,d.issueAuthority);
            pst4.executeUpdate();
            lost=d;
            break;
            case 5:AcademicSupplies s = new AcademicSupplies();
            s.setDetails();
                int lid5 = insertDetails(s,"lost");
                Lid=lid5;
                PreparedStatement pst5 = con.prepareStatement("insert into AcademicSupplies values(?,?)");
                pst5.setInt(1,lid5);
                pst5.setString(2,s.brand);
                pst5.executeUpdate();
            lost=s;
            break;
            case 6:EntertainmentsGears eg = new EntertainmentsGears();
            eg.setDetails();
                int lid6 = insertDetails(eg,"lost");
                Lid=lid6;
                PreparedStatement pst6 = con.prepareStatement("insert into entertainmentgears values(?,?)");
                pst6.setInt(1,lid6);
                pst6.setString(2,eg.brand);
                pst6.executeUpdate();
            lost=eg;
            break;
            case 7:ChildStuff cs = new ChildStuff();
            cs.setDetails();
                int lid7 = insertDetails(cs,"lost");
                Lid=lid7;
                PreparedStatement pst7 = con.prepareStatement("insert into Childstuff values(?,?)");
                pst7.setInt(1,lid7);
                pst7.setString(2,cs.brand);
                pst7.executeUpdate();
            lost = cs;
            break;
            case 8:eyeAndVision ev = new eyeAndVision();
            ev.setDetails();
                int lid8 = insertDetails(ev,"lost");
                Lid=lid8;
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
                Lid=lid9;
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
    public static void RecoveryRate() throws SQLException {
        String sql1= "select count(*) from lostandfound";
        String sql2 = "select count(*) from lostandfound where status ='Verified'";
        Statement st1 = con.createStatement();
        Statement st2 = con.createStatement();
        ResultSet rs1 =  st1.executeQuery(sql1);
        ResultSet rs2 =  st2.executeQuery(sql2);
        float percent=0;
        while (rs1.next()&& rs2.next()){
            percent= (float) rs2.getInt(1) /rs1.getInt(1)*100f;
        }
        System.out.println("The chances of getting a lost item back using this system are "+percent+"%");
    }

    public static void lossHotspot() throws SQLException {
        String sql = "select plocation,count(id) from lostandfound group by plocation order by count(id) desc";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        System.out.println("Location     No of lost Items");
        while (rs.next()){
            System.out.print(rs.getString(1)+"----->");
            System.out.println(rs.getString(2));
        }
    }
    static void found() throws SQLException, FileNotFoundException {
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
                PreparedStatement pst9 = con.prepareStatement("insert into Keys values(?,?,?)");
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
        System.out.println("Thanks for reporting The lost Item. We are thankful for your support. You will soon be rewarded for your efforts");
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
                case "Main.Electronics":
                    DoublyLinkedList list1 =  new DoublyLinkedList();
                    String sql = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
                case "Main.Bags":
                    System.out.println("s2 cleared");
                    DoublyLinkedList list2=  new DoublyLinkedList();
                    String sql2 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
                case "Main.Accessories":
                    DoublyLinkedList list3 =  new DoublyLinkedList();
                    String sql3 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
                case "Main.AcademicSupplies":
                    DoublyLinkedList list4 =  new DoublyLinkedList();
                    String sql4 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
                case "Main.Documents":
                    DoublyLinkedList list5 =  new DoublyLinkedList();
                    String sql5 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
                case "Main.ChildStuff":
                    DoublyLinkedList list6 =  new DoublyLinkedList();
                    String sql6 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
                            "    l.name, \n" +
                            "    l.area, \n" +
                            "    l.date, \n" +
                            "    l.color, \n" +
                            "    l.attribute, \n" +
                            "    e.Brand\n" +
                            "FROM \n" +
                            "    lostAndfound l\n" +
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
                case "Main.EntertainmentGears":
                    DoublyLinkedList list7 =  new DoublyLinkedList();
                    String sql7 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
                case "Main.eyeAndVision":
                    DoublyLinkedList list8 =  new DoublyLinkedList();
                    String sql8 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
                case "Main.Keys":
                    DoublyLinkedList list9 =  new DoublyLinkedList();
                    String sql9 = "SELECT \n" +
                            "    l.uid, \n" +
                            "    l.id,  \n"+
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
            System.out.println("Thanks for Using our System");
        }
        else{
            Admin a = new Admin();
            a.startProcedure();
        }
    }
}


