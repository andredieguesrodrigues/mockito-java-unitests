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
class CalculatePricingTest {

    @InjectMocks
    BookingService bookingService;

    //-------------------------------------------------------------------------------------------------//
    // Test method calculatePrice without dependencies
    //-------------------------------------------------------------------------------------------------//
    @Test
    void calculateCorrectPriceWithCorrectInput() {
        //given
        BookingRequest bookingRequest = new BookingRequest("1", 
                LocalDate.of(2022,01,01), 
                LocalDate.of(2022,01,05),
                2, 
                false);

        double expect = 4 * 2 * 50.0;

        //when
        double actual = bookingService.calculatePrice(bookingRequest);

        //then
        assertEquals(expect, actual);
    }

    //-------------------------------------------------------------------------------------------------//
    // Test method calculatePrice using mocked currency converter for the static method toEuro()
    //-------------------------------------------------------------------------------------------------//
    @Test
    void calculateCorrectPriceInEuroWithCorrectConvertedInput() {
        try(MockedStatic<CurrencyConverter> mockedConverter = mockStatic(CurrencyConverter.class)) {

            //given
            BookingRequest bookingRequest = new BookingRequest("1",
                    LocalDate.of(2022, 01, 01),
                    LocalDate.of(2022, 01, 05),
                    2,
                    false);

            double expect = 400 * 0.8;
            
            mockedConverter.when(() -> CurrencyConverter.toEuro(anyDouble()))
                    .thenAnswer(inv -> (double) inv.getArgument(0) * 0.8);

            //when
            double actual = bookingService.calculatePriceEuro(bookingRequest);

            //then
            assertEquals(expect, actual);
        }
    }

}
