package com.sabre.ssw.proxy.message.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonConverter implements JsonConverterInterface{

private static final Logger LOG = LoggerFactory.getLogger(JsonConverter.class);
private final String errorMsg = "Wrong testId format. Proper format of address' sufix is: ?testId={\"parametr_name\":\"PARAMETR_VALUE\",\"optional_parametr_name\":\"OPTIONAL_PARAMETR_VALUE\"}\n\n"
		+ "Required parameters:\n"
		+ "testId:any String without special characters\n\n"
		+ "endpoint:endpoint_url\n"
		+ "Optional parameters:\n"
		+ "mode:RECORD or REPLAY or COMPARE or TRANSPARENT\n"
	
		+ "generateCreateSession:true or false \n\n"
		+ "If any of optional parameters is not set that property is being read from proxy.properties file\n" +
		"f.e: webqtrip.html?testId={\"testId\":\"ABC\",\"endpoint\"=\"http://localhost:871/testproxy/s\",\"mode\":\"RECORD\"}";	
	
public String getJSONFromMap(Map<String,String> map)
	{
	
		if(map.get("testId") == null)
			throw new RuntimeException(errorMsg);

		String jsonString = "{";
		
		for(String key:map.keySet())
		{
			jsonString += "\""+refactorKey(key)+"\":\""+refactorString(map.get(key))+"\",";
			
		}
		jsonString = jsonString.substring(0, jsonString.length()-1);
		jsonString += "}";
		
		return jsonString;
	}

public Map<String,String> getMapFromJSON(String jsonString)
{
	Map <String,String> jsonMap = new HashMap<String,String>();
	String jsonPattern = "\\{\"\\S+\":\"\\S+\"(,\"\\S+\":\"\\S+\")?}";
	
	
	if(!Pattern.matches(jsonPattern, jsonString))
	{
		LOG.error(errorMsg);
		throw new RuntimeException("Received testId is:"+jsonString+"\n"+errorMsg);
	}
	
		
	jsonString = jsonString.substring(1,jsonString.length()-1);
	
	String entries[] = jsonString.split(",");
	for(String i:entries)
	{
		int index = i.indexOf(":");
		String key = i.substring(1, index-1);
		String value = i.substring(index+2, i.length()-1);
		jsonMap.put(deleteEscapeChars(key), deleteEscapeChars(value));
	}
	
	if(!jsonMap.containsKey("testId"))
	{
		LOG.error(errorMsg);
		throw new RuntimeException("Received testId is:"+jsonString+"\n"+errorMsg);
	}
	
	return jsonMap;
}
private String refactorKey(String value)
{
	if(value.contains("\\"))
		LOG.error("Key in map cannot contain \\");

	return refactorString(value);
}
	
private String refactorString(String value)
{
	value = value.replaceAll("/","\\\\/");

	return value;
}



private String deleteEscapeChars(String refactor)
{
	refactor = refactor.replaceAll("\\\\", "");
	return refactor;
	
}


}

