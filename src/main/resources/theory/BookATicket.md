# Book a Ticket High-level architecture

## Database Structure
Users(UserId, FullName, Email, PasswordHash)
Cinemas(CinemaId, Name, Address)
Movies(MovieId, Title, Genre, Duration)
Screenings(ScreeningId, CinemaHallId, MovieId, DateTime)
Booking(TicketId, ScreeningId, UserId, SeatNumber)
Notification(TicketId, ScreeningId, UserId, SeatNumber)
Payments(PaymentId, TicketId, UserId, Status, TransactionDate)

## API endpoints
### User Service API
Manages user data and emits events relating to user actions.
POST /users/register - Triggers UserRegistered event
POST /users/login - Triggers UserLoggedIn event
DELETE /users/{userId}/unregister - Triggers UserUnregistered event

### Cinema Service API
Manages cinema - cinema locations, number of halls in each cinema, hall capacities, etc.
POST /cinemas/ - Triggers CinemaCreated event
PATCH /cinemas/{cinemaId} - Triggers CinemaUpdated event
DELETE /cinemas/{cinemaId} - Triggers CinemaDeleted event
POST /cinemas/{cinemaId}/halls - Triggers HallCreated event

### Movies Service API
Handles details about movies.
POST /movies/ - Triggers MovieAdded event
PATCH /movies/{movieId} - Triggers MovieUpdated event
DELETE /movies/{movieId} - Triggers MovieRemoved event

### Screenings Service API
Handles movie screenings schedules, i.e., which movie is screened when and where.
GET /screenings/ - Returns a list of all active screenings (no event, no change in state)
GET /screenings/{screeningId} - Returns a specific screening (no event, no change in state)
GET /screenings/{screeningId}/seats - Returns a layout of the hall and the current booking status of each seat
POST /screenings/ - Triggers ScreeningScheduled event
PATCH /screenings/{screeningId} - Triggers ScreeningUpdated event
DELETE /screenings/{screeningId} - Triggers ScreeningCancelled event

### Booking Service API
Orchestrates the booking process. This would be the primary service that consumes events and triggers
further events.
POST /tickets/book - Triggers TicketBookingAttempted event

### Payment Service API
Processes payments for ticket bookings.
POST /payments - Triggers PaymentAttempted event

### Notification Service API
Sends notifications to users (e.g., booking confirmation).
The notification service would not expose endpoints, instead it would handle TicketBooked events and send notifications.

## Process
1) A user logs into the application. The /users/login endpoint in the **User Service** is invoked which emits a UserLoggedIn 
event. User details can also be returned to the client at this point.
2) The user browses through screenings. This invokes the **Screenings Service** to get a list of all active screenings.
3) The user picks a screening.
4) The user books a ticket. This triggers a POST request to the /tickets/book endpoint at the **Booking Service**. This 
does not immediately book a ticket, but instead the service publishes a TicketBookingAttempted event to the message 
queue.
5) The **Booking Service** is listening on the TicketBookingAttempted event. It validates the request (checks seat
availability, etc.) and, if the booking request is valid, updates its own storage, then publishes a TicketReserved 
event. It marks the seat as reserved. If the booking can't be done (due to unavailability or any other reason), it emits 
a TicketReservingFailed event.
6) The **Notification Service** is listening for these events TicketReserved and TicketReservingFailed. On receiving 
one of these, it sends a notification (it could be an email or an SMS) to the user indicating whether the operation was
successful (it will also tell that it is reserved for 15 minutes waiting for payment) or failed.
7) The user then has this duration to complete the payment for their booking. They can do this by making
a POST /payments request to the **Payment Service**, including the booking details. If the Payment Service confirms 
the payment, it will emit a PaymentSuccessful event. If the Payment Service doesn't receive a payment within 
the reserved duration for the booked seat, or if it receives a failed payment, it will emit a PaymentFailed event.
8) The **Booking Service** is listening for these events PaymentSuccessful and PaymentFailed. On this event finalizes 
the booking, and emits a TicketBooked event or TicketBookingFailed.
9) The **Notification Service** is listening for these events TicketBooked and TicketBookingFailed. On receiving one of 
these, it sends a notification (it could be an email or an SMS) to the user indicating whether the operation was 
successful or failed.
10) On part of the frontend or the client, after the POST /tickets/book call, it waits for a response from 
the **Notification Service** to inform the user about the status of their ticket booking request.

## Note
Be careful about business integrity with transactions to properly track seat bookings and payments.
