package com.tilak.pagepulse.service;

import com.tilak.pagepulse.dto.AuditRequest;
import com.tilak.pagepulse.dto.AuditResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    private final AuditService auditService = new AuditService();

    @Test
    void testAuditWebsiteSuccess() {

        AuditRequest request = new AuditRequest("https://github.com");

        AuditResponse response = auditService.auditWebsite(request);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getTitle());
        assertFalse(response.getTitle().isBlank());
        assertTrue(response.getWordCount() > 0);

    }

    @Test
    void testInvalidUrl() {

        AuditRequest request = new AuditRequest("hello");

        AuditResponse response = auditService.auditWebsite(request);

        assertEquals(0, response.getStatus());
        assertTrue(response.getMetaDescription().contains("Invalid"));

    }

    @Test
    void testNonHtmlPage() {

        AuditRequest request = new AuditRequest(
                "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        );

        AuditResponse response = auditService.auditWebsite(request);

        assertEquals(403, response.getStatus());
        assertEquals("Website returned HTTP 403", response.getMetaDescription());
    }
}