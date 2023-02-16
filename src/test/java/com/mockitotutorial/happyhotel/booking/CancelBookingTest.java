package com.mockitotutorial.happyhotel.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelBookingTest {

    @InjectMocks
    BookingService bookingService;

    @Mock
    RoomService roomServiceMock;

    @Spy
    BookingDAO bookingDAOMock;

    //-------------------------------------------------------------------------------------------------//
    // Test method cancelBooking injecting specific bookingId to DAO
    //-------------------------------------------------------------------------------------------------//
    @Test
    void cancelBookingWithCorrectInput() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        bookingRequest.setRoomId("1.3");
        String bookingId = "1";

        doReturn(bookingRequest).when(bookingDAOMock).get(bookingId);

        //when
        bookingService.cancelBooking(bookingId);

    }
}
