package com.mockitotutorial.happyhotel.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.LenientStubber;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MakeBookingTest {

    @InjectMocks
    BookingService bookingService;

    @Mock
    PaymentService paymentServiceMock;

    @Mock
    RoomService roomServiceMock;

    @Spy
    BookingDAO bookingDAOMock;

    @Mock
    MailSender mailSenderMock;

    @Captor
    ArgumentCaptor<Double> doubleCaptor;

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking simulating a thrown exception
    //-------------------------------------------------------------------------------------------------//
    @Test
    void throwExceptionWithoutAvailableRooms() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                false);

        when(this.roomServiceMock.findAvailableRoomId(bookingRequest)).thenThrow(BusinessException.class);

        //when
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        //then
        assertThrows(BusinessException.class, executable);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking simulating a thrown exception with mockito arguments
    //-------------------------------------------------------------------------------------------------//
    @Test
    void throwExceptionWithoutThePriceIsTooHigh() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        when(this.paymentServiceMock.pay(any(),anyDouble())).thenThrow(BusinessException.class);

        //when
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        //then
        assertThrows(BusinessException.class, executable);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking verifying only one specific interaction
    //-------------------------------------------------------------------------------------------------//
    @Test
    void makeBookingWithPrePaidOptionTrue() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        //when
        bookingService.makeBooking(bookingRequest);

        //then
        verify(paymentServiceMock,times(1)).pay(bookingRequest,400.0);
        verifyNoMoreInteractions(paymentServiceMock);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking to ensure that paymentService is never called when pre-payment isn't made
    //-------------------------------------------------------------------------------------------------//
    @Test
    void makeBookingWithPrePaidOptionFalse() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                false);

        //when
        bookingService.makeBooking(bookingRequest);

        //then
        verify(paymentServiceMock,never()).pay(any(),anyDouble());
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking to save booking request in DAO
    //-------------------------------------------------------------------------------------------------//
    @Test
    void makeBookingWithCorrectInput() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        //when
        bookingService.makeBooking(bookingRequest);

        //then
        verify(bookingDAOMock).save(bookingRequest);

    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking forcing exception with mailSenderMock
    //-------------------------------------------------------------------------------------------------//
    @Test
    void throwExceptionWithReadyMail() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        doThrow(new BusinessException()).when(mailSenderMock).sendBookingConfirmation(any());

        //when
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        //then
        assertThrows(BusinessException.class, executable);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking forcing to do nothing with mailSenderMock
    //-------------------------------------------------------------------------------------------------//
    @Test
    void throwExceptionWithoutReadyMail() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        doNothing().when(mailSenderMock).sendBookingConfirmation(any());

        //when
        bookingService.makeBooking(bookingRequest);

        //then
        // do nothing (no exception thrown)
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking using Argument Captors
    //-------------------------------------------------------------------------------------------------//
    @Test
    void makeBookingWithCapturedDoubleArgumentAsPrice() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        //when
        bookingService.makeBooking(bookingRequest);

        //then
        verify(paymentServiceMock,times(1)).pay(eq(bookingRequest),doubleCaptor.capture());
        double capturedArgument = doubleCaptor.getValue();

        assertEquals(400.0,capturedArgument);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking using multiples Argument Captors
    //-------------------------------------------------------------------------------------------------//
    @Test
    void makeBookingWithMultipleCapturedDoubleArgumentsAsPrice() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                true);

        BookingRequest bookingRequest2 = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,03),
                1,
                true);

        List<Double> expectedValues = Arrays.asList(400.0,100.0);

        //when
        bookingService.makeBooking(bookingRequest);
        bookingService.makeBooking(bookingRequest2);

        //then
        verify(paymentServiceMock,times(2)).pay(any(),doubleCaptor.capture());
        List<Double> capturedArguments = doubleCaptor.getAllValues();

        assertEquals(expectedValues,capturedArguments);
    }
    //-------------------------------------------------------------------------------------------------//
    // Test method makeBooking not calling paymentServiceMock (Strict Stub) and using Lenient
    //-------------------------------------------------------------------------------------------------//
    @Test
    void makeBookingWithoutPreApprovedPaymentUsingLenient() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1",
                LocalDate.of(2022,01,01),
                LocalDate.of(2022,01,05),
                2,
                false);

        lenient().when(this.paymentServiceMock.pay(any(),anyDouble())).thenReturn("1");

        //when
        bookingService.makeBooking(bookingRequest);

        //then
        //do nothing (no exception is thrown)
    }
}
