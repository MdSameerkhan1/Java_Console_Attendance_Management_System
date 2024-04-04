package com.ams.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.ams.color.CustomColor;

public class StudentList {
    public void List() {
        int choice;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println(CustomColor.BLUE+"\t 1. 1st Year Students List");
            System.out.println("\t 2. 2nd Year Students List");
            System.out.println("\t 3. Exit");
            System.out.print("Enter your option: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    displayStudents("1stYearStudents");
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "1234");

                       
                        String sql = "SELECT * FROM  1styearstudents";
                        PreparedStatement st = con.prepareStatement(sql);
                        ResultSet rs = st.executeQuery();

                       
                        while (rs.next()) {
                            System.out.println(CustomColor.SAFFRON+"Student Name =: "+ rs.getString("student_name")); 
                                           
                                           System.out.println("Gender =:" +rs.getString("gender")); 
                                        		   System.out.println("Course year =: "+rs.getString("course"));
           System.out.println("-----------------------------------------------------");
           
                        }

                        System.out.println();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    break;
                case 2:
                	displayStudents("2ndYearStudents");
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "1234");

                      
                        String sql = "SELECT * FROM  2ndyearstudents";
                        PreparedStatement st = con.prepareStatement(sql);
                        ResultSet rs = st.executeQuery();

                       
                        System.out.println(CustomColor.GREEN+"student_name\tgender\tcourse");

                        while (rs.next()) {
                            System.out.println(CustomColor.ROSY_PINK+
                                    
                                            rs.getString("student_name") + "\t" +
                                           
                                            rs.getString("gender") + "\t" +
                                            rs.getString("course")
                            );
                        }

                        System.out.println();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        } while (choice != 3);
    }

	private void displayStudents(String string) {
		
	}

	
    
    }

