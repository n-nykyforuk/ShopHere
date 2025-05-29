package online.shop.shophere.Controller;

import jakarta.servlet.http.HttpSession;
import online.shop.shophere.Entity.Advertisement;
import online.shop.shophere.Entity.User;
import online.shop.shophere.Service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/advertisement")
public class AdvertisementController {

    private static final Logger logger = LoggerFactory.getLogger(AdvertisementController.class);
    private final AdvertisementService advertisementService;
    private final UserService userService;
    private final FavoriteService favoriteService;
    private EmailService emailService;
    private VerificationCodeService verificationCodeService;

    public AdvertisementController(AdvertisementService advertisementService, UserService userService, FavoriteService favoriteService, EmailService emailService, VerificationCodeService verificationCodeService) {
        this.advertisementService = advertisementService;
        this.userService = userService;
        this.favoriteService = favoriteService;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/change-password")
    public String initiateChangePassword(Authentication authentication, HttpSession session) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        String code = verificationCodeService.generateCode();
        session.setAttribute("verificationCode", code);
        session.setAttribute("codeTimestamp", System.currentTimeMillis());
        emailService.sendVerificationCode(user.getEmail(), code);
        return "change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("repeatPassword") String repeatPassword,
            @RequestParam("verificationCode") String verificationCode,
            Authentication authentication,
            HttpSession session,
            Model model) {
        Long codeTimestamp = (Long) session.getAttribute("codeTimestamp");
        if (codeTimestamp == null || (System.currentTimeMillis() - codeTimestamp) > 5 * 60 * 1000) {
            model.addAttribute("error", "Verification code has expired. Please request a new one.");
            return "change-password";
        }
        String storedCode = (String) session.getAttribute("verificationCode");
        if (!verificationCode.equals(storedCode)) {
            model.addAttribute("error", "Invalid verification code.");
            return "change-password";
        }
        if (!newPassword.equals(repeatPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "change-password";
        }
        String username = authentication.getName();
        userService.updatePassword(username, newPassword);
        session.removeAttribute("verificationCode");
        session.removeAttribute("codeTimestamp");
        return "redirect:/advertisement/profile?passwordChanged=true";
    }

    @PostMapping("/profile/upload-avatar")
    public String uploadAvatar(@RequestParam("avatarFile") MultipartFile avatarFile) throws IOException {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("User not authenticated");
        }
        userService.updateAvatar(currentUser, avatarFile);
        return "redirect:/advertisement/profile";
    }

    @GetMapping
    public String listAdvertisements(@RequestParam(required = false) String search, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        List<Advertisement> advertisements;
        if (search != null && !search.trim().isEmpty()) {
            String normalizedSearch = search.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            advertisements = advertisementService.findAll().stream()
                    .filter(ad -> {
                        String normalizedTitle = ad.getTitle().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
                        return normalizedTitle.contains(normalizedSearch);
                    })
                    .collect(Collectors.toList());
        } else {
            advertisements = advertisementService.findAll();
        }
        model.addAttribute("advertisements", advertisements);
        model.addAttribute("currentUser", currentUser);
        return "advertisement";
    }

    @GetMapping("/update/{id}")
    public String showUpdatePage(@PathVariable Long id, Model model) {
        Advertisement advertisement = advertisementService.findById(id);
        User currentUser = userService.getCurrentUser();
        if (!advertisement.getUser().getId().equals(currentUser.getId())) {
            logger.warn("User {} attempted to edit advertisement {} which they do not own", currentUser.getId(), id);
            return "redirect:/advertisement";
        }
        model.addAttribute("advertisement", advertisement);
        return "advertisement-update";
    }

    @PostMapping("/update")
    public String updateAdvertisement(@ModelAttribute Advertisement advertisement, RedirectAttributes redirectAttributes, Model model) {
        User currentUser = userService.getCurrentUser();
        Advertisement existingAd = advertisementService.findById(advertisement.getId());
        if (!existingAd.getUser().getId().equals(currentUser.getId())) {
            logger.warn("User {} attempted to update advertisement {} which they do not own", currentUser.getId(), advertisement.getId());
            return "redirect:/advertisement";
        }
        existingAd.setCreatedAt(LocalDateTime.now());
        existingAd.setTitle(advertisement.getTitle());
        existingAd.setDescription(advertisement.getDescription());
        existingAd.setPrice(advertisement.getPrice());
        advertisementService.save(existingAd);
        model.addAttribute("message", "Advertisement updated successfully!");
        return "redirect:/advertisement";
    }

    @PostMapping("/delete")
    public String deleteAdvertisement(@RequestParam Long adId, RedirectAttributes redirectAttributes, Model model) {
        User currentUser = userService.getCurrentUser();
        Advertisement advertisement = advertisementService.findById(adId);
        if (!advertisement.getUser().getId().equals(currentUser.getId())) {
            logger.warn("User {} attempted to delete advertisement {} which they do not own", currentUser.getId(), adId);
            return "redirect:/advertisement";
        }
        advertisementService.delete(adId);
        model.addAttribute("message", "Advertisement deleted successfully!");
        return "redirect:/advertisement";
    }

    @GetMapping("/create")
    public String showAdvertisementForm(Model model) {
        model.addAttribute("advertisement", new Advertisement());
        return "advertisement-form";
    }

    @GetMapping("/info/{id}")
    public String showAdvertisementDetails(@PathVariable Long id, Model model) {
        Advertisement ad = advertisementService.findById(id);
        if (ad == null) {
            throw new IllegalArgumentException("Advertisement not found with id: " + id);
        }
        model.addAttribute("advertisement", ad);
        return "advertisement-info";
    }

    @GetMapping("/edit/{id}")
    public String editAdvertisementForm(@PathVariable Long id, Model model) {
        Advertisement ad = advertisementService.findById(id);
        if (ad == null) {
            throw new IllegalArgumentException("Advertisement not found with id: " + id);
        }
        model.addAttribute("advertisement", ad);
        return "advertisement-form";
    }

    @GetMapping("/mine")
    public String listUserAdvertisements(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("User not authenticated");
        }
        model.addAttribute("advertisements", advertisementService.findByUser(currentUser));
        return "advertisement-mine";
    }

    @GetMapping("/favorites")
    public String listFavorites(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("User not authenticated");
        }
        List<Advertisement> favorites = favoriteService.findFavoritesByUser(currentUser);
        model.addAttribute("favorites", favorites);
        return "favorites";
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path file = Paths.get("C:/Users/User/OneDrive/Desktop/SpringBoot projects/ShopHere/uploads/", filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public String addAdvertisement(@ModelAttribute Advertisement advertisement, @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
                String uploadDir = "C:/Users/User/OneDrive/Desktop/SpringBoot projects/ShopHere/uploads/";
                File file1 = new File(uploadDir);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File file = new File(uploadDir + fileName);
                imageFile.transferTo(file);
                advertisement.setImageUrl(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/advertisement/create?error=Failed to upload image";
            }
        }
        advertisement.setStatus(Advertisement.AdvertisementStatus.ACTIVE);
        advertisement.setUser(getCurrentUser());
        advertisementService.save(advertisement);
        return "redirect:/advertisement";
    }

    @PostMapping("/favorites/add")
    public String addFavorite(@RequestParam Long adId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("User not authenticated");
        }
        Advertisement ad = advertisementService.findById(adId);
        if (ad == null) {
            throw new IllegalArgumentException("Advertisement not found with id: " + adId);
        }
        favoriteService.addFavorite(currentUser, ad);
        return "redirect:/advertisement";
    }

    @PostMapping("/favorites/remove")
    public String removeFavorite(@RequestParam Long adId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("User not authenticated");
        }
        Advertisement ad = advertisementService.findById(adId);
        if (ad == null) {
            throw new IllegalArgumentException("Advertisement not found with id: " + adId);
        }
        favoriteService.removeFavorite(currentUser, ad);
        return "redirect:/advertisement/favorites";
    }

    @DeleteMapping("/{id}")
    public String deleteAdvertisement(@PathVariable Long id) {
        advertisementService.delete(id);
        return "redirect:/advertisement";
    }

    @PostMapping("/buy/update")
    public String updateUserProfile(@ModelAttribute User user,
                                    @RequestParam(required = false) Long adId,
                                    RedirectAttributes redirectAttributes) {
        logger.info("Received user: {}", user);
        if (user == null || user.getId() == null) {
            logger.error("User or user ID is null");
            redirectAttributes.addFlashAttribute("message", "Error: User data is incomplete.");
            return "redirect:/advertisement";
        }
        logger.info("Received user data: id={}, postOffice={}", user.getId(), user.getPostOffice());
        userService.updateUser(user); // Виклик лише з одним параметром
        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        logger.info("Updated user loaded: id={}, postOffice={}", user.getId(), user.getPostOffice());
        if (adId != null) {
            return "redirect:/advertisement/buy?adId=" + adId;
        } else {
            logger.warn("adId is null, redirecting to /advertisement");
            return "redirect:/advertisement";
        }
    }

    @PostMapping("/buy/payment")
    public String processPayment(@RequestParam(required = false) Long adId,
                                 @RequestParam String cardNumber,
                                 @RequestParam String expiryDate,
                                 @RequestParam String cvc,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        String postOffice = currentUser.getPostOffice();
        logger.info("Processing payment: username={}, postOffice={}, adId={}, cardNumber={}, expiryDate={}, cvc={}",
                currentUser.getUsername(), postOffice, adId, cardNumber, expiryDate, cvc);
        if (adId == null) {
            logger.warn("adId is null during payment processing");
            redirectAttributes.addFlashAttribute("message", "Payment failed: Invalid advertisement ID.");
            return "redirect:/advertisement";
        }
        if (postOffice == null || postOffice.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Payment failed. Please specify a post office number in your profile.");
            return "redirect:/advertisement/buy?adId=" + adId;
        } else if (isValidPayment(cardNumber, expiryDate, cvc)) {
            logger.info("Validation result: true");
            userService.createOrder(cardNumber, expiryDate, cvc, postOffice, adId);
            redirectAttributes.addFlashAttribute("message", "Payment successful!");
            return "redirect:/advertisement";
        } else {
            logger.info("Validation result: false");
            redirectAttributes.addFlashAttribute("message", "Payment failed. Please check your payment details.");
            return "redirect:/advertisement/buy?adId=" + adId;
        }
    }

    private boolean isValidPayment(String cardNumber, String expiryDate, String cvc) {
        logger.info("Validating payment: cardNumber={}, expiryDate={}, cvc={}", cardNumber, expiryDate, cvc);
        boolean isValid = cardNumber.length() == 16 && expiryDate.matches("\\d{2}/\\d{2}") && cvc.length() >= 3 && cvc.length() <= 4;
        logger.info("Validation result: {}", isValid);
        return isValid;
    }

    @GetMapping("/buy")
    public String showBuyPage(@RequestParam(required = false) Long adId, Model model) {
        User currentUser = userService.getCurrentUser();
        logger.info("Opening buy page with adId={}", adId);
        if (adId == null) {
            logger.warn("adId is null, redirecting to /advertisement");
            return "redirect:/advertisement";
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("adId", adId);
        return "buy";
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userService.findByUsername(username);
        }
        return null;
    }
}

