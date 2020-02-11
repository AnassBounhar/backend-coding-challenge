package backend.challenge.run;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import backend.challenge.constants.Constants;
import backend.challenge.pojo.Item;
import backend.challenge.pojo.Language;
import backend.challenge.pojo.Sort;
import dnl.utils.text.table.TextTable;

import javax.ws.rs.core.UriBuilder;

public class Consume {

	static Client client = Client.create();

	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws MalformedURLException {
		
		
		Gson gson = new GsonBuilder().create();
		JsonParser parser = new JsonParser();

		//Creating date to use un the query param
		String beforeAMonth = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		
		//Appending the query params
		URI endPoint = UriBuilder.fromUri(Constants.API_URL).queryParam("q", "created:"+beforeAMonth).queryParam("sort", "stars").queryParam("order", "desc").build();		
		
		//This list will be used to sort out response
		HashMap<String,Language> langList = new HashMap<String,Language>();
		
		//Sending request
		WebResource webResource = client.resource(endPoint);
		ClientResponse cresponse = webResource.get(ClientResponse.class);
		if(cresponse.getStatus()!=200){
			throw new RuntimeException("HTTP Error: "+ cresponse.getStatus());
		}
		
		//Storing raw response value
		String result = cresponse.getEntity(String.class);
		//Parsing the response to json
		JsonElement jsonElement = parser.parse(result);
		//Parsing the response to Java POJO objects
		Sort response = gson.fromJson(jsonElement, Sort.class);
		
		
		//Loop over projects in the objet Sort
		for (Iterator<Item> iterator = response.getItems().iterator(); iterator.hasNext();) {
			Item item = (Item) iterator.next();
			
			//Filtering the language to reject nulls
			if (item.getLanguage() != null) {
				//Adding non-existing language to the hashmap
				if (langList.get(item.getLanguage()) == null) {
					//Inctance of new object array of repos to pass to new Language object
					List<String> repos = new ArrayList<String>();
					//Adding the current language
					repos.add(item.getHtmlUrl());
					//saving the object of Language and identifier of the language
					langList.put(item.getLanguage(), new Language(item.getLanguage(), 1, repos));
				}
				//Edit existing language in the hashmap
				else {
					//Get object to edit
					Language languageToSwap = langList.get(item.getLanguage());
					//Incrementing the use of language
					languageToSwap.anotherUse();
					//Adding the repo that uses the language
					languageToSwap.addRepo(item.getHtmlUrl());
					//Swapping the object in the HasMap
					langList.replace(item.getLanguage(),languageToSwap);
				}
			}
		}
		
		//Initializing the double dimension array to use with library j-text-utils to print a beautiful table :P
		String[][] data = new String[langList.size()][3];
		
		//Looping over the HashMap result of sort earlier to fill the array 
		int i=0;
		Iterator iterator = langList.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry pair = (Map.Entry)iterator.next();
	        data[i][0] = ((Language) pair.getValue()).getLanguageid();
	    	data[i][1] = String.valueOf(((Language) pair.getValue()).getUsecount());
	    	data[i][2] = ((Language) pair.getValue()).getRepos().toString();
	    	i++;
	    }
	    
	    //Printing the Table in console.
		TextTable output = new TextTable(Constants.RESULT_TABLE_HEADER , data);                                                         
		output.printTable();
		
	}
}
