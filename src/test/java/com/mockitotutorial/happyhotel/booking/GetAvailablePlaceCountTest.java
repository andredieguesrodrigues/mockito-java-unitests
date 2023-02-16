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
class GetAvailablePlaceCountTest {

    @InjectMocks
    BookingService bookingService;

    @Mock
    RoomService roomServiceMock;

    //-------------------------------------------------------------------------------------------------//
    // Test method getAvailablePlaceCount with one occurence of roomServiceMock (dependency)
    //-------------------------------------------------------------------------------------------------//
    @Test
    void countAvailablePlacesWithOnlyOneAvailableRoom() {
        //given
        when(roomServiceMock.getAvailableRooms())
                .thenReturn(Collections.singletonList(new Room("Room 1", 5)));

        int expect = 5;

        //when
        int actual = bookingService.getAvailablePlaceCount();

        //then
        assertEquals(expect, actual);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method getAvailablePlaceCount with multiple occurences of roomServiceMock (dependency)
    //-------------------------------------------------------------------------------------------------//
    @Test
    void countAvailablePlacesWithMultipleAvailableRooms() {
        //given
        List<Room> rooms = Arrays.asList(new Room("Room 1",5), new Room("Room 1",5));

        when(roomServiceMock.getAvailableRooms())
                .thenReturn(rooms);

        int expect = 10;

        //when
        int actual = bookingService.getAvailablePlaceCount();

        //then
        assertEquals(expect,actual);
    }

    void firstAssert(int a, int b) {
        assertEquals(a,b);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method getAvailablePlaceCount with multiple requests of roomServiceMock (dependency)
    //-------------------------------------------------------------------------------------------------//
    @Test
    void countAvailablePlacesWithOnlyOneAvailableRoomByMultipleTimes() {
        //given
        when(roomServiceMock.getAvailableRooms())
                .thenReturn(Collections.singletonList(new Room("Room 1", 5)))
                .thenReturn(Collections.emptyList());

        int expectFirstCall = 5;
        int expectSecondCall = 0;

        //when
        int actualFirstCall = bookingService.getAvailablePlaceCount();
        int actualSecondCall = bookingService.getAvailablePlaceCount();

        //then
        assertAll(
                () -> assertEquals(expectFirstCall, actualFirstCall),
                () -> assertEquals(expectSecondCall, actualSecondCall));
    }
}
