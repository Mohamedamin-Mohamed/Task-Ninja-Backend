package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Repositories.UserRepository;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    @Autowired
    public UserService(UserRepository userRepository, PasswordUtils passwordUtils){
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public boolean createUser(RequestDto signupRequest) {

        boolean userExists = userExists(signupRequest);
        LOGGER.info("User created: " + !userExists);
        if (userExists) {
            LOGGER.warning("User with email: " + signupRequest.getEmail() + " already exists");
            return false;
        } else {
            //pass the item so that it added to the database
            UserDto user = new UserDto();
            user.setEmail(signupRequest.getEmail());
            user.setHashedPassword(passwordUtils.hashPassword(signupRequest.getPassword()));
            return userRepository.addUser(user);
        }
    }
        public boolean userExists(RequestDto request){
        LOGGER.info("User exists request received");
            return userRepository.userExists(request);
        }
    public boolean passwordMatches(RequestDto loginRequest){
        return userRepository.passwordMatches(loginRequest);
    }

    public boolean updateUser(RequestDto request){
        return userRepository.updateUser(request);
    }
}
