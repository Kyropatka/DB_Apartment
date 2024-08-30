package com.gmail.deniska1406sme;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApartmentDatabase {

    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydb1?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "Kyropatka14!";

    public static void main( String[] args ) {

        try(Connection conn = DriverManager.getConnection(DB_CONNECTION,DB_USER,DB_PASSWORD)){
            try (Statement st = conn.createStatement()){
                st.execute("DROP TABLE IF EXISTS Apartment");
                st.execute("CREATE TABLE Apartments (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, district VARCHAR(255) DEFAULT NULL" +
                        ", address VARCHAR(255) DEFAULT NULL, area DOUBLE DEFAULT NULL, rooms INT DEFAULT NULL, price DOUBLE DEFAULT NULL)");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        addApartment("centre","main 1",70.0,2, 25000);
        addApartment("centre","main 2",90.0,3, 35000);
        addApartment("centre","main 3",40.0,1, 15000);
        addApartment("centre","main 4",120.0,4, 50000);
        addApartment("downtown","studentskaya 15",140.0,4, 70000);
        addApartment("downtown","studentskaya 15",140.0,5, 60000);

        List<Apartment> apartments = getApartments("centre",40.0,90.0,0,0,30000);
        for(Apartment apartment : apartments){
            System.out.println(apartment);
        }

    }

    public static void addApartment(String district, String address, double area, int rooms, double price) {
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (district,address,area,rooms,price)" +
                     "VALUES (?,?,?,?,?)")) {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setDouble(3, area);
            ps.setInt(4, rooms);
            ps.setDouble(5, price);

            ps.executeUpdate();
            System.out.println("Apartment added");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Apartment> getApartments(String district, double minArea, double maxArea, int rooms,
                                                double minPrice, double maxPrice) {
        List<Apartment> apartments = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM Apartments WHERE 1=1");

        if (district != null) sb.append(" AND district = ?");
        if (minArea != 0) sb.append(" AND area >= ?");
        if (maxArea != 0) sb.append(" AND area <= ?");
        if (rooms != 0) sb.append(" AND rooms = ? ");
        if (minPrice != 0) sb.append(" AND price >= ?");
        if (maxPrice != 0) sb.append(" AND price <= ?");

        try (Connection conn = DriverManager.getConnection(DB_CONNECTION,DB_USER,DB_PASSWORD);
        PreparedStatement ps = conn.prepareStatement(sb.toString())){

            int i = 1;
            if (district != null) ps.setString(i++, district);
            if (minArea != 0) ps.setDouble(i++, minArea);
            if (maxArea != 0) ps.setDouble(i++, maxArea);
            if (rooms != 0) ps.setInt(i++, rooms);
            if (minPrice != 0) ps.setDouble(i++, minPrice);
            if (maxPrice != 0) ps.setDouble(i++, maxPrice);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = new Apartment(rs.getInt("id"), rs.getString("district"),
                        rs.getString("address"), rs.getDouble("area"),rs.getInt("rooms"),
                        rs.getDouble("price"));
                apartments.add(apartment);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return apartments;
    }
}
