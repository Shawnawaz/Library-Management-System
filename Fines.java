/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import sample.*;

/**
 *
 * @author ShawNawaz
 */
public class Fines {
    static Connection conn = null;
    Sample s=new Sample();
    
    float calculate_Fines(int loan_id) {
        float amount=0;
        PreparedStatement prepstmt=null;
        try {
            
            long diff;
            conn=s.DBconnect();
            
            SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
            Calendar calendar = new GregorianCalendar();
            java.util.Date currDate = calendar.getTime();
            String Curr_Date;
            Curr_Date =formatter.format(currDate);
            java.util.Date dueDate = null;
            //String Due_Date = formatter.format(dueDate);
            
            
            String Query="Select loan_id, Due_date, card_no from book_loans where loan_id = '"+loan_id+"' and Date_in IS NOT NULL;";
            
            System.out.println(Query);
            
            prepstmt=conn.prepareStatement(Query);
            
            //prepstmt.setInt(1, loan_id);
            ResultSet rs2=prepstmt.executeQuery();
            
            Query="Select loan_id from book_loans where loan_id=? ;";
                prepstmt=conn.prepareStatement(Query);
                prepstmt.setInt(1, loan_id);
                ResultSet rs3=prepstmt.executeQuery();
              
                if(!rs3.next())
                {
                    amount=-3;
                }
            
            if(!rs2.next())
            {
                Query="Select loan_id, Due_date, card_no from book_loans where loan_id=? ;";
                prepstmt=conn.prepareStatement(Query);
                prepstmt.setInt(1, loan_id);
                ResultSet rs1=prepstmt.executeQuery();
                while(rs1.next())
                {
                                                         
                    dueDate=rs1.getDate("Due_date");
                    
                    System.out.println("SYstem"+ currDate);
                    System.out.println("Due date"+ dueDate);
                    
                                   
                    diff=(currDate.getTime() - dueDate.getTime())/(60 * 60 * 1000* 24);
                    
                    System.out.println("no. of days :"+ diff);
                    if(diff<=0)
                    {
                        amount=-1;
                    
                    }
                    else
                    {
                        amount= (float) (diff*0.25);
                    }
                    
                }
            }
            else
            {
                amount=-2;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Fines.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    return amount;
        
    }
    
    
    
}
