package com.nisum.bci.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MaskingUtilsTest {

    @Test
    void maskEmail_shouldKeepFirstLetterAndDomain() {
        assertEquals("j***@rodriguez.org", MaskingUtils.maskEmail("juan@rodriguez.org"));
    }

    @Test
    void maskEmail_shouldMaskAll_whenEmailHasNoLocalPart() {
        assertEquals("***", MaskingUtils.maskEmail("@rodriguez.org"));
        assertEquals("***", MaskingUtils.maskEmail("juan.rodriguez.org"));
    }

    @Test
    void maskEmail_shouldReturnSameValue_whenEmailIsNullOrBlank() {
        assertNull(MaskingUtils.maskEmail(null));
        assertEquals(" ", MaskingUtils.maskEmail(" "));
    }
}
