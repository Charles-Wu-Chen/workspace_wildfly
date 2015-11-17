package com.devchronicles.facade.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.devchronicles.facade.BankServiceFacadeRemote;


public class FacadeClient {
	private static final Logger logger = Logger
			.getLogger(FacadeClient.class.getName());

	private final Context context;
	private BankServiceFacadeRemote facadeRemote;

	private final List<Future<String>> lastBookings = new ArrayList<Future<String>>();

	public FacadeClient() throws NamingException {
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

		new FacadeClient().run();
		
	}
	
	public void run () throws NamingException{
		this.facadeRemote = lookupFacadeEJB();
		showWelcomeMessage();
		System.out.println("get Loan Result::"+facadeRemote.getLoan(12345, 123345));
	}

	private void showWelcomeMessage() {
		System.out.println("====Welcome to the world======");
		
	}

	private BankServiceFacadeRemote lookupFacadeEJB() throws NamingException {
		return (BankServiceFacadeRemote) context
				.lookup("ejb:/J2EE-Design-Pattern-ejb/BankServiceFacade!com.devchronicles.facade.BankServiceFacadeRemote");
	}
}
