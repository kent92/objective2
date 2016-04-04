package testLogin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

public class testLogin {

	
	WebDriver driver;
	Random random = new Random();
	
	
	/**
	 * @author Kent Avasarala
	 * 
	 * @description :
	 * Attempt a login with an invalid username/password combination, and recognize that the login attempt failed because of bad credentials.
	 * Attempt a second login with the valid credentials, and again recognize that the login has been successful.
	 * Bonus 1: make the test randomly pick either bad credentials or good credentials (and recognize the outcome) each time it is run – we can then run it multiple times to get both a good and bad attempts.
	 * Bonus 2: make the test randomly pick between clicking on the Submit button and submitting the form programatically.
	 * 
	 */	
	@Test(dataProvider = "dp")
	public void testLogin(String Login_username, String Login_password, boolean valid_credentials, String LoginURL ) {
		
		try {
				// Open firefox
				WebDriver driver = new FirefoxDriver();
				System.out.println("Opening Firefox");
				
				// Go to the URL
				driver.get(LoginURL);
				System.out.println("Going to " + LoginURL);
				
				// Click 'Login'
				driver.findElement(By.id("clientLogin")).click();
				wait(3);
				System.out.println("Clicking on 'Login'");
				
				// Enter credentials
				driver.findElement(By.id("username")).sendKeys(Login_username);
				wait(2);
				System.out.println("Entering credentials: " + Login_username);
				
				driver.findElement(By.id("password")).sendKeys(Login_password);
				wait(2);
				System.out.println("Entering credentials: " + Login_password);
				
				// select randomly between clicking submit vs submitting programmatically
				if( random.nextBoolean() ){
					
					driver.findElement(By.id("btnSubmit")).submit();
					System.out.println("Submitting Login form programmatically");
					
				} else {
					
					driver.findElement(By.id("btnSubmit")).click();
					System.out.println("Clicking 'Submit' button");
				}
				
				wait(3);
				
				// If the credentials entered are valid, verify that the login was successful
				if( valid_credentials ) {
					
					// Assert that 'Client Portal' appears
					Assert.assertTrue(driver.findElement(By.xpath(".//*[@id='middle']/div[3]/h2")).getText().contains("Client Portal"));
					System.out.println("Logged in successfully with valid credentials");
					
					// Assert that the URL contains /client/portal
					Assert.assertTrue(driver.getCurrentUrl().contains("/client/portal"));
					System.out.println("URL changed to " + driver.getCurrentUrl() + " as expected");
					
					// Assert that the 'Welcome, <person>' greeting appears at the top of the page
					Assert.assertTrue(driver.findElement(By.xpath(".//*[@id='membermenu']/a[1]")).getText().contains("Welcome"));
					System.out.println("Found the 'Welcome' text, as expected");
										
				} else {	// If credentials are invalid, verify that the login failed message appears
					
					// Assert that all expected elements are found
					WebElement invalid_login_message = driver.findElement(By.xpath(".//*[@id='login']/label[2]/aside/p"));
					Assert.assertTrue(invalid_login_message.getText().contains("Invalid Username or Password"));
					System.out.println("Looking for 'Invalid Username or Password' message, found it");
					System.out.println("Login failed with invalid credentials, as expected");
					
					// Assert that the URL contains /client/login
					Assert.assertTrue(driver.getCurrentUrl().contains("/client/login"));
					System.out.println("Still on " + driver.getCurrentUrl());
					
				}
				
				System.out.println("Closing Firefox");
				driver.quit();
		
		} catch (Exception ex) {
			
			Assert.fail("Test failed due to an Exception being thrown. See console for details");
			System.out.println(ex.toString());			
			
		} 
	}

	/**
	 * @author Kent Avasarala
	 * @description The dataprovider to pass parameters to the test method
	 * 
	 * @return A random set of credentials (username, password) and whether the credentials are valid (true/false)
	 */	
  @DataProvider
  public Object[][] dp() {

	  List<credentials> creds = new ArrayList<credentials>();
	  
	  String LoginURL = "https://referencesite.nudatasecurity.com/" ;
	  
	  // feed all enums into a list
	  for( credentials c : credentials.values() ){		  
		  
		  creds.add(c);		  
	  }
	  
	  // generate a random integer to select the credentials to send
	  int i = random.nextInt(2);

	  return new Object[][] {
    		
			  new Object[] { 	
					  			creds.get(i).getUserName(), 
					  			creds.get(i).getPassword(), 
					  			creds.get(i).isValidCredentials(),
					  			LoginURL
			  }
      
    };
  }

  public enum credentials {
	  
	  KENT 		("kent.avasarala", "wC*MD^2V4G*qXj25", true),
	  ALICEBOB 	("alicebob", "qwerty", false);
	  
	  private String un;
	  private String pw;
	  private boolean valid_cred;
	  
	  credentials(String un, String pw, boolean valid_cred){
		  
		  this.un = un;
		  this.pw = pw;
		  this.valid_cred = valid_cred;
		  
	  }
	  
	  public String getUserName() { return this.un;  }
	  public String getPassword() {	return this.pw;  }
	  public boolean isValidCredentials() {	return this.valid_cred; }
	  
  
  }
  
  public static void wait( int seconds ) throws InterruptedException{
	  
	  Thread.sleep(seconds*1000);
	  
  }




}
