package edu.kcc;


import edu.kcc.entity.Event;
import edu.kcc.repository.EventRepository;
import edu.kcc.request.GetEventRequest;
import edu.kcc.response.GetEventResponse;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.List;


class ClientThread implements Runnable {
    private final Socket client;
    private final EventRepository repo = new EventRepository();

    public ClientThread(Socket c) {
        client = c;
    }

    @Override
    public void run(){

        try(InputStream inStream = client.getInputStream();
            OutputStream outStream = client.getOutputStream();
            var in = new ObjectInputStream(inStream);
            var out = new ObjectOutputStream(outStream);){

            System.out.println("We have a new connection!!!");

            GetEventResponse response = new GetEventResponse();
            boolean done = false;
            while(!done ){

                // read a GetEventRequest
                try {
                    GetEventRequest req = (GetEventRequest) in.readObject();

                    if (req == null){
                        done = true;
                        break;
                    }

                    System.out.println("Received Request: " + req);

                    // create the response
                    response = new GetEventResponse();

                    if (req.getCommand().equalsIgnoreCase("GetByDate")){
                        if (req.getDate() == null){
                            throw new Exception("Date can not be null!");
                        }

                        List<Event> eventList  = repo.getEventsForDate(req.getDate());
                        response.setStatusCode(200);
                        response.setErrorMessage("");
                        response.setEvents(eventList);
                    }
                    else if (req.getCommand().equalsIgnoreCase("GetAll")){
                        List<Event> eventList  = repo.getAllEvents();
                        response.setStatusCode(200);
                        response.setErrorMessage("");
                        response.setEvents(eventList);
                    }
                    else if (req.getCommand().equalsIgnoreCase(("Create"))){

                        Event newEvent = repo.createEvent(req.getCreateEventData().getTitle(), req.getCreateEventData().getEventDate(), req.getCreateEventData().getEventTime(), req.getCreateEventData().getDescription());

                        if (newEvent != null){
                            response.setEvents(List.of(newEvent));
                            response.setStatusCode(200);
                            response.setErrorMessage("");
                        }
                        else{
                            response.setStatusCode(400);
                            response.setErrorMessage("Failed to create the event");
                        }
                    }
                    else if (req.getCommand().equalsIgnoreCase("Disconnect")){
                        // return from here. this will close the socket and
                        // end the thread
                        return;
                    }
                    else{
                        // invalid command
                        response.setStatusCode(400);
                        response.setErrorMessage(req.getCommand() + " is not a valid command");
                    }
                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                catch(Exception e){
                    // invalid command
                    response.setStatusCode(400);
                    response.setErrorMessage(e.getMessage());
                }

                // send back the response
                out.writeObject(response);

            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}


public class ServerMain {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream("config/application.properties"));
        String portProperty = properties.getProperty("edu.kcc.Main.port", "9876");

        int port = Integer.parseInt(portProperty);
        System.out.println("Starting server... Listing on port " + port);

        try (var server = new ServerSocket(port)) {

            int i = 1;

            while (true) {
                Socket incoming = server.accept();
                Runnable r = new ClientThread(incoming);
                var t = new Thread(r);
                t.start();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


