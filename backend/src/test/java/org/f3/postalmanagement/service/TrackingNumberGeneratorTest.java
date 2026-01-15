package org.f3.postalmanagement.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrackingNumberGenerator Path Coverage Tests")
class TrackingNumberGeneratorTest {

    @InjectMocks
    private TrackingNumberGenerator trackingNumberGenerator;

    // ==================== generate() Tests ====================
    @Nested
    @DisplayName("generate()")
    class GenerateTests {

        @Test
        @DisplayName("Path 1: Generates tracking number with correct format")
        void generate_CorrectFormat() {
            String trackingNumber = trackingNumberGenerator.generate();

            assertThat(trackingNumber).isNotNull();
            assertThat(trackingNumber).startsWith("VN");
            assertThat(trackingNumber).hasSize(19); // VN + 13 digits timestamp + 4 digits sequence
        }

        @Test
        @DisplayName("Path 2: Generates unique tracking numbers")
        void generate_UniqueNumbers() {
            String first = trackingNumberGenerator.generate();
            String second = trackingNumberGenerator.generate();
            String third = trackingNumberGenerator.generate();

            assertThat(first).isNotEqualTo(second);
            assertThat(second).isNotEqualTo(third);
            assertThat(first).isNotEqualTo(third);
        }

        @Test
        @DisplayName("Path 3: Sequence increments correctly")
        void generate_SequenceIncrements() {
            String first = trackingNumberGenerator.generate();
            String second = trackingNumberGenerator.generate();

            // Both should have format VN + timestamp + sequence
            // Sequence should be different (incremented)
            String firstSequence = first.substring(first.length() - 4);
            String secondSequence = second.substring(second.length() - 4);

            int seq1 = Integer.parseInt(firstSequence);
            int seq2 = Integer.parseInt(secondSequence);

            // Second sequence should be 1 more than first (or wrap around at 10000)
            assertThat((seq2 - seq1 + 10000) % 10000).isEqualTo(1);
        }

        @Test
        @DisplayName("Path 4: Generated number passes format validation")
        void generate_PassesValidation() {
            String trackingNumber = trackingNumberGenerator.generate();

            boolean isValid = trackingNumberGenerator.isValidFormat(trackingNumber);

            assertThat(isValid).isTrue();
        }
    }

    // ==================== isValidFormat() Tests ====================
    @Nested
    @DisplayName("isValidFormat()")
    class IsValidFormatTests {

        @Test
        @DisplayName("Path 1: Valid format returns true")
        void isValidFormat_ValidFormat_ReturnsTrue() {
            boolean result = trackingNumberGenerator.isValidFormat("VN12345678901234567");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Path 2: Null input returns false")
        void isValidFormat_Null_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat(null);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 3: Empty string returns false")
        void isValidFormat_Empty_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 4: Blank string returns false")
        void isValidFormat_Blank_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("   ");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 5: Wrong prefix returns false")
        void isValidFormat_WrongPrefix_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("US12345678901234567");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 6: Too short returns false")
        void isValidFormat_TooShort_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("VN1234567890");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 7: Too long returns false")
        void isValidFormat_TooLong_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("VN123456789012345678");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 8: Contains letters in number part returns false")
        void isValidFormat_ContainsLetters_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("VN1234567890123456X");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 9: Contains special characters returns false")
        void isValidFormat_ContainsSpecialChars_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("VN1234567890123456!");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Path 10: Lowercase prefix returns false")
        void isValidFormat_LowercasePrefix_ReturnsFalse() {
            boolean result = trackingNumberGenerator.isValidFormat("vn12345678901234567");

            assertThat(result).isFalse();
        }
    }
}
