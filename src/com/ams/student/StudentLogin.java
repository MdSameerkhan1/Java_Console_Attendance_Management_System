package com.ams.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ams.color.CustomColor;
import com.ams.connection.DBConnectionClass;
import com.ams.otp_generator.OTPMailSender;

public class StudentLogin {
    Scanner din = new Scanner(System.in);

    public void login() {
        System.out.println(CustomColor.BLUE_ITALIC+"Student Login Here");
        String suser, password;

        System.out.println("Enter UserName");
        suser = din.next();
        System.out.println("Enter Password");
        password = din.next();

        try {
            Connection con = DBConnectionClass.getConnection();

            // Check 1stYearStudents table
            if (checkLogin(con, "1stYearStudents", suser, password)) {
                System.out.println(CustomColor.CYAN+"Welcome Student from 1st Year! Have a Great Day Please Wait For Otp");

                // Fetch student email from the database
                String studentEmail = getEmailFromDatabase(suser, "1stYearStudents");

                // Generate and send OTP
                OTPMailSender otpMailSender = new OTPMailSender();
                String generatedOTP = OTPMailSender.generateOTP();
                OTPMailSender.sendOTPEmail(studentEmail, generatedOTP);

                // Validate OTP
                if (otpMailSender.validateOTP(getUserInputOTP(), generatedOTP)) {
                    // Proceed with further actions
                	System.out.println("OTP VERIFIED SUCCESSFULLY");
                    process();
                    return;
                } else {
                    System.out.println(CustomColor.RED_ITALIC+"Invalid OTP. Login failed.");
                }
            }

            // Check 2ndYearStudents table
            if (checkLogin(con, "2ndYearStudents", suser, password)) {
                System.out.println(CustomColor.GREEN_ITALIC+"Welcome Student from 2nd Year! Have a Great Day ");

             
                String studentEmail = getEmailFromDatabase(suser, "2ndYearStudents");

             
                OTPMailSender otpMailSender = new OTPMailSender();
                String generatedOTP = OTPMailSender.generateOTP();
                OTPMailSender.sendOTPEmail(studentEmail, generatedOTP);

                
                if (otpMailSender.validateOTP(getUserInputOTP(), generatedOTP)) {
                	System.out.println(CustomColor.GREEN_ITALIC+"OTP VERIFIED");

                    process();
                    return;
                } else {
                    System.out.println(CustomColor.RED_ITALIC+"Invalid OTP. Login failed.");
                }
            }

            System.out.println(CustomColor.RED_ITALIC+"Invalid username or password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEmailFromDatabase(String username, String tableName) {
        try {
            Connection con = DBConnectionClass.getConnection();

        
            String sql = "SELECT studentemail FROM " + tableName + " WHERE suser = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Retrieve and return the email address
                        return rs.getString("studentemail");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
        return null;
    }

    private String getUserInputOTP() {
        System.out.println(CustomColor.YELLOW_BOLD+"Enter OTP:");
        return din.next();
    }

    private boolean checkLogin(Connection con, String tableName, String username, String password) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " WHERE suser = ? AND password = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void main(String[] args) {
        new StudentLogin().login();
    }


    public void register() {
        String studentname, studentuser, password, studentemail, studentmobile, gender, course;

        System.out.println(CustomColor.BLUE+"Register Here");
        try {
            Connection con = DBConnectionClass.getConnection();

            System.out.println("Enter Student Name");
            studentname = din.next();
            System.out.println("Enter Student UserName");
            studentuser = din.next();
            System.out.println("Enter Student Password");
            password = din.next();
            System.out.println("Enter Student Email");
            studentemail = din.next();
            System.out.println("Enter Student Mobile");
            studentmobile = din.next();
            System.out.println("Enter Your Gender M / F");
            gender = din.next();
            System.out.println("Enter Student Course (1 for 1st year, 2 for 2nd year)");
            course = din.next();

            String tableName;
            if ("1".equals(course)) {
                tableName = "1stYearStudents";
            } else if ("2".equals(course)) {
                tableName = "2ndYearStudents";
            } else {
                System.out.println(CustomColor.GREEN+"Invalid course selection");
                return;
            }

            // Check if the email already exists
            if (isEmailExists(con, studentemail, tableName)) {
                System.out.println(CustomColor.GREEN+"Email already exists. Please choose a different email.");
                return;
            }

            String sql = "INSERT INTO " + tableName + "(student_name, suser, password, studentemail, studentmobile, gender, course) VALUES(?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, studentname);
                ps.setString(2, studentuser);
                ps.setString(3, password);
                ps.setString(4, studentemail);
                ps.setString(5, studentmobile);
                ps.setString(6, gender);
                ps.setString(7, course);

                int i = ps.executeUpdate();

                if (i == 1) {
                    // Retrieve the generated student_id
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int studentId = generatedKeys.getInt(1);

                        System.out.println(CustomColor.GREEN+"Registration successfully Completed");
                        System.out.println("Student ID: " + studentId);

                        String body = null;
                        String subject = null;
                        sendEmail(studentId, studentemail, subject, body);
                    } else {
                        System.out.println(CustomColor.RED_ITALIC+"Failed to retrieve generated student_id");
                    }
                } else {
                    System.out.println(CustomColor.RED+"Application Failed To send");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to check if the email already exists in the specified table
    private boolean isEmailExists(Connection con, String email, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE studentemail = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }


    
    	static void sendEmail(int studentId, String studentemail, String subject, String body) {
    	    final String username = "msk498767@gmail.com"; // Your email address
    	    final String password = "pckk aeul cbvf prbz"; // Your email password

    	    Properties props = new Properties();
    	    props.put("mail.smtp.auth", "true");
    	    props.put("mail.smtp.starttls.enable", "true");
    	    props.put("mail.smtp.host", "smtp.gmail.com");
    	    props.put("mail.smtp.port", "587");

    	    // Get the default Session object
    	    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
    	        protected PasswordAuthentication getPasswordAuthentication() {
    	            return new PasswordAuthentication(username, password);
    	        }
    	    });

    	    try {
    	        MimeMessage message = new MimeMessage(session);
    	        message.setFrom(new InternetAddress(username));
    	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(studentemail));
    	        message.setSubject("Welcome to School");

    	        // Now set the actual message
    	        message.setText("Dear Student,\n\n" +
    	                "We are thrilled to welcome you to our school community!\n" +
    	                "As you embark on this exciting journey, we want to assure you that our dedicated faculty and staff are here to support and guide you every step of the way.\n" +
    	                "Here are a few things to get you started:\n" +
    	                "- Check the school calendar for important dates and events.\n" +
    	                "- Familiarize yourself with your class schedule and classroom locations.\n" +
    	                "- Explore the school facilities and resources available to you.\n\n" +
    	                "If you have any questions or need assistance, feel free to reach out to our school office or your teachers.\n" +
    	                "Wishing you a fantastic academic year filled with learning, growth, and success!\n\n" +
    	                "Your Student ID: " + studentId + "\n\n" +
    	                "Best regards,\n" +
    	                "The School Administration");

    	        // Send the message
    	        Transport.send(message);

    	        System.out.println("Email sent to: " + studentemail);
    	    } catch (MessagingException e) {
    	        e.printStackTrace();
    	    }
    	}

		
	

	void process(){
    	 int choice1 ;
        
         
         do {
         	System.out.println(CustomColor.SAFFRON+"\t \t1. View Attendance");
         	System.out.println("\t 2. View Marks");
         	System.out.println("\t 3. View Assignments");
         	System.out.println("\t 4. View Events");     	
         	System.out.println("\t 5. Exit");
         	 choice1 = din.nextInt();
             
			switch (choice1) {
             case 1:
            	 
            	 try {
            		 Connection con = DBConnectionClass.getConnection();
                     System.out.println(CustomColor.BLUE_ITALIC+"Enter Your Id  to View attendance:");
                     String student_id = din.next();

                     String selectAttendance = "SELECT * FROM attendance WHERE student_id = ?";
                     PreparedStatement ps = con.prepareStatement(selectAttendance);
                     ps.setString(1, student_id);

                     ResultSet rs = ps.executeQuery();

                     int presentCount = 0;
                     int absentCount = 0;

                     while (rs.next()) {
                         String status = rs.getString("attendance_status");
                         if ("Present".equalsIgnoreCase(status)) {
                             presentCount++;
                         } else if ("Absent".equalsIgnoreCase(status)) {
                             absentCount++;
                         }
                         
                     }

                     System.out.println(CustomColor.CYAN_BOLD+"Total Attendance for student " + student_id + ":");
                     System.out.println("Present: " + presentCount);
                     System.out.println("Absent: " + absentCount);
                     
                    
                 } catch (SQLException e) {
                     e.printStackTrace();
                 }
            	 break;
             case 2:
            	 Scanner din = new Scanner(System.in);
                 System.out.println(CustomColor.GREEN+"Enter student Roll_Number to view:");
                 String student_Id = din.next();

                 try  {
                	 Connection con = DBConnectionClass.getConnection();
                     String sql = "SELECT student_id, final FROM marks WHERE student_id = ?";

                     try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                         // Convert the user input to an integer
                         int studentId = Integer.parseInt(student_Id);
                         preparedStatement.setInt(1, studentId);

                         try (ResultSet resultSet = preparedStatement.executeQuery()) {
                             // Process the ResultSet
                             while (resultSet.next()) {
                                 int StudentId = resultSet.getInt("student_id");
                                 int finalMarks = resultSet.getInt("final");

                                 System.out.println(CustomColor.CYAN_BOLD+"Student ID: " + StudentId + ", Final Marks: " + finalMarks);
                             }
                         }
                     }

                 } catch (SQLException e) {
                     e.printStackTrace();
            	         } break ;
             case 3:
            	
            	 try {
                     
            		 Connection con = DBConnectionClass.getConnection();

                  
                     String sql = "SELECT * FROM assignment";
                     PreparedStatement statement = con.prepareStatement(sql);

                    
                     ResultSet resultSet = statement.executeQuery();

                
                     while (resultSet.next()) {
                         int assignmentId = resultSet.getInt("id");
                         String title = resultSet.getString("title");
                         String description = resultSet.getString("description");

                         System.out.println(CustomColor.GREEN_BOLD+"Assignment ID: " + assignmentId);
                         System.out.println("Title: " + title);
                         System.out.println("Description: " + description);
                         System.out.println();
                     }

                     
                
                 } catch (SQLException e) {
                     e.printStackTrace();
                 } break;
             case 4:
            	 String query = "SELECT * FROM events";

                 try {
               
                	 Connection con = DBConnectionClass.getConnection();
                     Statement statement = con.createStatement();                       
                     ResultSet resultSet = statement.executeQuery(query);                      
                     while (resultSet.next()) {
                         int eventId = resultSet.getInt("id");
                         String eventName = resultSet.getString("name");
                         String eventDate = resultSet.getString("date");
                         String eventLocation = resultSet.getString("location");
                         System.out.println(CustomColor.ORANGE+"Event ID: " + eventId);
                         System.out.println("Event Name: " + eventName);
                         System.out.println("Event Date: " + eventDate);
                         System.out.println("Event Location :" + eventLocation);
                         System.out.println("---------------------------");
                     }

                   break;
                   
                 } catch (SQLException e) {
                     e.printStackTrace();
                 }
             
             case 5 :
            	 System.out.println(CustomColor.GREEN_ITALIC+"Thank you");
            	
			}	 
 
             
    	
    } while(choice1 !=5);
    }
}

