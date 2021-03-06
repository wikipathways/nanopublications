/* Copyright (C) 2015-2016  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.unimaas.bigcat.wikipathways.nanopubs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PubMed {
	public static JSONObject loadJSONData()
	{
		JSONObject jsonData = new JSONObject();
		
		try (Reader reader = new FileReader("pubmed_data.json")) {
			JSONParser parser = new JSONParser();
			JSONObject jsObj = (JSONObject) parser.parse(reader);
			jsonData = (JSONObject) jsObj.get("result");
           return jsonData;
		}
		catch(Exception e)
		{
			System.out.println("Please provide the pubmed json data.");
		}
		
		return null;
	}
	   public static Date getPubDate(String pubmedId, JSONObject jsonData) throws IOException, ParseException {
		   if(isNumeric(pubmedId))
		   {
			   JSONObject jsPubmed = (JSONObject) jsonData.get(pubmedId);
			   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				  
			   Date date = null;
				try {
					if(jsPubmed!=null)
						date = format.parse(jsPubmed.get("pubdate").toString());
					else
						date = format.parse("0000-00-00");
				} catch (java.text.ParseException e) {
				}
				  return date;
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
