package com.devchronicale.di;

import javax.enterprise.context.Dependent;

@Dependent
public class UserDataRepositoryImpl implements UserDataRepository {
	@Override
	public void save(User user) {
		// Persistence Code Here
		System.out.println("UserDataRepositoryImpl :: Save user here");
	}
}
