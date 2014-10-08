package com.sabre.ssw.proxy.message;

import java.util.Map;

import com.sabre.ssw.proxy.defines.ProxyMode;


public interface TestProxyParameters
{
    public String getAirlineId();
    public void setAirlineId(String airlineId);
  

    public ProxyMode getMode();   
    public void setMode(String mode);
    public void setMode(ProxyMode mode);
    

    public String GetCommandName();
    public void setTestId(String testId);
   

    public String getEndpoint();   
    public void setEndpoint(String endpoint);
    
    public String getSslEndpoint();
    public void setSslEndpoint(String sslEndpoint);
    

    
    /**
     * @return
     * @deprecated Use getParametersAsMap instead
     */
    public Map<String, String> getPropertiesMap();
    
    public Map<String, String> getParametersAsMap();
   
}
