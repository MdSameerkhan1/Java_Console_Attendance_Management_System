package com.ams.otp_generator;

import com.ams.color.CustomColor;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class OTPGenerator {

    // Replace these values with your Twilio Account SID and Auth Token
	 private static final String ACCOUNT_SID = "ACd57aaf953b950c1ff8e2d81e1b48220f";
	    private static final String AUTH_TOKEN = "0f297bd3cddae9bcbfe7ab34e4ce1567";
    private static final String TWILIO_PHONE_NUMBER = "+15512267803";

    // Method to generate and send OTP using Twilio
    public static String generateAndSendOTP(String phoneNumber) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Generate a 6-digit OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Send OTP via Twilio SMS
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                "Your OTP for registration: " + otp)
                .create();

        System.out.println(CustomColor.BLUE+"Twilio Message SID: " + message.getSid());
        return otp;
    }
}

