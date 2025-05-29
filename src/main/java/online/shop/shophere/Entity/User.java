package online.shop.shophere.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Arrays;
import java.util.Collection;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Username must contain only letters")
    private String username;

    @NotBlank(message = "Full name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Full name must contain only letters")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Not eal email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Phone cannot be empty")
    @Pattern(regexp = "^\\+?[0-9]{5,20}$", message = "Phone number must be between 5 and 15 digits")
    private String phone;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @Column(name = "post_office")
    private String postOffice;

    @Column(name = "avatar_url")
    private String avatarUrl;

    public User(String username, String lastName, String email, String password, String phone,String city, String address,String postOffice, String avatarUrl) {
        this.username = username;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.postOffice = postOffice;
        this.avatarUrl = avatarUrl;
    }

    public User() {}

    public String getPostOffice() {return postOffice;}
    public void setPostOffice(String postOffice) {this.postOffice = postOffice;}
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public void setUsername(String username) {this.username = username;}
    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}
    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public String getAvatarUrl() {return avatarUrl;}
    public void setAvatarUrl(String avatarUrl) {this.avatarUrl = avatarUrl;}
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));}
    @Override
    public boolean isAccountNonExpired() {return true;}
    @Override
    public boolean isAccountNonLocked() {return true;}
    @Override
    public boolean isCredentialsNonExpired() {return true;}
    @Override
    public boolean isEnabled() {return true;}
    @Override
    public String getUsername() {return username;}
    @Override
    public String getPassword() {return password;}
}
