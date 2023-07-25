package sg.edu.nus.iss.Feelings.service;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.iss.Feelings.model.User;
import sg.edu.nus.iss.Feelings.model.UserPrincipal;
import sg.edu.nus.iss.Feelings.repository.FeelingsRepository;


@Service
@Transactional
@Qualifier("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService{


private Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

   @Autowired
   private FeelingsRepository feelingsRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        Optional<User> opt = feelingsRepo.findByUsername(username);
        User user = opt.get();
        System.out.println("userDetails: " + user);
      
        if (opt.isEmpty() || user == null){
            throw new UsernameNotFoundException("User not found");
        }
        UserPrincipal userPrincipal = new UserPrincipal(user);
        logger.info("UserdetailsSvc userPrincipal: "+ userPrincipal);
        return userPrincipal;
    }


}
