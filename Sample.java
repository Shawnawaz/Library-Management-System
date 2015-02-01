package sample;


import Entity.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Sample {
	static Connection conn = null;
        String message;
        
        public Connection DBconnect()
		{
		
			try
			{
				//used to connect to the Database with username : root and password : 1428
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "1428");
			
				// Create a SQL statement object and execute the query.
				Statement stmt = conn.createStatement();
					
				// Set the current database, if not already set in the getConnection
				// Execute a SQL statement
				stmt.execute("use library;");
			
			}
			catch(SQLException ex)
			{
				System.out.println("Error in connection: " + ex.getMessage());
			
			}
                    return conn;
		}
	
       
      String[][] search(String id_1,String title_1,String author_1,String fname_1,String lname_1,String mInit_1)
      {
          DBconnect();
          PreparedStatement prepstmt = null;
          ResultSet rs=null;
          String Query;
          //Book b=new Book();
          int count=0;
          String[][] result=new String[100][8];
          try
          {
              System.out.println("inside search conn and outisde else if , id:"+id_1+", title : "+title_1);
               
               Query ="Select book_id,title,author_name,branch_id,no_of_copies as Total,\n" +
"no_of_copies -(select count(*) from book_loans bl where date_in IS NULL  \n" +
"and bl.book_id=bc.book_id and bl.branch_id=bc.branch_id) as Available\n" +
"from book_copies bc natural join book natural join book_authors\n" +
"where bc.book_id like '%"+id_1+"%' AND book.title like '%"+title_1+"%' AND book_authors.author_name like '%"+author_1+"%'\n" ;

                if(!fname_1.isEmpty())
                {
                    Query  += "and book_authors.Fname like '%"+fname_1+"%'";
                }
                if(!lname_1.isEmpty())
                {
                    Query  += "and book_authors.Lname like '%"+lname_1+"%'";
                }
                if(!mInit_1.isEmpty())
                {
                    Query  += "and book_authors.Minit like '%"+mInit_1+"%'";
                }

                Query += "group by bc.book_id, bc.branch_id;";
		
				System.out.println(Query);
				prepstmt = conn.prepareStatement(Query);
				//prepstmt.setString(1,id_1);
                                
			//}
		
               rs=prepstmt.executeQuery();
                
                
                while(rs.next())
		{
					
					result[count][0]=rs.getString("book_id");
					result[count][1]=rs.getString("title");
					result[count][2]=rs.getString("author_name");
                                        result[count][3]=rs.getString("branch_id");
                                        result[count][4]=rs.getString("Total");
                                        result[count][5]=rs.getString("Available");
                                        
					count++;
                                        System.out.println("ID: "+ rs.getString("book_id"));
							
					System.out.println("title :"+ rs.getString("title"));
										
					System.out.println("title :"+ rs.getString("author_name"));
                }                                
          }
          catch(SQLException e)
          {
              // TODO Auto-generated catch block
		e.printStackTrace();
		return null;
          }
          return result;
      }

      
      String checkOUT(int c_no,String b_id, int br_id )
      {
          conn=DBconnect();
          message=null;
          String Book_ID=null;
          int Branch_ID;
          int Loan_ID;
          int count=0;
          
          
          SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
          Calendar calendar = new GregorianCalendar();
          java.util.Date currDate = calendar.getTime();
          String Curr_Date = formatter.format(currDate);
          calendar.add(Calendar.DATE, 14);
	  java.util.Date dueDate = calendar.getTime();
	  String Due_Date = formatter.format(dueDate);
      
           
        try 
        {
            String Query1 = " Select book_id,branch_id,\n" +
                    "no_of_copies -(select count(*) from book_loans bl where date_in IS NULL  \n" +
                    "and bl.book_id=bc.book_id and bl.branch_id=bc.branch_id) as Available\n" +
                    "from book_copies bc \n" +
                    "where bc.book_id = "+b_id+" and bc.branch_id= "+br_id+"\n" +
                    "group by bc.book_id, bc.branch_id;";
            
            
            
            //Using preparedstatement to make the query a userdefined one.
           // System.out.println(Query1);
            PreparedStatement prepstmt;
            prepstmt = conn.prepareStatement(Query1);
            //prepstmt.setString(1,b_id);
            //prepstmt.setInt(2,br_id);
            ResultSet rs1=prepstmt.executeQuery();
            
            String Query2 = "Select count(card_no) as Card_Count from book_loans where card_no = ? and date_in IS NULL;";
            prepstmt=conn.prepareStatement(Query2);
            prepstmt.setInt(1,c_no);
            ResultSet rs2=prepstmt.executeQuery();
            
            String Query3="Select * from borrower where card_no=?";
            
            prepstmt=conn.prepareStatement(Query3);
            prepstmt.setInt(1,c_no);
            ResultSet rs3=prepstmt.executeQuery();
            
            String Query5="Select max(Loan_id) as max_LoanID from book_loans";
            
            prepstmt=conn.prepareStatement(Query5);
            ResultSet rs5=prepstmt.executeQuery();
            
            String Query6="Select book_id from book";
            
            prepstmt=conn.prepareStatement(Query6);
            ResultSet rs6=prepstmt.executeQuery();
            
            String Query7="Select branch_id from book_copies";
            
            prepstmt=conn.prepareStatement(Query7);
            ResultSet rs7=prepstmt.executeQuery();
            
            
           
            //To Check whether card no is available in the DB
            //rs3 corresponds to the card existance query
            if(!rs3.next())
            {
                message ="No Card available";
                
            }
            else
            {   
                //rs2 is used to fetch the count of card instance in the book_loans
                while(rs2.next())
                {
                    int Book_flag=0;
                    int Branch_flag=0;
                    //To check whether the Book ID is a valid one
                    //rs6 is used to fetch the list of book_ID from the book table
                    while(rs6.next())
                    {
                        if(b_id.equalsIgnoreCase(rs6.getString("book_id")))
                        {
                            Book_flag=1;
                            System.out.println("Book available");
                        }
                    }
                    //To Check whether the branch ID is a valid one
                    //rs7 is used to fetch the branch id from the DB
                    while(rs7.next())
                    {
                        if(br_id == rs7.getInt("Branch_id"))
                        {
                            Branch_flag=1;
                            System.out.println("Branch avaialable");
                        }
                    }
                    //to check the no. of card/ user borrowed
                    if(rs2.getInt("Card_Count")>=3)
                    {
                        message = "Borrower has reached the borrowing limit";
                    }
                    //setting the warning message for invalid book ID
                    else if(Book_flag==0)
                    {
                        message ="Invalid Book ID";
                        System.out.println("Book not available");
                    }
                    //setting the warning message for invalid branch ID
                    else if(Branch_flag==0)
                    {
                        message = "Invalid Branch ID";
                        System.out.println("Branch not available");
                    }
                    
                    else
                    {   
                        //rs5 is used to fetch the max loan ID from the DB
                        rs5.next();
                        //rs1 is used to fetch the book id, branch id,availability
                        while(rs1.next())
                        {
                            
                            count=rs1.getInt("Available");
                            if(count==0)
                            {
                                message = "No Book is available";
                            }
                            else
                            {
                                Book_ID=rs1.getString("book_id");
                                System.out.println("Book ID :"+Book_ID);
                                Branch_ID=rs1.getInt("branch_id");
                                System.out.println("Branch ID :"+Branch_ID);
                                Loan_ID=rs5.getInt("max_LoanID");
                                Loan_ID=Loan_ID+1;
                                
                                //Actual Insert command to insert the check out
                                    String Query4 = "INSERT INTO book_loans(loan_id,book_id,branch_id,card_no,date_out,due_date,date_in) VALUES(?,?,?,?,?,?,?)";
                                    prepstmt=conn.prepareStatement(Query4);
                                    prepstmt.setInt(1,Loan_ID);
                                    prepstmt.setString(2,b_id);
                                    prepstmt.setInt(3,br_id);
                                    prepstmt.setInt(4,c_no);
                                    prepstmt.setString(5,Curr_Date);
                                    prepstmt.setString(6,Due_Date);
                                    prepstmt.setString(7,null);
                                                                
                                    prepstmt.executeUpdate();
                                    message="Checked out Successfully!!!" + "\nLoan_ID " +Loan_ID;
  
                                
                                
                            }          
                        }
                    }
                    
                }
                
            }
            
            
        } catch (SQLException ex) 
        {
            Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }            
             
      String[][] checkIN(int c_no, String b_id,String fname,String lname)
      {
          String[][] res=new String[100][7];
            try {
                conn=DBconnect();
                
                int count=0;
                int extra;
                
                String Query1 = " Select loan_id,book_id,branch_id,card_no,date_out,due_date,date_in\n" +
                                "from book_loans bl NATURAL JOIN borrower b where bl.Date_in IS NULL AND bl.book_id like '%"+b_id+"%' \n";
                
                if(c_no != 0)
                {
                    Query1=Query1 + " AND bl.card_no="+c_no;
                }
                if(!fname.isEmpty())
                {
                    
                    Query1=Query1 + " AND b.fname like '%"+fname+"%'";
                }
                if(!lname.isEmpty())
                {
                    Query1=Query1 + " AND b.lname like '%"+lname+"%' ";
                         
                }
                
                Query1=Query1 + ";";
                
                
                
                //Using preparedstatement to make the query a userdefined one.
                System.out.println(Query1);
                PreparedStatement prepstmt;
                prepstmt = conn.prepareStatement(Query1);
                ResultSet rs1=prepstmt.executeQuery();
                
                while(rs1.next())
                {
                    extra=rs1.getInt("loan_id");
                    res[count][0]=Integer.toString(extra);
                    res[count][1]=rs1.getString("book_id");
                    extra=rs1.getInt("branch_id");
                    res[count][2]=Integer.toString(extra);
                    extra=rs1.getInt("card_no");        
                    res[count][3]=Integer.toString(extra);
                    res[count][4]=rs1.getString("date_out");
                    res[count][5]=rs1.getString("due_date");
                    res[count][6]=rs1.getString("date_in");
                    count++;
                }
                
                
                
            } catch (SQLException ex) {
                Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
            }
          
          
          
          return res;
          
          
          
      }
                
      
      float checkIN_Book(String l_id)
      {
          Fines fine=new Fines();
          float choice=0;
            try {
                conn=DBconnect();
                message=null;
                float amount;
                SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
                Calendar calendar = new GregorianCalendar();
                java.util.Date currDate = calendar.getTime();
                String Curr_Date = formatter.format(currDate);
                int Loan_id=Integer.parseInt(l_id);
                
                String Query1 = "UPDATE book_loans SET Date_in = ? WHERE loan_id=? ;";
                    PreparedStatement prepstmt;
                    prepstmt = conn.prepareStatement(Query1);
                    prepstmt.setString(1,Curr_Date);
                    prepstmt.setInt(2,Integer.parseInt(l_id));
                
                amount=fine.calculate_Fines(Loan_id);
                
                if ( amount == -1)
                {
                    
                    int result=prepstmt.executeUpdate();
                    if(result!=0)
                    {
                    choice = 1;
                    //message= "Checke IN successfully!!!!";
                    }
                    else
                    {
                        choice =0;
                    }
                
                }
                else if (amount > 0)
                {
                    choice=amount;
                    //message = "Checke IN not successfully!!!!";
                }
            } catch (SQLException ex) {
                Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
            }
          
           return choice;
      }
      
      String CheckIN_Fines(Float amount,String l_id)
      {
          conn=DBconnect();
          try {
                conn=DBconnect();
                message=null;
                
                SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
                Calendar calendar = new GregorianCalendar();
                java.util.Date currDate = calendar.getTime();
                String Curr_Date = formatter.format(currDate);
                int Loan_id=Integer.parseInt(l_id);
                
                String Query1 = "UPDATE book_loans SET Date_in = ? WHERE loan_id=? ;";
                    PreparedStatement prepstmt;
                    prepstmt = conn.prepareStatement(Query1);
                    prepstmt.setString(1,Curr_Date);
                    prepstmt.setInt(2,Integer.parseInt(l_id));
                                           
                    int result1=prepstmt.executeUpdate();
                    
                    Query1="INSERT INTO fines VALUES(?,?,TRUE);";
                    
                    prepstmt = conn.prepareStatement(Query1);
                    prepstmt.setInt(1,Loan_id);
                    prepstmt.setFloat(2,amount);
                                           
                    int result2=prepstmt.executeUpdate();
                            
                    if(result1!=0 && result2!=0)
                    {
                        message= "Booked Checked IN with fine paid";
                    }
                    else
                    {
                        message="Book is not checked IN!!!";
                    }
                
               } catch (SQLException ex) {
                Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
               }
          
           return message;
          
      }
      
    
      int addBorrower(String fname,String lname,String address,String city,String state,String phone)
      { 
          conn=DBconnect();
            int result=0;
            try {
                PreparedStatement prepstmt;
                
                int c_no;
                
                String Query1="Select lname from borrower where fname ='"+fname+"' AND lname ='"+lname+"' AND address='"+address+"' AND city='"+city+"' AND state ='"+state+"';";
                
                System.out.println(Query1);
               
            prepstmt = conn.prepareStatement(Query1);
            //prepstmt.setString(1,b_id);
            //prepstmt.setInt(2,br_id);
            ResultSet rs1=prepstmt.executeQuery();
                
                if(rs1.next())
                {
                    result=1;
                }
                else
                {
                    String Query2="Select max(card_no) as max_cno from borrower ;";
                    prepstmt=conn.prepareStatement(Query2);
                    ResultSet rs2=prepstmt.executeQuery();
                    
                    //extracting the max card no from the borrower table from DB.
                    rs2.next();
                    c_no=rs2.getInt("max_cno");
                    c_no=c_no+1;
                    
                    //Insert statment to insert the values
                    String Query3="INSERT INTO borrower VALUES (?,?,?,?,?,?,?);";
                    prepstmt=conn.prepareStatement(Query3);
                    prepstmt.setInt(1,c_no);
                    prepstmt.setString(2,fname);
                    prepstmt.setString(3,lname);
                    prepstmt.setString(4,address);
                    prepstmt.setString(5,city);
                    prepstmt.setString(6,state);
                    prepstmt.setString(7,phone);
                    
                    
                    if(prepstmt.executeUpdate()!=0)
                    {
                        result=c_no;
                    }
                    
                }
                          
                
                
                
                
                
            } catch (SQLException ex) {
                Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
            }
            return result;
      }
}


