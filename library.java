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
        int bookId=0;
        static int id=1,defaultStock = 5;
        int current_Stock = defaultStock;
        Books(int k){}
        Books() {
            bookId=id++;
        }
        void displayBooks(Books array[],String genre){
            for(int i=0;i< array.length;i++){
                if(genre.equalsIgnoreCase(array[i].Genre)){
                System.out.print(array[i].bookId+"."+array[i].bookName);
                System.out.println(" "+"Author: "+array[i].bookauhtor);}
            }
        }
        boolean MaxPossesion(int id,Members array[]){
            if(array[id-1].bookInPossesion[2].equals("")&&array[id-1].bookcounter<=3){
                return true;
            }
            else {
                System.out.println("You have Maximum no of books. Kindly return a book then proceed");
                return false;
            }
        }
        boolean availibility(Books array[],Members array2[],int memId,byte userBookId){//Stock availibilty
            if(array[userBookId-1].current_Stock>0&&MaxPossesion(memId,array2)){
                int k = array2[memId-1].bookcounter;
                array[userBookId-1].current_Stock-=1;
                array2[memId-1].bookInPossesion[k]=array[userBookId-1].bookName;
                array2[memId-1].bookId[k]=userBookId;
                array2[memId-1].bookcounter++;
                return true;
            }
            else
                return false;
        }
    }
    class Members extends library{
        Scanner sc = new Scanner(System.in);
        String name,defaultpass;
        String bookInPossesion[] = {"","",""};
        int bookId[] = new int[3];int bookcounter=0;
        Members(int i){}
        Members(){
            System.out.println("Enter Name");
            name = sc.nextLine();defaultpass="1";
        }
        void newMember(Members array[],String name,String pass){
            int counter;
            for(int i =0;i< array.length;i++){
                if(array[i].name.isEmpty()){
                    array[i].name=name;
                    array[i].defaultpass=pass; System.out.println("Your Id is "+i);break;
                }
                else
                    if(i+1==array.length)
                        System.out.println("There are no enough seats left");
            }
        }
        void dispayandReturn(Books array[],Members memarray[],byte id){
            System.out.println("You currently have These books:");
            for(int i =0;i<3;i++){
                System.out.print((i+1)+". Name of Book: "+memarray[id-1].bookInPossesion[i]);
                System.out.println(" Id of book is "+memarray[id-1].bookId[i]);
            }
            System.out.println(" Would You like to return any of these books?");
            String s = sc.nextLine();
            if(s.equalsIgnoreCase("yes")){
                System.out.println("Enter the ID and serial no of book you want to return");
                byte UserBook = sc.nextByte();//insert validation!!
                byte serialno = sc.nextByte();
                array[UserBook-1].current_Stock++;
                memarray[id-1].bookInPossesion[serialno-1]="";
                memarray[id-1].bookId[serialno-1]=0;
                System.out.println("Your book has been Succesfully returned");//Validate
                // Id of book
            }


        }
    }
    class Run123 {

           static byte userBookId=0;
        public static void main(String args[]) {

            Run123 callRun = new Run123();
            Scanner sc = new Scanner(System.in);
            Books books[] = new Books[50];
            Books callBook = new Books(1);
            Members callObj = new Members(2);
            for (int i = 0; i < books.length; i++) {//provides the array of books
                books[i] = new Books();
            }
            String[] horrorBooks = {"The Shining", "Dracula", "Frankenstein", "Pet Sematary", "The Haunting of Hill House", "It", "Bird Box", "The Exorcist", "Salem's Lot", "The Silence of the Lambs"};
            String[] horrorAuthors = {"Stephen King", "Bram Stoker", "Mary Shelley", "Stephen King", "Shirley Jackson", "Stephen King", "Josh Malerman", "William Peter Blatty", "Stephen King", "Thomas Harris"};
            String[] fictionBooks = {"To Kill a Mockingbird", "1984", "Pride and Prejudice", "The Great Gatsby", "Moby Dick", "War and Peace", "Crime and Punishment", "The Catcher in the Rye", "Brave New World", "The Lord of the Rings"};
            String[] fictionAuthors = {"Harper Lee", "George Orwell", "Jane Austen", "F. Scott Fitzgerald", "Herman Melville", "Leo Tolstoy", "Fyodor Dostoevsky", "J.D. Salinger", "Aldous Huxley", "J.R.R. Tolkien"};
            String[] psychologicalBooks = {"Gone Girl", "The Girl on the Train", "The Silent Patient", "Sharp Objects", "Shutter Island", "The Woman in the Window", "Before I Go to Sleep", "The Girl Before", "Big Little Lies", "The Couple Next Door"};
            String[] psychologicalAuthors = {"Gillian Flynn", "Paula Hawkins", "Alex Michaelides", "Gillian Flynn", "Dennis Lehane", "A.J. Finn", "S.J. Watson", "JP Delaney", "Liane Moriarty", "Shari Lapena"};
            String[] businessCaseStudiesBooks = {
                    "Delivering Happiness: A Path to Profits, Passion, and Purpose",
                    "No Rules Rules: Netflix and the Culture of Reinvention",
                    "How Google Works",
                    "Joy, Inc.: How We Built a Workplace People Love",
                    "Instant: The Story of Polaroid",
                    "Dealers of Lightning: Xerox PARC and the Dawn of the Computer Age",
                    "Skunk Works: A Personal Memoir of My Years at Lockheed",
                    "Loonshots: How to Nurture the Crazy Ideas That Win Wars, Cure Diseases, and Transform Industries",
                    "Troublemakers: Silicon Valley's Coming of Age",
                    "Playing to Win: How Strategy Really Works"
            };
            String[] businessCaseStudiesAuthors = {
                    "Tony Hsieh",
                    "Reed Hastings",
                    "Eric Schmidt",
                    "Richard Sheridan",
                    "Christopher Bonanos",
                    "Michael A. Hiltzik",
                    "Ben R. Rich",
                    "Safi Bahcall",
                    "Leslie Berlin",
                    "A.G. Lafley and Roger Martin"
            };

            for(int i=0;i<10;i++){
                books[i].bookName=fictionBooks[i];
                books[i].Genre="Fiction";
                books[i].bookauhtor=fictionAuthors[i];
            }for(int i=10,k=0;i<20;i++,k++){
                books[i].bookName=psychologicalBooks[k];
                books[i].Genre="Physocological";
                books[i].bookauhtor=psychologicalAuthors[k];
            }for(int i=20,k=0;i<30;i++,k++){
                books[i].bookName=horrorBooks[k];
                books[i].Genre="Horror";
                books[i].bookauhtor=horrorAuthors[k];
            }for(int i=20,k=0;i<30;i++,k++){
                books[i].bookName=businessCaseStudiesBooks[k];
                books[i].Genre="Business Case Studies";
                books[i].bookauhtor=businessCaseStudiesAuthors[k];
            }
            Members members[] = new Members[2];//Provides the array of members
            for (int i = 0; i < members.length; i++) {
                members[i] = new Members();
            }
            System.out.println("Type Reader if you are a reader and librarian if you are a librarian");
            String userType = sc.nextLine();
            int type = userType.equalsIgnoreCase("Reader") ? 1 : 2;
            while(true){
            switch (type) {
                case 1:
                    System.out.println("Are you an existing member of library?Yes/No");
                    sc.nextLine();
                    String userClass = sc.nextLine();
                    if (userClass.equalsIgnoreCase("Yes")) {
                        int attempt = 0;
                        boolean loginStat = false;
                        library callobj = new library();
                        System.out.println("Enter your id");
                        byte memId = sc.nextByte();
                        sc.nextLine();
                        while (true) {//Login
                            System.out.println("Enter your name and pass respectively");
                            String memName = sc.nextLine();
                            String pass = sc.nextLine();
                            if (callobj.login(memName, pass, members[memId - 1])) {
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
                            System.out.println("1.Fiction\n2.Horror\n.3." +
                                    "Physocological");
                            byte userGenre = sc.nextByte();
                            switch (userGenre) {
                                case 1:
                                    callBook.displayBooks(books, "Fiction");
                                    System.out.println("Kindly enter the book id You want to read");
                                     userBookId = sc.nextByte();//Code to be improvised.!!Add bookname;
                                    System.out.println(userBookId);
                                    if (callBook.availibility(books, members, memId,userBookId)) {
                                        System.out.println("Book has been Issued to you");
                                    }
                                    break;
                                case 2:
                                    callBook.displayBooks(books, "Horror");
                                    System.out.println("Kindly enter the book id You want to read");
                                    userBookId = sc.nextByte();
                                    if (callBook.availibility(books, members, memId,userBookId)) {
                                        System.out.println("Book has been Issued to you");
                                    }
                                    break;
                                case 3:
                                    callBook.displayBooks(books, "Physocological");
                                    System.out.println("Kindly enter the book id You want to read");
                                    userBookId = sc.nextByte();
                                    if (callBook.availibility(books, members, memId,userBookId)) {
                                        System.out.println("Book has been Issued to you");
                                    }
                                    break;
                                case 4:
                                    callBook.displayBooks(books, "Buisness Case Studies");
                                    System.out.println("Kindly enter the book id You want to read");
                                    userBookId = sc.nextByte();
                                    if (callBook.availibility(books, members, memId,userBookId)) {
                                        System.out.println("Book has been Issued to you");
                                    }
                            }

                            System.out.println("Happy Reading");
                            callObj.dispayandReturn(books,members,memId);
                            System.out.println("do you Want to continue Reading Books?Yes/No");
                            String input1 = sc.nextLine();
                            if(input1.equalsIgnoreCase("yes")){continue;}
                            else break;
                            //code continues here;
                        }
                    } else {//registering a new member
                        System.out.println("We will be glad to have you as our member.");
                        System.out.println("Please enter details below for registering as a new Member");
                        System.out.println("Enter Your Name and Password");
                        String name = sc.nextLine();
                        String pass = sc.nextLine();
                        callObj.newMember(members, name, pass);

                        continue;
                    }
                    break;
                    //Member operations end
                case 2:
            }

            }
        }
    }






