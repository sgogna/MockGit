<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
    <soapenv:Body>
        <ns1:OTA_VehAvailRateRS xmlns:ns1="http://www.opentravel.org/OTA/2003/05" xmlns="http://www.opentravel.org/OTA/2003/05" Version="4.500">
            <ns1:Success></ns1:Success>
            <ns1:VehAvailRSCore>
                <ns1:VehRentalCore PickUpDateTime="2012-12-09T09:30:00" ReturnDateTime="2012-12-16T05:00:00">
                    <ns1:PickUpLocation CodeContext="IATA" ExtendedLocationCode="BCN" LocationCode="BCN"></ns1:PickUpLocation>
                    <ns1:ReturnLocation CodeContext="IATA" ExtendedLocationCode="BCN" LocationCode="BCN"></ns1:ReturnLocation>
                </ns1:VehRentalCore>
                <ns1:VehVendorAvails>
                    <ns1:VehVendorAvail>
                        <ns1:VehAvails>
                        </ns1:VehAvails>
                        <ns1:Info>
                            <ns1:LocationDetails Name="Barcelona Airport">
                                <ns1:Address FormattedInd="false">
                                    <ns1:AddressLine>TERMINAL 1 AND 2              </ns1:AddressLine>
                                    <ns1:AddressLine>08820 BARCELONA               </ns1:AddressLine>
                                    <ns1:CityName>BARCELONA AP T1 T2            </ns1:CityName>
                                    <ns1:PostalCode>08820      </ns1:PostalCode>
                                    <ns1:CountryName Code="ES">SPAIN                         </ns1:CountryName>
                                </ns1:Address>
                                <ns1:Telephone FormattedInd="false" PhoneLocationType="4" PhoneNumber="932983637" PhoneTechType="1"></ns1:Telephone>
                                <ns1:AdditionalInfo>
                                    <ns1:ParkLocation Location="1"></ns1:ParkLocation>
                                    <ns1:CounterLocation Location="1"></ns1:CounterLocation>
                                    <ns1:OperationSchedules>
                                        <ns1:OperationSchedule>
                                            <ns1:OperationTimes>
                                                <ns1:OperationTime Text="MO-SU 0700-2400"></ns1:OperationTime>
                                                <ns1:OperationTime End="23:59:00" Mon="true" Start="07:00:00"></ns1:OperationTime>
                                                <ns1:OperationTime End="23:59:00" Start="07:00:00" Tue="true"></ns1:OperationTime>
                                                <ns1:OperationTime End="23:59:00" Start="07:00:00" Weds="true"></ns1:OperationTime>
                                                <ns1:OperationTime End="23:59:00" Start="07:00:00" Thur="true"></ns1:OperationTime>
                                                <ns1:OperationTime End="23:59:00" Fri="true" Start="07:00:00"></ns1:OperationTime>
                                                <ns1:OperationTime End="23:59:00" Sat="true" Start="07:00:00"></ns1:OperationTime>
                                                <ns1:OperationTime End="23:59:00" Start="07:00:00" Sun="true"></ns1:OperationTime>
                                            </ns1:OperationTimes>
                                        </ns1:OperationSchedule>
                                    </ns1:OperationSchedules>
                                </ns1:AdditionalInfo>
                            </ns1:LocationDetails>
                        </ns1:Info>
                    </ns1:VehVendorAvail>
                </ns1:VehVendorAvails>
            </ns1:VehAvailRSCore>
        </ns1:OTA_VehAvailRateRS>
    </soapenv:Body>
</soapenv:Envelope>