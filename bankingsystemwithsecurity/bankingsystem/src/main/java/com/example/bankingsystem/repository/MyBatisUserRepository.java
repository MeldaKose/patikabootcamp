package com.example.bankingsystem.repository;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.models.MyUser;

@Component
public class MyBatisUserRepository implements UserDetailsService{
	
	private String configFile="mapper/myBatis_conf.xml";
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Reader reader;
		try {
			reader = Resources.getResourceAsReader(configFile);
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
			SqlSession session=sqlSessionFactory.openSession();
			MyUser user=session.selectOne("accountUser.userDetail", username);
			if(user!=null) {
				String authorities=session.selectOne("accountUser.userAuthority", username);
				List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
				String[] parts=authorities.split(",");
				for(String part:parts) {
					list.add(new SimpleGrantedAuthority(part));
				}
				user.setAuthorities(list);
				//MyUser u=new MyUser(user.getUsername(),user.getPassword(),true,true,true,true,user.getAuthorities());
				return user;
			}else {
				throw new UsernameNotFoundException(username + " Not Found");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
