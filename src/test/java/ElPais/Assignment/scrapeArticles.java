package ElPais.Assignment;

import org.testng.annotations.Test;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class scrapeArticles extends BaseTest{
	
	List<String> articlesTitle =new ArrayList<>();
	String[] translatedTitles;
	
	@Test
	public void articles() throws IOException {
		driver.findElement(By.xpath("(//a[@href='https://elpais.com/opinion/'])[1]")).click();
		
         for (int i = 1; i <= 5; i++) {
        	
        	WebElement ithArticle = driver.findElement(By.xpath("(//div/article)[" + i + "]"));

        	
        	String title=ithArticle.findElement(By.tagName("h2")).getText();
        	String content=ithArticle.findElement(By.tagName("p")).getText(); 
        	
        	System.out.println("Title : "+title);
        	System.out.println("Content : "+content);
        	
        	try {
                WebElement imgElement = driver.findElement(By.xpath("(//div/article)["+i+"]//figure//a//img"));
                
                String imageurl=imgElement.getAttribute("src");
    	        //Saving image
      	        saveImage(imageurl,title);
      	        System.out.println("Image found in article");
            } catch (NoSuchElementException e) {
            	System.out.println("No image found in article");
            }
        	
        	articlesTitle.add(title);

        	//System.out.println(articlesTitle);
        	
        }
         //translating the headers/titles
         translatedTitles=translateToEnglish(articlesTitle);
         
         //
         
         //Occurrence of translated headers/titles
         analyzeTitle(translatedTitles);
	}
	
	
    private String[] translateToEnglish(List<String> articlesTitle) throws IOException {
		// TODO Auto-generated method stub
    	String apiKey="1161129c20msh62c9e60b4fc6c13p1e62a1jsn34c98712f69d";
    	String apiEndpoint = "https://rapid-translate-multi-traduction.p.rapidapi.com/t"; 
        
        
        URL url = new URL(apiEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("x-rapidapi-key", apiKey);
        connection.setRequestProperty("x-rapidapi-host", "rapid-translate-multi-traduction.p.rapidapi.com");
        connection.setRequestProperty("Content-Type", "application/json");
        
        connection.setDoOutput(true);
        
        JsonArray qArray = new JsonArray();
        for(String s:articlesTitle) {
        	qArray.add(s);
        }

        
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.add("q", qArray); 
        jsonRequest.addProperty("from", "es");
        jsonRequest.addProperty("to", "en"); 
        jsonRequest.addProperty("e", "");
        
        //System.out.println(jsonRequest.toString());

        // Write the JSON data to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } 
        
        // Get the response from the API
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed to get response from API. HTTP response code: " + responseCode);
        }

        // Read the response from the API
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String jsonResponse = response.toString();
        JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
        String[] stringArray = new String[jsonArray.size()];
        System.out.println("Translated titles : ");
        for (int i = 0; i < jsonArray.size(); i++) {
            stringArray[i] = jsonArray.get(i).getAsString();
            System.out.println(stringArray[i]);
        }
        
       
        return stringArray;
		
	}
	private void saveImage(String imageurl,String title) throws IOException {
	// TODO Auto-generated method stub
    	 	String path="./Images/";
    	 	URL imageURL = new URL(imageurl);
    	 	BufferedImage saveImage = ImageIO.read(imageURL);
     
    	 	File file =new File(path+title+".jpg");
    	 	ImageIO.write(saveImage, "jpg",file );
     
	
	
     }
	
	
	public void analyzeTitle(String[] translatedTitles) {
		 Map<String, Integer> wordCount = new HashMap<>();
		 for (String title : translatedTitles) {
	            String[] words = title.split("\\s+");
	            for (String word : words) {
	                word = word.toLowerCase();
	                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
	            }
	        }
		 
		 System.out.println("Repeated words across all headers combined:");
		 boolean duplicatePresence=false;
	        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
	            if (entry.getValue() > 2) {
	                System.out.println(entry.getKey() + ": " + entry.getValue());
	                duplicatePresence=true;
	            }
	            
	        }
	        if(!duplicatePresence) {
	        	System.out.println(" No Repeated words across all headers");
	        }
	        
	        
	}



}
