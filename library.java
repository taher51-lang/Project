import java.util.Scanner;
   public class library {
        Scanner sc = new Scanner(System.in);
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
    class Librarian {
    }
    class Books extends library  {
        String bookName,bookauhtor,Genre;
        int bookId;
        static int id=1,defaultStock = 5;
        int current_Stock = defaultStock;
        Books(int k){}
        Books() {
            bookId=id++;
        }
        void displayBooks(Books array[],String genre){
            for(int i=0;i< array.length;i++){
                if(genre.equalsIgnoreCase(array[i].Genre)){
                System.out.print(array[i].bookName+" "+array[i].bookId);
                System.out.println(array[i].bookauhtor);}
            }
            System.out.println("Kindly enter the book id You want to read");
            byte userBookId = sc.nextByte();
            if(availibility(userBookId,array)){
                System.out.println("This Book has been issued to you");
            }
        }
        boolean availibility(int userinput,Books array[]){//Stock availibilty
            if(array[userinput-1].current_Stock>0){
                array[userinput-1].current_Stock-=1;
                return true;
            }
            else
                return false;
        }
    }
    class Members extends library{
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
            Books books[] = new Books[2];
            Books callBook = new Books(1);
            for (int i = 0; i < books.length; i++) {//provides the array of books
                books[i] = new Books();
            }
            Members members[] = new Members[1];//Provides the array of members
            for (int i = 0; i < members.length; i++) {
                members[i] = new Members();
            }
            System.out.println("Type Reader if you are a reader and librarian if you are a librarian");
            String userType = sc.nextLine();
            int type = userType.equalsIgnoreCase("Reader") ? 1 : 2;
            switch (type) {
                case 1:
                    System.out.println("Are you an existing member of library?Yes/No");
                    String userClass = sc.nextLine();
                    if(userClass.equalsIgnoreCase("Yes")) {
                        int attempt = 0;
                        boolean loginStat = false;
                        library callobj = new library();
                        while (true) {//Login
                            System.out.println("Enter Your name , pass and id respectively");
                            if (callobj.login(sc.nextLine(), sc.nextLine(), members[sc.nextInt() - 1])) {
                                System.out.println("Login Success");
                                loginStat = true;
                                break;
                            } else if (attempt == 3) {
                                System.out.println("Your account has been Locked");
                                break;
                            } else {
                                attempt++;
                            }
                        }
                        if (loginStat) {
                            System.out.println("Select your preferred Genre of Books:");
                            System.out.println("1.Fiction\n2.Horror\nPhysocological");
                            byte userGenre = sc.nextByte();
                            switch (userGenre){
                                case 1:callBook.displayBooks(books,"Fiction");break;
                                case 2:callBook.displayBooks(books,"Horror");break;
                                case 3:callBook.displayBooks(books,"Physocological");break;
                            }
                            System.out.println("Happy Reading");
                            callBook.displayBooks(books,"Fiction");
                            //code continues here;
                        }
                    }
                    else {//registering a new member
                        System.out.println("We will be glad to have you as our member.");
                        System.out.println("Please enter details below for registering as a new Member");
                    }
                    break;//Member operations end
            case 2 :

            }
        }
    }






