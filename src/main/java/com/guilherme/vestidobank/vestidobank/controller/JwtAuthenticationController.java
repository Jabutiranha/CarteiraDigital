package com.guilherme.vestidobank.vestidobank.controller;

import com.guilherme.vestidobank.vestidobank.config.JwtTokenUtil;
import com.guilherme.vestidobank.vestidobank.model.JwtRequest;
import com.guilherme.vestidobank.vestidobank.model.JwtResponse;
import com.guilherme.vestidobank.vestidobank.model.User;
import com.guilherme.vestidobank.vestidobank.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    private void authenticate(String username, String password) throws Exception{
        try {
            authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e){
            throw new Exception("USUARIO INATIVO", e);
        } catch (BadCredentialsException e){
            throw new Exception("CREDENCIAIS INVALIDAS", e);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody User user) throws Exception{
        return ResponseEntity.ok(userDetailsService.save(user));
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request)
            throws Exception{
        authenticate(request.getUsername(), request.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(
                request.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
