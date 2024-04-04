package com.ams.staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ams.color.CustomColor;
import com.ams.connection.DBConnectionClass;
import com.ams.otp_generator.OTPGenerator;
import com.ams.student.StudentLogin;


public class FacultyLogin {
	static Scanner din = new Scanner(System.in);

	 
    public void LogType() {
        int Choice;
        System.out.println(CustomColor.SAFFRON+"Select Your Option");
        System.out.println("Press 1. For Login");
        System.out.println("Press 2. For Register");
       
        Choice = din.nextInt();

        switch (Choice) {
            case 1:
                Login();
                break;
            case 2:
                register();
                break;
        }
    }
    static void Login() {
        String username, password;

        System.out.println(CustomColor.GREEN+"Enter UserName");
        username = din.next();
        System.out.println("Enter Password");
        password = din.next();

        try {
        	Connection con = DBConnectionClass.getConnection();
            String sql = "SELECT * FROM staff WHERE username=? AND password=? ";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
           

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");

                if (status.equals("active")) {
                    System.out.println(CustomColor.SAFFRON+"Login Successful!");

                    // Generate and send OTP
                    String mobileNumber = rs.getString("smobile");
                    String generatedOTP = OTPGenerator.generateAndSendOTP(mobileNumber);

                    // Prompt user to enter OTP
                    System.out.println(CustomColor.SAFFRON+"Please enter the OTP sent to your mobile number:");
                    String userEnteredOTP = din.next();

                    // Validate OTP
                    if (validateOTP(userEnteredOTP, generatedOTP)) {
                        System.out.println(CustomColor.BROWN+"OTP Authentication Successful. Proceeding with the login.");
                        aprocess(con);
                    } else {
                        System.out.println(CustomColor.RED+"Invalid OTP. Login failed.");
                    }
                } else {
                    System.out.println(CustomColor.RED+"Login Failed. Account is inactive. Contact Admin");
                }
            } else {
                System.out.println(CustomColor.RED+"Login Failed. Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

 
        static void aprocess(Connection con) {
         int choice;
         do {
        	 System.out.print("Enter your choice: ");
        	 System.out.println("\n\t 1.Mark attendance....");
				 System.out.println("\t 2.View attendance....");
				 System.out.println("\t 3. Add Marks");
				 System.out.println("\t 4.Register a new Student");
				System.out.println("\t 5. Add Assignment");
				System.out.println("\t 6. Exit");
				 System.out.print("Enter your choice: ");
	     choice = din.nextInt();
	    
         switch(choice)
        {
       
        case 1:
        	    System.out.println("\t 1.Mark attendance....");
        	    markAttendance(con);
        	    break;
        case 2:
        	    System.out.println("\t 2.View attendance....");
		        viewAttendance(con);
        	    break;
        case 3:
        	 openMarksClass(con);
        	break;
        case 4:
        	    System.out.println("\t 4.Register a new Student");
        	    StudentLogin sl = new StudentLogin();
        	    sl.register();
        	    break;
        case 5:
            try {
            	Connection con1 = DBConnectionClass.getConnection();
                String sql = "INSERT INTO assignment (title, description, last_date) VALUES (?, ?, ?)";
                PreparedStatement statement = con1.prepareStatement(sql);

               
                System.out.print("Enter the assignment title: ");
                String title = din.nextLine();
                System.out.print("Enter the assignment description: ");
                String description =din.nextLine();
                System.out.print("Enter the last_date (YYYY-MM-DD): ");
                String lastDate = din.nextLine();

                statement.setString(1, title);
                statement.setString(2, description);
                statement.setString(3, lastDate);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Assignment added successfully.");
                } else {
                    System.out.println("Failed to add assignment.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            break;

     
        case  6:
        	System.out.println("Exiting");
           
            break;
        }
		
        	
         }while(choice != 6);
        }
        
        
   
        private static void viewAttendance(Connection con) {
            try {
                System.out.println("Enter student Roll_Number to view attendance:");
                String student_id = din.next();

                String selectAttendance = "SELECT * FROM attendance1 WHERE student_id = ?";
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
                    System.out.println(rs.getDate("date") + "\t" + status);
                }

                System.out.println("Total Attendance for student " + student_id + ":");
                System.out.println("Present: " + presentCount);
                System.out.println("Absent: " + absentCount);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        
        static void markAttendance(Connection con) {
            try {
                System.out.println("Enter student Roll no to mark attendance:");
                String student_id = din.next();

                // Check if attendance already marked for the student on the current date
                String checkAttendance = "SELECT * FROM attendance1 WHERE student_id = ? AND DATE(date) = CURDATE()";
                try (PreparedStatement checkPs = con.prepareStatement(checkAttendance)) {
                    checkPs.setString(1, student_id);
                    ResultSet resultSet = checkPs.executeQuery();

                    if (resultSet.next()) {
                        System.out.println("Attendance already marked for student " + student_id + " on the current date.");
                        return; // Do not proceed with marking attendance
                    }
                }

                System.out.println("Enter attendance status (Present or Absent):");
                String attendance_status = din.next();

                // Insert a new record for the current date
                String insertAttendance = "INSERT INTO attendance1 (student_id, attendance_status, date) VALUES (?, ?, NOW())";
                try (PreparedStatement insertPs = con.prepareStatement(insertAttendance)) {
                    insertPs.setString(1, student_id);
                    insertPs.setString(2, attendance_status);

                    int rowsAffected = insertPs.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Attendance marked successfully for student: " + student_id);
                    } else {
                        System.out.println("Failed to mark the attendance");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        static void register() {
            String sname, suser, spass, semail, smobile, subject = null;

            // Declare generatedOTP variable
            String generatedOTP;

            try {
                Connection con = DBConnectionClass.getConnection();
                System.out.println("Enter Staff Name");
                sname = din.next();
                System.out.println("Enter Staff UserName");
                suser = din.next();
                System.out.println("Enter Staff Password");
                spass = din.next();
                System.out.println("Enter Staff Email");
                semail = din.next();
                System.out.println("Enter Staff Mobile");
                smobile = din.next();

                // Generate and send OTP using OTPGenerator class
                generatedOTP = OTPGenerator.generateAndSendOTP(smobile);

                // Validate OTP before proceeding
                System.out.println("Please enter the OTP sent to your mobile number:");
                String userEnteredOTP = din.next();

                if (validateOTP(userEnteredOTP, generatedOTP)) {
                    System.out.println("OTP Authentication Successful.");

                    String sql = "insert into staff(sname, username, password, semail, smobile, status) values(?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, sname);
                    ps.setString(2, suser);
                    ps.setString(3, spass);
                    ps.setString(4, semail);
                    ps.setString(5, smobile);
                    ps.setString(6, "inactive");  // Initial status is set to inactive

                    int i = ps.executeUpdate();
                    if (i == 1) {
                        System.out.println("Staff Created Successfully");
                        System.out.println("Please Wait For A Second");
                        System.out.println("Contact Admin For Approval! Try Later");

                        String body = "Dear " + sname + ",\n\nYour registration is successful. Welcome to Ai School of Learning!";
                        sendEmail(semail, subject, body);
                    } else {
                        System.out.println("Application Failed To Send");
                    }
                } else {
                    System.out.println("Invalid OTP. Registration failed.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Email validation method
        static boolean isValidEmail(String email) {
            // Implement your email validation logic here
            // For simplicity, you can use a regular expression
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            return email.matches(emailRegex);
        }

	    static boolean validateOTP(String userEnteredOTP, String generatedOTP) {
	        // Implement your OTP validation logic here
	        return userEnteredOTP.equals(generatedOTP);
	    }

		
		    static void sendEmail(String semail, String subject, String body) {
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
		          

		            
		        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(semail));

		            
		            message.setSubject("Hai");

		            // Now set the actual message
		            message.setText("Dear [Recipient's Name],\r\n"
		                    + "\r\n"
		                    + "We are delighted to welcome you to [Ai School Of Learning]! As you join our team, we want to express our appreciation for your dedication and commitment to education.\r\n"
		                    + "\r\n"
		                    + "At [Ai Shool Of Learning], we believe in fostering a collaborative and supportive environment for both students and staff. Your contributions play a crucial role in creating an enriching educational experience.\r\n"
		                    + "\r\n"
		                    + "Here are some key topics to help you settle in:\r\n"
		                    + "- Onboarding Schedule: Please check your email for the detailed onboarding schedule to familiarize yourself with the campus and administrative procedures.\r\n"
		                    + "- Staff Resources: Explore the [Ai School Of Learning] staff portal to access important resources, including policies, documentation, and professional development opportunities.\r\n"
		                    + "- Campus Facilities: Familiarize yourself with the facilities available on our campus, including staff offices, meeting rooms, and common areas.\r\n"
		                    + "- Team Collaboration: Engage with your colleagues and participate in team-building activities to enhance collaboration and communication.\r\n"
		                    + "\r\n"
		                    + "If you have any questions or need assistance, please feel free to reach out to our team. We are here to support you in your role.\r\n"
		                    + "\r\n"
		                    + "Before you start your official duties, please visit the admin office for any final approvals and to complete any pending formalities.\r\n"
		                    + "\r\n"
		                    + "Wishing you a successful and rewarding journey with [Ai School Of Learning]!\r\n"
		                    + "\r\n"
		                    + "Best regards,\r\n"
		                    + "[Mohammad Sameer Khan]\r\n"
		                    + "[Admin]\r\n"
		                    + "[+918985166132]\r\n"
		                    + "");



		            // Send the message
		            Transport.send(message);

		            System.out.println("Email sent to: " + semail);
		        } catch (MessagingException e) {
		            e.printStackTrace();
		        }
		    }
		    private static void openMarksClass(Connection con) {
		        // Create an instance of the marks class and invoke its main method
		        
		        marks.main(new String[]{});
		    }
		    
	    }
		


