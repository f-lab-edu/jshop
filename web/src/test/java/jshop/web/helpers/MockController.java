package jshop.web.helpers;

import java.util.HashMap;
import java.util.Map;
import jshop.web.security.annotation.CurrentUserId;
import jshop.web.security.annotation.CurrentUserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MockController {

    @GetMapping("/userid")
    public Map<String, Long> test(@CurrentUserId Long userId) {
        Map<String, Long> response = new HashMap<>();
        response.put("userid", userId);
        return response;
    }

    @GetMapping("/userrole")
    public Map<String, String> test2(@CurrentUserRole String userRole) {
        Map<String, String> response = new HashMap<>();
        response.put("userRole", userRole);
        return response;
    }
}
