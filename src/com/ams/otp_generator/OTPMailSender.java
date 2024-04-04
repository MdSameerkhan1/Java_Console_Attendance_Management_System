package com.ams.otp_generator;

import java.security.SecureRandom;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ams.color.CustomColor;

public class OTPMailSender {

    private static final String SENDER_EMAIL = "msk498767@gmail.com";
    private static final String SENDER_PASSWORD = "pckk aeul cbvf prbz";

    public static void main(String[] args) {
        try {
          
            String otp = generateOTP();

            String studentemail = null;
			// Send OTP via email
            sendOTPEmail(studentemail, otp);

            System.out.println(CustomColor.GREEN+"OTP sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static String generateOTP() {
        // Generate a 6-digit random OTP
        SecureRandom random = new SecureRandom();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    public static void sendOTPEmail(String recipientEmail, String otp) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "587"); 

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("OTP for Verification");
            message.setText("Your OTP is: " + otp);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean validateOTP(String userInputOTP, String generatedOTP) {
        // Compare the user-entered OTP with the generated OTP
        return userInputOTP.equals(generatedOTP);
    }
}
