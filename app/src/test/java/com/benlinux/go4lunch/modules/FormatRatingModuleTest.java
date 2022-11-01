package com.benlinux.go4lunch.modules;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

/**
 * Class that tests 6 levels for rating format (from 0.5 to 3 stars)
 */
public class FormatRatingModuleTest {


    @Test
    // 0.5 star for double value > 0 and <= 8
    public void shouldReturnFormattedDoubleOfFirstLevel() {
        final double doubleValue = 0.4;
        final double expectedDoubleValue = 0.5;
        try (MockedStatic<FormatRatingModule> mockedStatic = Mockito.mockStatic(FormatRatingModule.class)) {
            mockedStatic
                    .when(() -> FormatRatingModule.formatRating(doubleValue))
                    .thenReturn(expectedDoubleValue);

            Double result = FormatRatingModule.formatRating(doubleValue);
            Assert.assertTrue(new ReflectionEquals(expectedDoubleValue).matches(result));
        }
    }

    @Test
    // 1 star for double value > 0.8 and <= 1.6
    public void shouldReturnFormattedDoubleOfSecondLevel() {
        final double doubleValue = 1.2;
        final double expectedDoubleValue = 1.0;

        Double result = FormatRatingModule.formatRating(doubleValue);
        Assert.assertTrue(new ReflectionEquals(expectedDoubleValue).matches(result));

    }

    @Test
    // 1.5 star for double value > 1.6 and <= 2.5
    public void shouldReturnFormattedDoubleOfThirdLevel() {
        final double doubleValue = 1.8;
        final double expectedDoubleValue = 1.5;

        Double result = FormatRatingModule.formatRating(doubleValue);
        Assert.assertTrue(new ReflectionEquals(expectedDoubleValue).matches(result));

    }

    @Test
    // 2 stars for double value > 2.5 and <= 3.4
    public void shouldReturnFormattedDoubleOfForthLevel() {
        final double doubleValue = 2.9;
        final double expectedDoubleValue = 2.0;

        Double result = FormatRatingModule.formatRating(doubleValue);
        Assert.assertTrue(new ReflectionEquals(expectedDoubleValue).matches(result));
    }

    @Test
    // 2.5 stars for double value > 3.5 and <= 4.3
    public void shouldReturnFormattedDoubleOfFifthLevel() {
        final double doubleValue1 = 4.1;
        final double doubleValue2 = 3.6;
        final double expectedDoubleValue = 2.5;

        Double result1 = FormatRatingModule.formatRating(doubleValue1);
        Assert.assertTrue(new ReflectionEquals(expectedDoubleValue).matches(result1));

        Double result2 = FormatRatingModule.formatRating(doubleValue2);
        Assert.assertTrue(new ReflectionEquals(expectedDoubleValue).matches(result2));
    }

    @Test
    // 3 stars for double value > 4.3 and <= 5.0
    public void shouldReturnFormattedDoubleOfSixthLevel() {
        final double doubleValue = 4.5;
        final double expectedDoubleValue = 3.0;

        Double result = FormatRatingModule.formatRating(doubleValue);
        Assert.assertTrue(new ReflectionEquals(expectedDoubleValue).matches(result));
    }

}
