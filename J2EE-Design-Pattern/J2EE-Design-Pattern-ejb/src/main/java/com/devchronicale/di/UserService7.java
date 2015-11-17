package com.devchronicale.di;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
@Remote(IUserService.class)

public class UserService7 implements IUserService {

	@Inject
	private UserDataRepository udr;

	public int persistUser(User user) {
		udr.save(new User());
		return 1;
	}
}
