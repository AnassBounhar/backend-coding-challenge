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

		String beforeAMonth = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		URI endPoint = UriBuilder.fromUri(Constants.API_URL).queryParam("q", "created:"+beforeAMonth).queryParam("sort", "stars").queryParam("order", "desc").build();		
		HashMap<String,Language> langList = new HashMap<String,Language>();
		
		
		WebResource webResource = client.resource(endPoint);
		ClientResponse cresponse = webResource.get(ClientResponse.class);
		if(cresponse.getStatus()!=200){
			throw new RuntimeException("HTTP Error: "+ cresponse.getStatus());
		}
		
		String result = cresponse.getEntity(String.class);
		JsonElement jsonElement = parser.parse(result);
		Sort response = gson.fromJson(jsonElement, Sort.class);
		
		for (Iterator<Item> iterator = response.getItems().iterator(); iterator.hasNext();) {
			Item item = (Item) iterator.next();
			if (item.getLanguage() != null) {
				if (langList.get(item.getLanguage()) == null) {
					List<String> repos = new ArrayList<String>();
					repos.add(item.getHtmlUrl());
					langList.put(item.getLanguage(), new Language(item.getLanguage(), 1, repos));
				}else {
					Language languageToSwap = langList.get(item.getLanguage());
					languageToSwap.anotherUse();
					languageToSwap.addRepo(item.getHtmlUrl());
					langList.replace(item.getLanguage(),languageToSwap);
				}
			}
		}
		
		
		String[][] data = new String[langList.size()][3];
		
		int i=0;
		Iterator iterator = langList.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry pair = (Map.Entry)iterator.next();
	        data[i][0] = ((Language) pair.getValue()).getLanguageid();
	    	data[i][1] = String.valueOf(((Language) pair.getValue()).getUsecount());
	    	data[i][2] = ((Language) pair.getValue()).getRepos().toString();
	    	i++;
	    }
	    
		TextTable output = new TextTable(Constants.RESULT_TABLE_HEADER , data);                                                         
		output.printTable();
		
	}
}
