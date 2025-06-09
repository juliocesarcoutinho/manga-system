package br.com.juliocesarcoutinho.userservice.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliocesarcoutinho.userservice.entities.User;
import br.com.juliocesarcoutinho.userservice.repositories.UserRepository;
import br.com.juliocesarcoutinho.userservice.security.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do UserDetailsService para carregar usuários pelo email
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Buscando usuário pelo email: {}", username);
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));
        
        log.info("Usuário encontrado: {}", user.getEmail());
        
        return new UserDetailsImpl(user);
    }
}
