package com.ams.staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import com.ams.connection.DBConnectionClass;

public class marks {
	  
	static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        try {
          
            
            Connection con =DBConnectionClass.getConnection();
           
           

            System.out.print("Enter Year (1 or 2): ");
            int year = scanner.nextInt();

            if (year == 1 || year == 2) {
                System.out.println("Insert Marks for Year " + year + " Students");

                System.out.print("Enter Student ID: ");
                int studentId = scanner.nextInt();

                System.out.print("Enter Marks for R1: ");
                int r1 = scanner.nextInt();

                System.out.print("Enter Marks for R2: ");
                int r2 = scanner.nextInt();

                System.out.print("Enter Marks for R3: ");
                int r3 = scanner.nextInt();

                System.out.print("Enter Final Marks: ");
                int finalMark = scanner.nextInt();

                if (year == 1) {
                    insertMarksFor1stYear(con, studentId, r1, r2, r3, finalMark);
                } else {
                    insertMarksFor2ndYear(con, studentId, r1, r2, r3, finalMark);
                }

                System.out.println("Marks inserted successfully!");
            } else {
                System.out.println("Invalid year. Please enter either 1 or 2.");
            }

           
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
	private static void insertMarksFor1stYear(Connection connection, int studentId, int r1, int r2, int r3, int finalMark) throws SQLException {
        String sql = "INSERT INTO 1styearstudents_marks (student_id, r1, r2, r3, final) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, r1);
            preparedStatement.setInt(3, r2);
            preparedStatement.setInt(4, r3);
            preparedStatement.setInt(5, finalMark);
            preparedStatement.executeUpdate();
        }
    }

    private static void insertMarksFor2ndYear(Connection connection, int studentId, int r1, int r2, int r3, int finalMark) throws SQLException {
        String sql = "INSERT INTO 2ndyearstudents_marks (student_id, r1, r2, r3, final) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, r1);
            preparedStatement.setInt(3, r2);
            preparedStatement.setInt(4, r3);
            preparedStatement.setInt(5, finalMark);
            preparedStatement.executeUpdate();
        }
    }

    
}

