package ElPais.Assignment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BaseTest {
	
	public WebDriver driver;
	
	
	public WebDriver InitializeDriver() {
		
		ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        System.setProperty("webdriver.chrome.driver", "./chromedriver"); 
        driver = new ChromeDriver(options);
        
        return driver;
			
	}
	
	@BeforeTest
	public void lauchApplication() {
		driver = InitializeDriver();
		driver.get("https://elpais.com/");
		WebElement htmlElement = driver.findElement(By.tagName("html"));
        String langAttribute = htmlElement.getAttribute("lang");
        
        if(langAttribute.equals("es-ES")) {
        	System.out.println("Website displayed in Spanish");
        	
        	
        }
        
        try {
       	 WebElement acceptButton = driver.findElement(By.xpath("//span[text()='Aceptar']"));
    		 if(acceptButton.isDisplayed())
    			acceptButton.click();
         }catch (Exception e) {
           System.out.println("Cookie accept button not found.");
         }
		
	}
	
	@AfterSuite
	public void close() {
		driver.close();
	}
	

}
