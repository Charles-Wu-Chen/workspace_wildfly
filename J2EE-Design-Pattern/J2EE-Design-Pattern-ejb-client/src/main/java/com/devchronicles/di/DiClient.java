package com.devchronicles.di;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator;

import com.devchronicale.di.IUserService;
import com.devchronicale.di.User;
import com.devchronicles.facade.BankServiceFacadeRemote;


public class DiClient {
	private static final Logger logger = Logger
			.getLogger(DiClient.class.getName());

	private final Context context;
	private IUserService userService;
	public DiClient() throws NamingException {
		final Properties jndiProperties = new Properties();
		jndiProperties.setProperty(Context.URL_PKG_PREFIXES,
				"org.jboss.ejb.client.naming");
		// this is EJB client API style, for better performance and reduce
		// server side load
		// (https://docs.jboss.org/author/display/AS71/Remote+EJB+invocations+via+JNDI+-+EJB+client+API+or+remote-naming+project)
		// lookup optimization by the EJB client API project happens for
		// stateless beans.
		// vs EJB invocations from a remote client using JNDI
		// https://docs.jboss.org/author/display/AS71/EJB+invocations+from+a+remote+client+using+JNDI
		this.context = new InitialContext(jndiProperties);
	}

	public static void main(String[] args) throws Exception {
		Logger.getLogger("org.jboss").setLevel(Level.SEVERE);
		Logger.getLogger("org.xnio").setLevel(Level.SEVERE);
		Logger.getLogger("org.jboss.ejb.client").setLevel(Level.ALL);
		new DiClient().run();
		
	}
	
	public void run () throws NamingException{
		this.userService = lookupEJB();
		showWelcomeMessage();
		User user = new User();
		System.out.println(userService.persistUser(user));
		
//		BankServiceFacadeRemote userService = lookupFacadeEJB();
//		showWelcomeMessage();
//		System.out.println(userService.getLoan(123, 234));
		RemoteCalculator statelessRemoteCalculator =  lookupCalculatorFacadeEJB() ;
		int num1 = 3434;
        int num2 = 2332;
        System.out.println("Subtracting " + num2 + " from " + num1
            + " via the remote stateless calculator deployed on the server");
        int difference = statelessRemoteCalculator.subtract(num1, num2);
        System.out.println("Result>"+difference);
	}

	private void showWelcomeMessage() {
		System.out.println("====Welcome to the world======");
		
	}

	private IUserService lookupEJB() throws NamingException {
		return (IUserService) context
				.lookup("ejb:/J2EE-Design-Pattern-ejb/UserService7!com.devchronicale.di.IUserService");
	}
	
	private BankServiceFacadeRemote lookupFacadeEJB() throws NamingException {
		return (BankServiceFacadeRemote) context
				.lookup("ejb:/J2EE-Design-Pattern-ejb/BankServiceFacade!com.devchronicles.facade.BankServiceFacadeRemote");
	}
	
	private RemoteCalculator lookupCalculatorFacadeEJB() throws NamingException {

		return (RemoteCalculator) context
				.lookup("ejb:/wildfly-ejb-remote-server-side/CalculatorBean!org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator");
	
	}
	
}
