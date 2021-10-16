package fido;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatabaseHandler extends JDBC{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException{
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection=DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public void signUpUser(String username, String email, String password){
        String insert = "INSERT INTO " +constant.USER_TABLE + "(" + constant.USER_USERNAME + "," + constant.USER_EMAIL + "," +
                constant.USER_PASSWORD + ")" + "VALUES(?,?,md5(?))";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);

            prSt.setString(1,username);
            prSt.setString(2,email);
            prSt.setString(3, password);


            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createRoom(String roomName, String location, int capacity){
        String insert = "INSERT INTO " +constant.ROOMS_TABLE + "(" + constant.ROOMS_ROOMNAME + "," + constant.ROOMS_LOCATION + "," +
                constant.ROOMS_CAPACITY +")" + "VALUES(?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);

            prSt.setString(1,roomName);
            prSt.setString(2,location);
            prSt.setInt(3, capacity);


            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteRoom(String roomName){
        String delete = "DELETE FROM " + constant.ROOMS_TABLE + " WHERE " +
                constant.ROOMS_ROOMNAME + "=?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(delete);

            prSt.setString(1,roomName);


            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteUser(String email, String password){
        String delete = "DELETE FROM " + constant.USER_TABLE + " WHERE " +
                constant.USER_EMAIL + "=? AND " + constant.USER_PASSWORD + "=md5(?)";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(delete);

            prSt.setString(1,email);
            prSt.setString(2, password);


            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getUser(String email, String password) throws Exception {
        ResultSet resSet = null;

        String select = "SELECT  * FROM " + constant.USER_TABLE + " WHERE " +
                constant.USER_EMAIL + "=? AND " + constant.USER_PASSWORD + "=md5(?)" ;
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);

            prSt.setString(1,email);
            prSt.setString(2,password);
            resSet = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  resSet;
    }
    public ResultSet checkRoomAvailability(Timestamp startDate, Timestamp endDate) throws Exception {
        ResultSet rtSt = null;
        PreparedStatement prSt = null;
        Connection con = null;
        String select = "SELECT  * FROM " + constant.DATE_TABLE + " WHERE ? >" +constant.DATE_STARTDATE + " AND ?<" + constant.DATE_ENDDATE + " OR ?>" + constant.DATE_STARTDATE + " AND ?<" + constant.DATE_ENDDATE
                + " OR ?<=" + constant.DATE_STARTDATE + " AND ?>=" +constant.DATE_ENDDATE;
        try {
            con = getDbConnection();
            prSt = con.prepareStatement(select);
            System.out.println(startDate);

            prSt.setTimestamp(1, startDate);
            prSt.setTimestamp(2, startDate);
            prSt.setTimestamp(3, endDate);
            prSt.setTimestamp(4, endDate);
            prSt.setTimestamp(5, startDate);
            prSt.setTimestamp(6, endDate);
            System.out.println(prSt);
            rtSt = prSt.executeQuery();
            return rtSt;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return rtSt;
    }


    public void rentTime(Timestamp startDate, Timestamp endDate){
        String insert = "INSERT INTO "
                +constant.DATE_TABLE + "(" + constant.DATE_STARTDATE + "," + constant.DATE_ENDDATE + ")" + "VALUES(?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);

            prSt.setTimestamp(1, startDate);
            prSt.setTimestamp(2, endDate);


            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
