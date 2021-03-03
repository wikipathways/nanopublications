package nl.unimaas.bigcat.wikipathways.nanopubs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PubMed {
	   public static Date getPublicationDate(String pubmedId) throws IOException, ParseException {
		   if(isNumeric(pubmedId))
		   {
			    String postUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+pubmedId+"&retmode=json"; //post url
			    //URLConnection conn = null;
			    
//	    		URL url = new URL(postUrl);
//		        conn = url.openConnection();
//		        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		       
			    URL url = new URL(postUrl);
			    HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
			    conn.setRequestMethod("GET");
			    conn.connect();
			    int responsecode = conn.getResponseCode();
			    if(responsecode != 200)
			    {
			    	System.out.println(pubmedId);
			    	throw new RuntimeException("HttpResponseCode: " +responsecode);
			    }
			    else
			    {
			    	InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
			    	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			    	JSONParser parse = new JSONParser();
			    	JSONObject jobj = (JSONObject)parse.parse(bufferedReader);
			    	JSONObject resultJson = (JSONObject) jobj.get("result");
			    	JSONObject pubmedJson = (JSONObject) resultJson.get(pubmedId);
			    	String jsonDate = "";
			    	
			    	conn.disconnect();
			    	
			    	if (!((String)pubmedJson.get("epubdate")).equals(""))
			    		jsonDate = (String)pubmedJson.get("epubdate");
			    	else if(!((String)pubmedJson.get("pubdate")).equals(""))
			    		jsonDate = (String)pubmedJson.get("pubdate");
			    		
			    	String[] splitDate = jsonDate.split("-");
			    	String date = "";
			    	if(splitDate[0].split(" ").length == 3)
			    		date = splitDate[0];
			    	else if(splitDate[0].split(" ").length == 2)
			    		date = splitDate[0] + " 01";
			    	else
			    		date = splitDate[0] + " Jan 01";
			    	
			    	SimpleDateFormat format = new SimpleDateFormat("yyyy MMM dd");
			    	String dateString = format.format( new Date());
			    	//System.out.println(dateString);
			    	try {
						Date pubDate = format.parse ( date );
						format = new SimpleDateFormat("yyyy-MM-dd");
						
						return pubDate;
					} catch (java.text.ParseException e) {
						System.out.println(pubmedId + " " + jsonDate);
						System.out.println(e);
					} 
			    }		   
		   }

			return null;
		}
	   
	   public static boolean isNumeric(final String str) {

	       if (str == null || str.length() == 0) {
	           return false;
	       }

	       try {

	           Integer.parseInt(str);
	           return true;

	       } catch (NumberFormatException e) {
	           return false;
	       }

	   }
}
