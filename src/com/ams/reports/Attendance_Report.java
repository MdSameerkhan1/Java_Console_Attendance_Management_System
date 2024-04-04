package com.ams.reports;

import java.io.FileOutputStream;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;

import java.io.File;

import com.ams.color.CustomColor;
import com.ams.connection.DBConnectionClass;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Attendance_Report {
	
    public  void report() {
        try {
          Connection con = DBConnectionClass.getConnection();
          Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from attendance1 ");
            Document doc = new Document();
            String filePath = "Attendance.pdf"; 
            PdfWriter.getInstance(doc, new FileOutputStream("Attendance.pdf"));
            doc.open();
            Font f = FontFactory.getFont(FontFactory.COURIER, 45, BaseColor.GREEN);
            PdfPTable table = new PdfPTable(3); // Virtual table // 3 columns
            table.addCell("student_id");
            table.addCell("Date");
            table.addCell("status");
            Chunk ch = new Chunk("Attendance Details", f);
            Font f1 = FontFactory.getFont(FontFactory.COURIER, 15, BaseColor.BLACK);
            doc.add(ch);
            while (rs.next()) {
                String sid = String.valueOf(rs.getInt(1));
                table.addCell(sid);
                table.addCell(rs.getString(2));
                table.addCell(rs.getString(3));
                
            }
            doc.add(table);
            doc.close();
            System.out.println(CustomColor.RED_ITALIC+"Attendance report generated successfully. File saved at: " + new File(filePath).getAbsolutePath());
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void report2() {
        try {
            Connection con = DBConnectionClass.getConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from marks ");
            Document doc = new Document();
            String filePath = "Marks.pdf"; 
            PdfWriter.getInstance(doc, new FileOutputStream("Marks.pdf"));
            doc.open();
            Font f = FontFactory.getFont(FontFactory.COURIER, 45, BaseColor.BLACK);
            PdfPTable table = new PdfPTable(2); // Virtual table // 2 columns
            table.addCell("student_id");
            table.addCell("marks");
            Chunk ch = new Chunk("Marks Details", f);
            Font f1 = FontFactory.getFont(FontFactory.COURIER, 15, BaseColor.BLACK);
            doc.add(ch);
            while (rs.next()) {
                String student_id = String.valueOf(rs.getInt(1));
                table.addCell(student_id);
                table.addCell(rs.getString(2));
            }
            doc.add(table);
            doc.close();
            System.out.println(CustomColor.RED_ITALIC+" report generated successfully. File saved at: " + new File(filePath).getAbsolutePath());
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
