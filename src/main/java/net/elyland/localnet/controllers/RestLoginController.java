package net.elyland.localnet.controllers;

import net.elyland.localnet.domains.User;
import net.elyland.localnet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/logon")
public class RestLoginController {
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "success_login", method = RequestMethod.GET)
    public ResponseEntity<?> successLogin() {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "login_error", method = RequestMethod.GET)
    public ResponseEntity<?> errorLogin() {

        return new ResponseEntity<>("error", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "getUser", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentUser() {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user != null) {
            user.setPassword(null);
            user.setPasswordConfirm(null);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity(
                    new User(),
                    HttpStatus.OK);
        }
    }
}
