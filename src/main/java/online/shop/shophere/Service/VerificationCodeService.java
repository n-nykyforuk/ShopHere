package online.shop.shophere.Service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VerificationCodeService {

    private static final int CODE_LENGTH = 5;

    public String generateCode() {
        Random random = new Random();
        int code = 10000 + random.nextInt(90000);
        return String.valueOf(code);
    }
}