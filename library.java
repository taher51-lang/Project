import java.util.Scanner;

public class library {}
    class Main12 {
        boolean login(String username, String pass, Members obj){
            if(username.equalsIgnoreCase(obj.name)&&pass.equals(obj.defaultpass)){
                return true;
            }
            else{
                System.out.println("Enter correct Username/Password");
                return false;
            }
        }
    }
    
    class Reader {

    }
    class Books {
        Scanner sc = new Scanner(System.in);
        String bookName, bookauhtor;
        int bookPrice;
        Books() {
            bookName = sc.nextLine();
            bookauhtor = sc.nextLine();
            bookPrice = sc.nextInt();
            sc.nextLine();
        }
    }
    class Members {
        Scanner sc = new Scanner(System.in);
        String name,defaultpass;
        int penalty,id;
        Members(){
            System.out.println("Enter Name");
            name = sc.nextLine();defaultpass="1";
        }
    }

    class Run123 {
        public static void main(String args[]) {
            Scanner sc = new Scanner(System.in);
            Books books[] = new Books[10];
            for (int i = 0; i < books.length; i++) {
                //books[i] = new Books();
            }
            Members members[] = new Members[1];
            for(int i =0;i< members.length;i++){
                members[i] = new Members();
            }
            int attempt=0;boolean loginStat = false;
            Main12 callobj = new Main12();
            while(true) {
                System.out.println("Enter Your name , pass and id respectively");
                if (callobj.login(sc.nextLine(), sc.nextLine(), members[sc.nextInt() - 1])) {
                    System.out.println("Login Success");loginStat=true;break;
                }
                else if (attempt==3) {
                    System.out.println("Your account has been Locked");
                    break;
                }
                else {attempt++;
                    continue;
                }
            }
            if(loginStat){
                //code continues here;
            }
        }
    }






