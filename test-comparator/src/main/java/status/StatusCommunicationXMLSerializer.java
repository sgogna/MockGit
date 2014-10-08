package status;

import com.thoughtworks.xstream.XStream;

public class StatusCommunicationXMLSerializer
{

    private static XStream stream = new XStream();

    public static String serializeRequest(StatusCommunicationRequest request)
    {
        return (stream.toXML(request));
    }

    public static StatusCommunicationRequest deserializeRequest(String xmlRequest)
    {
        return ((StatusCommunicationRequest) stream.fromXML(xmlRequest));
    }

    public static String serializeResponse(StatusCommunicationResponse response)
    {
        return (stream.toXML(response));
    }

    public static StatusCommunicationResponse deserializeResponse(String xmlResponse)
    {
        return ((StatusCommunicationResponse) stream.fromXML(xmlResponse));
    }
}