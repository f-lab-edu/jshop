package jshop.utils;

import java.util.HashMap;
import java.util.Map;
import jshop.global.annotation.CurrentUserId;
import jshop.global.annotation.CurrentUserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/test")
    public Object test3(@RequestBody Object object) {
        return object;
    }

}
