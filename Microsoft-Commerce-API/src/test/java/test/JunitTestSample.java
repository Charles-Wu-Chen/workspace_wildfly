package test;

import microsoft.partner.csp.api.v1.samples.AADToken;
import microsoft.partner.csp.api.v1.samples.Customer;
import microsoft.partner.csp.api.v1.samples.Order;
import microsoft.partner.csp.api.v1.samples.PartnerAPiCredentialsProvider;
import microsoft.partner.csp.api.v1.samples.Profile;
import microsoft.partner.csp.api.v1.samples.Reseller;
import microsoft.partner.csp.api.v1.samples.SAToken;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;


public class JunitTestSample {

    private Collection collection;
    private String aadToken;
    private String saToken;
    private String resellerMicrosoftId;
    private String resellerCid;
    private Customer customer;
    
    
//    @BeforeClass
//    public static void oneTimeSetUp() {
//        // one-time initialization code   
//    	System.out.println("@BeforeClass - oneTimeSetUp");
//    }
//
//    @AfterClass
//    public static void oneTimeTearDown() {
//        // one-time cleanup code
//    	System.out.println("@AfterClass - oneTimeTearDown");
//    }

    @Before
    public void setUp() {
        collection = new ArrayList();
        System.out.println("@Before - setUp");
        
        aadToken = new AADToken().getAADToken();
		saToken = new SAToken(aadToken).getSAToken();
		resellerMicrosoftId = PartnerAPiCredentialsProvider.getPropertyValue("MicrosoftId");

		resellerCid = new Reseller(saToken).getResellerCid(resellerMicrosoftId);
		customer = new Customer(saToken);
    }

//    @After
//    public void tearDown() {
//        collection.clear();
//        System.out.println("@After - tearDown");
//    }
//
//    @Test
//    public void testEmptyCollection() {
//        assertTrue(collection.isEmpty());
//        System.out.println("@Test - testEmptyCollection");
//    }
//
//    @Test
//    public void testOneItemCollection() {
//        collection.add("itemA");
//        assertEquals(1, collection.size());
//        System.out.println("@Test - testOneItemCollection");
//    }
//    
    //@Test
    public void testCreateCustomer(){
    	System.out.println("testCreateCustomer started");
    	
    	customer.createCustomer(resellerCid);
		resellerMicrosoftId = customer.getEtid();
		String customerCid = customer.getCustomerId(); //persist this value for a later usage
		System.out.println("customerCid >>"+customerCid);
    	System.out.println("testCreateCustomer end");
    }
    
    @Test
    public void testGetCustomer(){
    	System.out.println("testGetCustomer started");
    	String customerCid = PartnerAPiCredentialsProvider.getPropertyValue("ExistingCustomerCid"); 
    	String customerToken = customer.getCustomerToken(customerCid, aadToken);
		String customerProfile = new Profile(customerCid, customerToken).getCustomerProfile();
		System.out.println("Retrieved Customer Profile = " + customerProfile);
    }
    

    @Test
    public void testgetAllOrder(){

    	String customerCid = PartnerAPiCredentialsProvider.getPropertyValue("ExistingCustomerCid"); 
    	Order order = new Order(saToken);
		order.getAllOrders(resellerCid,customerCid);
    }
    
    @Test
    public void testCreateOffice365PremiumOrder(){

		//Create an Office 365 Business Essentials order for this customer
		String offerUri = "/3c95518e-8c37-41e3-9627-0ca339200f53/offers/bd938f12-058f-4927-bba3-ae36b1d2501c";
		String quantity = "1";
		String customerCid = PartnerAPiCredentialsProvider.getPropertyValue("ExistingCustomerCid"); 
		Order order = new Order(saToken);
		order.placeOrderWithSingleLineItem(resellerCid, customerCid, offerUri, quantity);
    }
}
