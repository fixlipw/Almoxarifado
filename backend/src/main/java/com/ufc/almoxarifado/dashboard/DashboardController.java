package com.ufc.almoxarifado.dashboard;

import com.ufc.almoxarifado.usuario.AuthSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthSecurityService authSecurityService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        boolean managerial = authSecurityService.hasAdminOrStaffAccess(authentication);
        Long userId = managerial ? null : authSecurityService.getCurrentUserId(authentication);
        return ResponseEntity.ok(dashboardService.getDashboard(userId, managerial));
    }
}
