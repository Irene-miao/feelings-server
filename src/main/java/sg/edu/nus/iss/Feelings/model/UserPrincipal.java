package sg.edu.nus.iss.Feelings.model;


import java.util.Collection;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



public class UserPrincipal implements UserDetails{
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

 @Override
 public String getPassword(){
    return this.user.getPassword();
 }
    
 @Override
 public String getUsername(){
    return this.user.getUsername();
 }

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    // TODO Auto-generated method stub
   return null;

}

@Override
public boolean isAccountNonExpired() {
    // TODO Auto-generated method stub
    return true;
}

@Override
public boolean isAccountNonLocked() {
    // TODO Auto-generated method stub
    return true;
}

@Override
public boolean isCredentialsNonExpired() {
    // TODO Auto-generated method stub
    return true;
}

@Override
public boolean isEnabled() {
    // TODO Auto-generated method stub
    return true;
}

 

}
