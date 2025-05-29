package online.shop.shophere.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class HomeController {

    @GetMapping
    public String showHome() {
        return "home";
    }
}