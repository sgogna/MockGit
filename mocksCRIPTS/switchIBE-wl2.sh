#!/bin/bash
# Usage: ./switchIBE.sh [options]

HOST=localhost             		# jmx host for IBE

PORT=9006                   		# jmx port for IBE

MOCK_URL=sswhli474.mgmt.sabre.com	# URL for MOCK

MOCK_PORT=14080				# Port for MOCK	

USG_URL=sws-crt.internal.cert.sabre.com	# URL for USG


# show usage
usage() {
	 echo ""
        echo "Usage: switchIBE <mock/usg>"
	 echo ""
        echo " mock     		- to switch to mock"
        echo " usg     		- to switch to usg"
        echo " all     		- the switch will change all configured airlines"
	 echo " storefront      	- the switch will change specific airlines. Airlines Ipcc should be comma seperated for example CYCY,F7F7 etc...."
        echo " getIpcc      	- get all the configured Ipccs"
}

#Method to switch to mock for all airlines
switchToMockAll() {
getIpcc
var=$(sed -e 's/.*getConfiguredIpccs://g' tmpMock)
var=${var//[[:space:]]/}
arr=$(echo $var | tr "," "\n")
for x in $arr
do
tmp=$x
ipcc=${tmp##*:}
changeMock
done
}

#Method to switch to mock for all airlines
switchToMock() {
getIpcc
var=$(sed -e 's/.*getConfiguredIpccs://g' tmpMock)
var=${var//[[:space:]]/}
arr=$(echo $var | tr "," "\n")
for x in $arr
do
tmp=$x
ipcc=${tmp##*:}
if [ ! -z "$(echo $1 | awk /$ipcc/)" ]
then 
changeMock
fi
done
}

#Method to switch to method to switch to USG
switchToUSGAll() {
getIpcc
var=$(sed -e 's/.*getConfiguredIpccs://g' tmpMock)
var=${var//[[:space:]]/}
arr=$(echo $var | tr "," "\n")
for x in $arr
do
tmp=$x
ipcc=${tmp##*:}
changeUSG
done
}

#Method to switch to usg for specific airlines
switchToUSG() {
getIpcc
var=$(sed -e 's/.*getConfiguredIpccs://g' tmpMock)
var=${var//[[:space:]]/}
arr=$(echo $var | tr "," "\n")
for x in $arr
do
tmp=$x
ipcc=${tmp##*:}
if [ ! -z "$(echo $1 | awk /$ipcc/)" ]
then 
changeUSG
fi
done
}

getIpcc()
{
java -jar cmdline-jmxclient-0.10.3.jar - $HOST:$PORT qtrip:path=SSW2010.server,name=configuration getConfiguredIpccs 2>tmpMock
if [ "$1" == getIpcc ]
then
var=$(sed -e 's/.*getConfiguredIpccs://g' tmpMock)
var=${var//[[:space:]]/}
var=$(sed -e 's/.*getConfiguredIpccs://g' tmpMock)
var=${var//[[:space:]]/}
arr=$(echo $var | tr "," "\n")
for x in $arr
do
echo [$x]
done
fi
}

changeMock()
{
java -jar cmdline-jmxclient-0.10.3.jar - $HOST:$PORT qtrip:path=SSW2010.server,name=configuration setConfigurationProperty=$ipcc,gds.sabre.ws.endpoint,http://$MOCK_URL:$MOCK_PORT/testproxy/s?TestRun$ipcc,null
java -jar cmdline-jmxclient-0.10.3.jar - $HOST:$PORT qtrip:path=SSW2010.server,name=configuration setConfigurationProperty=$ipcc,usg.ws.endpoint,http://$MOCK_URL:$MOCK_PORT/testproxy/s?TestRun$ipcc,null
echo Changed $ipcc to point to http://$MOCK_URL:$MOCK_PORT/testproxy/s?TestRun$ipcc
}

changeUSG()
{
java -jar cmdline-jmxclient-0.10.3.jar - $HOST:$PORT qtrip:path=SSW2010.server,name=configuration setConfigurationProperty=$ipcc,gds.sabre.ws.endpoint,http://$USG_URL,null
java -jar cmdline-jmxclient-0.10.3.jar - $HOST:$PORT qtrip:path=SSW2010.server,name=configuration setConfigurationProperty=$ipcc,usg.ws.endpoint,http://$USG_URL,null
echo Changed $ipcc to point to http://$USG_URL
}

if [ "$1" == mock ] && [ "$2" == all ]
then
switchToMockAll
elif [ "$1" == usg ] && [ "$2" == all ]
then
switchToUSGAll
elif [ "$1" == mock ] && [ "$2" != all ] && [ "$2" != "" ]
then
switchToMock "$2"
elif [ "$1" == usg ] && [ "$2" != all ] && [ "$2" != "" ]
then
switchToUSG "$2"
elif [ "$1" == getIpcc ] 
then
getIpcc $1
elif [ "$1" == help ] 
then
usage
else
usage
fi
