package Main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Admin {
    public String username_admin;
    public String name_admin;
    public String email_admin;
    public String password;
    public Long contactno;
    private final String systempass = "admin123";
    Connection con;
    Scanner sc = new Scanner(System.in);
    Admin() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "");
    }
    public void startProcedure() throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        boolean loginCheck = true;

        while (loginCheck) {
            System.out.println("Are you a new admin or a registered admin?");
            System.out.print("Type 'new' or 'registered': ");
            String choice = sc.nextLine().trim().toLowerCase();

            switch (choice) {
                case "new":
                    System.out.print("Enter default system password: ");
                    String inputPass = sc.nextLine();

                    if (inputPass.equals(systempass)) {
                        System.out.println("System password verified. Proceeding with registration...");
                        adminregistration();
                        loginCheck = false;
                        selectFromChoice();
                    } else {
                        System.out.println("Incorrect system password. Access denied.");
                    }

                    break;

                case "registered":
                    while (true) {
                        boolean success = login();
                        if (success) {
                            System.out.println("Welcome back, admin.");
                            loginCheck = false;
                            selectFromChoice();
                            break;
                        } else {
                            System.out.println("Login failed. Try again.");
                        }
                    }
                    break;

                default:
                    System.out.println("Invalid input. Please choose 'new' or 'registered'.");
            }
        }
    }

    void selectFromChoice() throws SQLException, IOException {
        System.out.println("Enter your choice from the following");
        System.out.println("1.Verify and Analyse requests\n2.Exit");
        int choice = 0;
        boolean param = true;
        while (param) {
            try {
                choice = sc.nextInt();
            }
            catch (Exception e){
                System.out.println("Enter numeric input");
                continue;
            }
            switch (choice){
                case 1:verificationProcess();
                    break;
                case 2: System.exit(0);break;
                default:
                    System.out.println("Enter No between 1 and 2");
                    continue;
            }
            param=false;
        }
    }
    public void verificationProcess() throws SQLException, IOException {
        System.out.println("Match The attributes of Lost User and Found User and make your decision");
        String sql ="select * from adminverification where status = 'Pending'";
        Statement st = con.createStatement();
        ResultSet rs5 = st.executeQuery(sql);
        ResultSetMetaData rsmd = rs5.getMetaData();
        int lostId,foundId,lostUserId,foundUserId;
        while (rs5.next()) {
            lostId = rs5.getInt(3);
            foundId = rs5.getInt(5);
            lostUserId = rs5.getInt(2);
            foundUserId = rs5.getInt(4);
            System.out.println(rsmd.getColumnName(2) +"  "+rsmd.getColumnName(3)+"  "+rsmd.getColumnName(4)+"  "+rsmd.getColumnName(4));
            System.out.println(lostUserId+"  "+lostId+"  "+foundUserId+"  "+foundId);
            System.out.println("Do you want to verify this request?Enter yes if you wish to proceed else press any key for other requests");
            String circumstance = sc.next();
            if (circumstance.equalsIgnoreCase("yes")) {
                Statement st1 = con.createStatement();
                Statement st2 = con.createStatement();
                String sql1 = "select attribute,image from lostandfound where id = " + lostId;
                String sql2 = "select attribute,image from lostandfound where id = " + foundId;
                ResultSet rs1 = st1.executeQuery(sql1);
                ResultSet rs2 = st2.executeQuery(sql2);
                while (rs1.next() && rs2.next()) {
                    System.out.println("These are the details given by user who has lost the item:");
                    System.out.println(rs1.getString(1));
                    System.out.println("These are the details given by user who has found the item:");
                    System.out.println(rs2.getString(1));
                    Blob i1 = rs1.getBlob(2);
                    Blob  i2= rs1.getBlob(2);
                    if(i1!=null&&i2!=null){
                        byte[] arr1 = i1.getBytes(1, (int) i1.length());
                        byte[] arr2 = i2.getBytes(1, (int) i2.length());
                        FileOutputStream fos1 = new FileOutputStream("C:\\Users\\rangw\\OneDrive\\Pictures\\Screenshots\\userlost");
                        FileOutputStream fos2 = new FileOutputStream("C:\\Users\\rangw\\OneDrive\\Pictures\\Screenshots\\userfound");
                        fos1.write(arr1);
                        fos2.write(arr2);
                        fos1.close();
                        fos2.close();
                        System.out.println("Images from both the users has been downloaded. Please analyse both");
                    }
                    System.out.println("Kindly make a decision. Press 1 to verify and 2 for denial ");
                    int choice;
                    while (true) {
                        try {
                            choice = sc.nextInt();
                            break;
                        } catch (Exception e) {
                            System.out.println("Improper input. Try again");
                        }
                    }
                    if (choice == 1) {
                        PreparedStatement pst1 = con.prepareStatement("update lostandfound set status = 'Verified' where id = " + lostId);
                        PreparedStatement pst2 = con.prepareStatement("update lostandfound set status = 'Verified' where id = " + foundId);
                        pst2.executeUpdate();
                        pst1.executeUpdate();
                        CallableStatement cst = con.prepareCall("call afterVerification(?,?)");
                        cst.setInt(1,lostUserId);
                        cst.setInt(2,foundUserId);
                        cst.executeUpdate();
                    }
                    else{
                        CallableStatement cst = con.prepareCall("call afterRejection(?,?)");
                        cst.setInt(1,lostUserId);
                        cst.setInt(2,foundUserId);
                        cst.executeUpdate();
                    }
                }
            }

        }
    }
    public void adminregistration() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter username: ");
        username_admin = sc.nextLine();

        System.out.print("Enter name: ");
        name_admin = sc.nextLine();

        System.out.print("Enter email: ");
        email_admin = sc.nextLine();

        System.out.print("Enter password: ");
        password = sc.nextLine();

        System.out.print("Enter contact number: ");
        contactno = sc.nextLong();

        String sql = "INSERT INTO admin (username_admin, name_admin, email_admin, password, contactno) VALUES ('"
                + username_admin + "', '"
                + name_admin + "', '"
                + email_admin + "', '"
                + password + "', "
                + contactno + ")";

        try {
            Statement stmt = con.createStatement();
            int rowsInserted = stmt.executeUpdate(sql);

            if (rowsInserted > 0) {
                System.out.println("Main.Admin registered successfully.");
            } else {
                System.out.println("Registration failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }
    public boolean login() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter name: ");
        String inputName = sc.nextLine();

        System.out.print("Enter password: ");
        String inputPassword = sc.nextLine();

        String sql = "SELECT * FROM admin WHERE name_admin = ? AND password = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, inputName);
            pstmt.setString(2, inputPassword);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Login successful.");
                return true;
            } else {
                System.out.println("Invalid name or password.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }
}