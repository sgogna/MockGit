package com.sabre.ssw.proxy.message.converter;



import java.util.Map;

public interface JsonConverterInterface {

	public String getJSONFromMap(Map<String,String> map);
	public Map<String,String> getMapFromJSON(String jsonString);
	
}
