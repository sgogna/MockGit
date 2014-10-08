package com.sabre.ssw.proxy.message;

import java.util.Map;

import com.sabre.ssw.proxy.defines.CommonTestProxyConstants;
import com.sabre.ssw.proxy.message.converter.JsonConverter;
import com.sabre.ssw.proxy.message.converter.JsonConverterInterface;



public class ProxyCommandCreator
{    
    public static String getBrowserCommand(Map<String,String> commandParameters)
    {
        return CommonTestProxyConstants.TEST_PROXY_COMMAND + "=" + getCommandArguments(commandParameters);
    }
    
    public static String getDirectProxyCommand(Map<String,String> commandParameters, String jsessionId)
    {
        return getCommandArguments(commandParameters) + CommonTestProxyConstants.ID_SEPARATOR + jsessionId;
    }
    
    private static String getCommandArguments(Map<String,String> commandParameters)
    {
        JsonConverterInterface converter = new JsonConverter();
        return converter.getJSONFromMap(commandParameters);
    }
}
