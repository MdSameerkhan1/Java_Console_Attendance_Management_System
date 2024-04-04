package com.ams.admin;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

import com.ams.color.CustomColor;
import com.ams.connection.DBConnectionClass;
import com.ams.customException.AdminResourceException;
import com.ams.reports.Attendance_Report;
import com.ams.staff.FacultyLogin;
import com.ams.student.StudentList;
import com.ams.student.StudentLogin;
public class AdminLogin {   
     static  Scanner din = new Scanner(System.in);	
     
    public static void main(String[] args) throws SQLException {  
    	int choice;   
    	 System.out.println(CustomColor.SAFFRON + "\t\t---AA------------------------------------------IIIIII");
         System.out.println("\t  A  A                                           II");
         System.out.println("\t AAAAAA  Welcome To Ai School Of learning--------II");
         System.out.println("\tA------A---------------------------------------IIIIII");
         // Reset color to default after printing
         System.out.println(CustomColor.RESET);
        System.out.println("\n\t Please Choose Your Login");
     
        System.out.println(CustomColor.RED_ITALIC+" \t| 1.Admin Login |");
        System.out.println(" \t| 2.Staff Login |");
        System.out.println(" \t| 3.Student Login |");     
        choice = din.nextInt();
        switch (choice) {
            case 1:
            	 boolean loggedIn = false;
            	do {           
                System.out.println(CustomColor.ROSY_PINK+"\t Welcome  Admin Enter Your Credentials Here");
                String username, password;
                System.out.println(CustomColor.BLUE+"\n\t ENTER USERNAME");
                username = din.next(); 
                System.out.println("\t  ENTER PASSWORD" );
                password = din.next(); 
                try {
                	Connection con = DBConnectionClass.getConnection();                
                    String sql = "select * from admin";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                    	if (username.equalsIgnoreCase(rs.getString(2)) && password.equalsIgnoreCase(rs.getString(3))) {
                            System.out.println(CustomColor.BROWN+" *********  WELCOME ADMIN ***********");
                            System.out.println("");
                            new AdminLogin().process();  
                            loggedIn = true;
                        }
                    }
                    if (!loggedIn) {
                    	throw new AdminResourceException("Sorry ....Login Failed.");
                    	
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (!loggedIn);
            break;
            case 2:
            	FacultyLogin tl = new FacultyLogin();
            	tl.LogType();
            	break;
            case 3:
            	StudentLogin sl = new StudentLogin();
            	sl.login();
            	break;
        }  
    }              		
	void process() {
        int choice1;              
        do {
        	System.out.println(CustomColor.BLUE+" \t 1. View Staff Members");
        	System.out.println("\t 2. View Students List");
        	System.out.println("\t 3. View Attendance");
        	System.out.println("\t 4. Update Staff Status Active Or InActive"); 
        	System.out.println("\t 5. Add Events");
        	System.out.println("\t 6. View Events");
        	System.out.println("\t 7. Update Staff Information");
        	System.out.println("\t 8. View Marks");        	 
        	System.out.println("\t 9. View Assignments");        	
        	System.out.println("\t 10.Generate Reports");   
        	System.out.println("\t 11. Exit"); 
        	System.out.println("\t Enter Your Option");
        	System.out.println("");
            choice1 = din.nextInt();
            switch (choice1) {              
            case 1:
                try {
                	Connection con = DBConnectionClass.getConnection();
                    String sql = "select * from staff";
                    System.out.println(CustomColor.RED_ITALIC+"List of Staff Members");
                    Statement st =  con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    System.out.println(CustomColor.ORANGE+"sid\t\t\tstaff_name\t\t\tsuser\t\t\tsemail\t\t\tsmobile\t\tstatus");
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + "\t\t\t" + rs.getString(2) + "\t\t\t" + rs.getString(3) + "\t\t\t" +
                                rs.getString(5) + "\t\t\t" + rs.getString(6) + "\t\t\t" + rs.getString(7));
                    }
                    System.out.println("");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;                  
                case 2:
                	StudentList sList= new StudentList();
                	sList.List();
                       break;                              	
                case 3:
                    try {
                    	Connection con = DBConnectionClass.getConnection();                                      
                        String selectAllAttendance = "SELECT * FROM attendance1";
                        PreparedStatement ps =  con.prepareStatement(selectAllAttendance);
                        ResultSet rs = ps.executeQuery();
                        System.out.println(CustomColor.GREEN_ITALIC+"Attendance Records for All Students:");
                        System.out.println(CustomColor.BLUE_ITALIC+"Student ID\tDate\t\tStatus");

                        while (rs.next()) {
                            System.out.println(rs.getString("student_id") + "\t" + rs.getDate("date") + "\t" + rs.getString("attendance_status"));
                        }

                        if (!rs.isBeforeFirst()) {
                            System.out.println(CustomColor.RED_ITALIC+"All Attendance records in database found.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;

                
                case 4:
                	try {  
                		Connection con = DBConnectionClass.getConnection();                    
                        System.out.print(CustomColor.GREEN_ITALIC+"Enter Staff ID to update status: ");
                        int sid = din.nextInt();
                        System.out.print(CustomColor.GREEN_ITALIC+"Enter new status active inactive): ");
                        String Status = din.next();
                        Date currentDate = Date.valueOf(LocalDate.now());                        
                        String sql = "UPDATE staff SET status = ?, date_column = ? WHERE sid = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, Status);
                        ps.setDate(2, currentDate);
                        ps.setInt(3, sid);

                        int rowsAffected = ps.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println(CustomColor.GREEN_ITALIC+"Staff status updated successfully.");
                        } 
                        else 
                        {
                            System.out.println(CustomColor.RED_ITALIC+"No staff found with the given ID.");
                        }                                      
                     
                    } 
                	catch (SQLException e)
                	{
                        e.printStackTrace();
                    }
                		break;
                case 5:
                	new AdminLogin().CreateEvents(null, null, null);
                	
                     break;
                case 6:
                	String query = "SELECT * FROM events";

                  try {
                    	Connection con = DBConnectionClass.getConnection();                       
                        Statement statement =  con.createStatement();                       
                        ResultSet resultSet = statement.executeQuery(query);                      
                        while (resultSet.next()) {
                            int eventId = resultSet.getInt("id");
                            String eventName = resultSet.getString("name");
                            String eventDate = resultSet.getString("date");
                            String eventLocation = resultSet.getString("location");
                            System.out.println("Event ID: " + eventId);
                            System.out.println("Event Name: " + eventName);
                            System.out.println("Event Date: " + eventDate);
                            System.out.println("Event Location :" + eventLocation);
                            System.out.println("---------------------------");
                        }
                      break;                     
                    }
                  catch (SQLException e) {
                        e.printStackTrace();
                    }                
                case 7:
                	try {                       
                		Connection con = DBConnectionClass.getConnection();                      
                        String sql = "UPDATE staff SET suser = ? WHERE sid = ?";
                        PreparedStatement statement = con.prepareStatement(sql);
                       
                        System.out.print(CustomColor.GREEN_ITALIC+"Enter the new username: ");
                        String newUsername = din.next();
                        System.out.print(CustomColor.GREEN_ITALIC+"Enter the staff ID: ");
                        int staffId = din.nextInt();
                        statement.setString(1, newUsername);
                        statement.setInt(2, staffId);                 
                        int rowsAffected = statement.executeUpdate();                  
                        if (rowsAffected > 0) {
                            System.out.println(CustomColor.GREEN_ITALIC+"Staff information updated successfully.");
                        } else {
                            System.out.println(CustomColor.RED_ITALIC+"Failed to update staff information.");
                        }                        
                       
                    } catch (SQLException e) 
                	{
                        e.printStackTrace();
                    }                
                		break;
                case 8:
                    try  {
                    	Connection con = DBConnectionClass.getConnection();
                        String sql = "SELECT student_id, final FROM marks;";

                        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

                            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                                while (resultSet.next()) {
                                    int studentId = resultSet.getInt("student_id");
                                    int marks = resultSet.getInt("final");

                                    System.out.println(CustomColor.RED_ITALIC+"Student ID : " + studentId + ", Marks: " + marks);
                                }
                            }
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;                	
                case 9 :
                	 try {
                        
                		 Connection con = DBConnectionClass.getConnection();
                         String sql = "SELECT * FROM assignment";
                         PreparedStatement statement = con.prepareStatement(sql);                       
                         ResultSet resultSet = statement.executeQuery();                   
                         while (resultSet.next()) {
                             int assignmentId = resultSet.getInt("id");
                             String title = resultSet.getString("title");
                             String description = resultSet.getString("description");

                             System.out.println(CustomColor.SAFFRON+"Assignment ID: " + assignmentId);
                             System.out.println("Title: " + title);
                             System.out.println("Description: " + description);
                             System.out.println();
                         }                                             
                     } catch (SQLException e) {
                         e.printStackTrace();
                     }                
                	break;              
                case 10:
                	 System.out.println(CustomColor.YELLOW_BOLD+"Select Your Option");
                     System.out.println("Press 1. For Attendance_Report");
                     System.out.println("Press 2. For Marks");
                     int reportOption = din.nextInt();
                     switch (reportOption) {
                         case 1:
                        	 Attendance_Report attendanceReport = new Attendance_Report();
                             attendanceReport.report();
                             break;                            
                         case 2:
                        	 Attendance_Report attendanceReport1 = new Attendance_Report();
                             attendanceReport1.report2();
                             break;                            
                         default:
                             System.out.println(CustomColor.RED_ITALIC+"Invalid report option");
                     }
                     break;            
                case 11:
                	System.out.println(CustomColor.BROWN+"THANK YOU ADMIN HAVE A NICE DAY");
                	System.exit(1);
                default:
                	System.out.println(CustomColor.RED_ITALIC+"Invalid option");
                	break;
            }              
        } while (choice1 != 11 );           
        }	
	void CreateEvents(String eventName, String eventDate, String eventLocation ) {
        try  {
        	Connection con = DBConnectionClass.getConnection();
        	 System.out.println(CustomColor.BLUE_ITALIC+"\n\t Enter Event Name");
             eventName = din.next(); 
             System.out.println("\n\t Enter Event Date");
             eventDate = din.next(); 
             System.out.println("\n\t Enter Event Location");
             eventLocation = din.next(); 
            String sql = "INSERT INTO events (name, date, location) VALUES (?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, eventName);
            statement.setString(2, eventDate);
            statement.setString(3, eventLocation);
            statement.executeUpdate();
            System.out.println(CustomColor.GREEN_ITALIC+"Event created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }	
	}



