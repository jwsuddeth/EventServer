package edu.kcc;

import edu.kcc.repository.EventRepository;

public class Main {
    public static void main(String[] args) {


        EventRepository repo = new EventRepository();
        var events = repo.getAllEvents();

        events.forEach(System.out::println);

//        Event newEvent = repo.createEvent("Brunch with Mike", LocalDate.of(2024, 7, 18), LocalTime.of(10, 30, 0, 0), "Meet at Brickstone");
//        System.out.println("Events for 2024-07-18");
//        System.out.println("+++++++++++++++++++++++");
//
//        var eventList = repo.getEventsForDate(LocalDate.of(2024, 7, 18));
//
//        eventList.forEach(System.out::println);
    }
}