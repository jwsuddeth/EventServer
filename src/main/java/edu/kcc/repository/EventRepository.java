package edu.kcc.repository;

import edu.kcc.entity.Event;

import java.sql.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class EventRepository {

    String url = "jdbc:mysql://localhost:3306/kcc";
    String user = "jsuddeth";
    String password = "IdkWtf501!";


    public List<Event> getAllEvents(){
        ArrayList<Event> events = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(url, user, password);) {


            PreparedStatement ps = conn.prepareStatement("SELECT id, event_name, event_date, event_time, description FROM events");
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                // convert sql data and time to local date and local time
                java.sql.Date sqlDateRow = rs.getDate("event_date");
                LocalDate eventDate = sqlDateRow.toLocalDate();

                java.sql.Time sqlTimeRow = rs.getTime("event_time");
                LocalTime eventTime = sqlTimeRow.toLocalTime();

                // create the event and set its data from the ResultSet
                Event event = new Event();

                event.setId(rs.getInt("id"));
                event.setTitle(rs.getString("event_name"));
                event.setEventDate(eventDate);
                event.setEventTime(eventTime);
                event.setDescription(rs.getString("description"));

                events.add(event);
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }

        return events;
    }


    public List<Event> getEventsForDate(LocalDate dt){
        ArrayList<Event> events = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(url, user, password);) {


            PreparedStatement ps = conn.prepareStatement("SELECT id, event_name, event_date, event_time, description FROM events WHERE event_date = ?");
            java.sql.Date sqlDate = java.sql.Date.valueOf(dt);

            ps.setDate(1, sqlDate);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                // convert sql data and time to local date and local time
                java.sql.Date sqlDateRow = rs.getDate("event_date");
                LocalDate eventDate = sqlDateRow.toLocalDate();

                java.sql.Time sqlTimeRow = rs.getTime("event_time");
                LocalTime eventTime = sqlTimeRow.toLocalTime();

                // create the event and set its data from the ResultSet
                Event event = new Event();

                event.setId(rs.getInt("id"));
                event.setTitle(rs.getString("event_name"));
                event.setEventDate(eventDate);
                event.setEventTime(eventTime);
                event.setDescription(rs.getString("description"));

                events.add(event);
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }

        return events;
    }

    public Event createEvent(String name, LocalDate dt, LocalTime tm, String description){

        // create the event with a 0 ID
        Event evt = new Event(0, name, dt, tm, description);

        // add it to the database
        try(Connection conn = DriverManager.getConnection(url, user, password);){

            // date and time need to be converted to SQL types
            java.sql.Date sqlDate = dtToSql(dt);
            java.sql.Time sqlTime = tmToSql(tm);

            CallableStatement cs = conn.prepareCall("{call CreateEvent(?, ?, ?, ?, ?)}");
            cs.setString(1, name);
            cs.setDate(2, sqlDate);
            cs.setTime(3, sqlTime);
            cs.setString(4, description);

            int updatedRows = cs.executeUpdate();
            if (updatedRows == 1){
                evt.setId(cs.getInt("newId"));
                return evt;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        // set the new ID
        return null;
    }

    public java.sql.Date dtToSql(LocalDate dt){

        return java.sql.Date.valueOf(dt);
    }

    public LocalDate sqlToDt(java.sql.Date sqlDate){

        return sqlDate.toLocalDate();
    }

    public java.sql.Time tmToSql(LocalTime tm){
        return java.sql.Time.valueOf(tm);
    }

    public LocalTime sqlToTm(java.sql.Time sqlTime){
        return sqlTime.toLocalTime();
    }
}
