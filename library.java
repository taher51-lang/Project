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
    }//Add Default Member names and Design the code properly!!
    class Librarian extends library{
       String defaultpass = "admin123";
       String name = "admin";
       boolean LibLogin(String name,String pass){
           if(name.equalsIgnoreCase(this.name)&&defaultpass.equals(pass)){
               return true;
           }
           else
               return false;
       }
       void displayMemberDetails(Members obj){
           System.out.println("Name of member: "+obj.name);
           System.out.println("Currently Holds "+obj.bookcounter+" books");
           for(int i =0;i<3;i++){
               System.out.println(obj.bookInPossession[i]+" BookId :"+obj.bookId[i]);
           }
       }
       void RemoveBook(Books array[]){
           System.out.println("Enter The Book id You want to remove");
           displayBooks(array);
           byte k = sc.nextByte();
           array[k-1].current_Stock=0;
       }
        void displayBooks(Books array[]){
            for(int i=0;i< array.length;i++){
                    System.out.print(array[i].bookId+"."+array[i].bookName);
                    System.out.println("--"+"Author: "+array[i].bookauhtor);}

        }
       void reviewAndUpdate(Books book[]){
           displayBooks(book);
           System.out.println("Enter The Book Id you want to see/update");
           short k = sc.nextShort();
           System.out.println(book[k-1].bookName+"-- Genre"+book[k-1].Genre+" --Current Stock "+book[k-1].current_Stock);
           System.out.println("Enter The updated Stock ");
           book[k-1].current_Stock= sc.nextByte();
       }
       void addBook(Books book[]){
           System.out.println("Enter The name,genre,author,stock of book respectively");
           for(int i=0;i<book.length;i++){
               if(book[i].bookName==null){
                   book[i].bookName=sc.nextLine();
                   book[i].Genre=sc.nextLine();
                   book[i].bookauhtor=sc.nextLine();
                   book[i].current_Stock=sc.nextByte();break;
               }
           }
       }
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
        boolean MaxPossession(int id,Members array[]){
            if(array[id-1].bookInPossession[2].equals("")&&array[id-1].bookcounter<=3){
                return true;//insert a delete method SOMEHOW!!
            }
            else {
                System.out.println("You have Maximum no of books. Kindly return a book then proceed");
                return false;
            }
        }
        boolean availibility(Books array[],Members array2[],int memId,byte userBookId){//Stock availibilty
            if(array[userBookId-1].current_Stock>0&&MaxPossession(memId,array2)){
                int k = array2[memId-1].bookcounter;
                array[userBookId-1].current_Stock-=1;
                array2[memId-1].bookInPossession[k]=array[userBookId-1].bookName;
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
        String bookInPossession[] = {"","",""};
        int bookId[] = new int[3];int bookcounter=0;
        Members(){
            name="";
        }
        void newMember(Members array[],String name,String pass){
            int counter;
            for(int i =0;i< array.length;i++){
                if(array[i].name.equals("")){
                    array[i].name=name;
                    array[i].defaultpass=pass; System.out.println("Your Id is "+(i+1));break;
                }
                else
                    if(i+1==array.length)
                        System.out.println("There are no enough seats left");
            }
        }
        void displayandReturn(Books array[],Members memarray[],byte id){
            System.out.println("You currently have These books:");
            for(int i =0;i<3;i++){
                if(memarray[id-1].bookId[i]!=0){
                System.out.print((i+1)+". Name of Book: "+memarray[id-1].bookInPossession[i]);
                System.out.println(" Id of book is "+memarray[id-1].bookId[i]);}
            }
            System.out.println("---Would You like to return any of these books? If yes Type 1, anything else if you don't want so---");
            String s = sc.nextLine();
            if(s.equalsIgnoreCase("1")){
                System.out.println("---Enter the ID and serial no of book you want to return--");
                byte UserBook = sc.nextByte();// validation!!
                byte serialno = sc.nextByte();
                if(memarray[id-1].validate(UserBook)){
                array[UserBook-1].current_Stock++;
                memarray[id-1].bookInPossession[serialno-1]="";
                memarray[id-1].bookId[serialno-1]=0;
                System.out.println("-Your book has been Succesfully returned-");
                memarray[id-1].bookcounter--;}
                else
                    System.out.println("You do not have such book");

            }
        }
        boolean validate(byte b){
            boolean a = true;
            for(int i =0;i< bookId.length;i++){
                if(bookId[i]==b){
                    return a;}
                else{
                    a=false;
                }
            }
            return a;
        }
    }
    class Run123 {

       static byte userBookId=0;
        public static void main(String args[]) {
            System.out.println("-----------Welcome To Galaxy Library------------");
            Scanner sc = new Scanner(System.in);
            Books books[] = new Books[60];
            Books callBook = new Books(1);
            Members callObj = new Members();
            Librarian librarian = new Librarian();
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
            String[] uniqueClassicNovels = {
                    "The Great Gatsby",
                    "Brave New World",
                    "The Catcher in the Rye",
                    "Wuthering Heights",
                    "One Hundred Years of Solitude",
                    "The Count of Monte Cristo",
                    "The Picture of Dorian Gray",
                    "Anna Karenina",
                    "The Brothers Karamazov",
                    "Frankenstein"
            };
            String[] authorsClssicalNovels = {
                    "F. Scott Fitzgerald",
                    "Aldous Huxley",
                    "J.D. Salinger",
                    "Emily Brontë",
                    "Gabriel García Márquez",
                    "Alexandre Dumas",
                    "Oscar Wilde",
                    "Leo Tolstoy",
                    "Fyodor Dostoevsky",
                    "Mary Shelley"
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
            }for(int i=30,k=0;i<40;i++,k++){
                books[i].bookName=businessCaseStudiesBooks[k];
                books[i].Genre="Business Case Studies";
                books[i].bookauhtor=businessCaseStudiesAuthors[k];
            }for(int i=40,k=0;i<50;i++,k++){
                books[i].bookName=uniqueClassicNovels[k];
                books[i].Genre="Classical Novels";
                books[i].bookauhtor=authorsClssicalNovels[k];
            }
            String defaultMembers[] = {"Taher","Parth","Meet","Yash","Nitin"};
            Members members[] = new Members[10];//Provides the array of members
            for (int i = 0; i < members.length; i++) {
                members[i] = new Members();
            } for (int i = 0; i < defaultMembers.length; i++) {
                members[i].name = defaultMembers[i];
                members[i].defaultpass=members[i].name+"123";
            }
            while(true) {
                System.out.println("---Enter 1 if you are a reader and 2 if you are a librarian---");
                byte type = sc.nextByte();
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
                                while (true) {
                                    System.out.println("Select your preferred Genre of Books:");
                                    System.out.println("1.Fiction\n2.Horror\n3.Psychological\n4.Business Administration\n5.Classical Novels");
                                    byte userGenre = sc.nextByte();
                                    switch (userGenre) {
                                        case 1:
                                            callBook.displayBooks(books, "Fiction");
                                            System.out.println("Kindly enter the book id You want to read");
                                            userBookId = sc.nextByte();//Code to be improvised.!!Add bookname;
                                            System.out.println(userBookId);
                                            if (callBook.availibility(books, members, memId, userBookId)) {
                                                System.out.println("Book has been Issued to you");
                                            }
                                            break;
                                        case 2:
                                            callBook.displayBooks(books, "Horror");
                                            System.out.println("Kindly enter the book id You want to read");
                                            userBookId = sc.nextByte();
                                            if (callBook.availibility(books, members, memId, userBookId)) {
                                                System.out.println("Book has been Issued to you");
                                            }
                                            break;
                                        case 3:
                                            callBook.displayBooks(books, "Physocological");
                                            System.out.println("Kindly enter the book id You want to read");
                                            userBookId = sc.nextByte();
                                            if (callBook.availibility(books, members, memId, userBookId)) {
                                                System.out.println("Book has been Issued to you");
                                            }
                                            break;
                                        case 4:
                                            callBook.displayBooks(books, "Business Case Studies");
                                            System.out.println("Kindly enter the book id You want to read");
                                            userBookId = sc.nextByte();
                                            if (callBook.availibility(books, members, memId, userBookId)) {
                                                System.out.println("Book has been Issued to you");
                                            }
                                            break;
                                        case 5:
                                            callBook.displayBooks(books,"Classical Novels");
                                            System.out.println("Kindly enter the book id You want to read");
                                            userBookId = sc.nextByte();
                                            if (callBook.availibility(books, members, memId, userBookId)) {
                                                System.out.println("Book has been Issued to you");
                                            }
                                            break;
                                        default:
                                            System.out.println("Enter available genre Books"); break;
                                    }
                                    System.out.println("Happy Reading");
                                    callObj.displayandReturn(books, members, memId);
                                    System.out.println("do you Want to continue Reading Books?Yes/No");
                                    sc.nextLine();
                                    String input1 = sc.nextLine();
                                    if (input1.equalsIgnoreCase("yes")) {
                                        continue;
                                    } else break;
                                }
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
                        boolean k;
                        while (true) {
                            System.out.println("---Please Enter Your Name and pass---");
                            String name = sc.nextLine();
                            String pass = sc.nextLine();
                            if (librarian.LibLogin(name, pass)) {
                                k = true;
                                break;
                            } else {
                                System.out.println("Enter Proper credentials");
                            }
                        }
                        if (k) {
                            System.out.println("What Action do you want to Perform:");
                            System.out.println("1. Show Member Details.");
                            System.out.println("2. Show book data and update.");
                            System.out.println("3. Add a book.");
                            System.out.println("4. Remove  a book.");
                        }
                        int choice = sc.nextInt();
                        switch (choice) {
                            case 1:
                                System.out.println("Enter the id of member you want to see details of");
                                byte id = sc.nextByte();
                                librarian.displayMemberDetails(members[id - 1]);
                                break;
                            case 2:
                                librarian.reviewAndUpdate(books);
                                break;
                            case 3:
                                librarian.addBook(books);
                                break;
                            case 4:
                                librarian.RemoveBook(books);
                                break;
                        }
                }
            }
        }
    }






