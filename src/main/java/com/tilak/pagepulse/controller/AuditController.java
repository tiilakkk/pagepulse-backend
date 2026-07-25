package com.tilak.pagepulse.controller;

import com.tilak.pagepulse.dto.AuditRequest;
import com.tilak.pagepulse.dto.AuditResponse;
import com.tilak.pagepulse.service.AuditService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/audit")
    public AuditResponse auditWebsite(@RequestBody AuditRequest request) {
        return auditService.auditWebsite(request);
    }
}