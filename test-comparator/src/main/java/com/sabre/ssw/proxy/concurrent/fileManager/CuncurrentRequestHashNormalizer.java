package com.sabre.ssw.proxy.concurrent.fileManager;

public class CuncurrentRequestHashNormalizer {

	public static String normalize(String s) {
		if (!s.contains("SabreCommandLLSRQ"))
		{
			return s;	
		} else
		{
			return s.replaceFirst("HostCommand>W\\*2A3939\\w{2}90100010<", "HostCommand>W*2A3939F790100010<");
		}												
	}

}
