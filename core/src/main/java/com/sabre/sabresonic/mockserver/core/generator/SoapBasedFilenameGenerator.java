package com.sabre.sabresonic.mockserver.core.generator;

import com.sabre.sabresonic.mockserver.core.config.Config;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class SoapBasedFilenameGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(SoapBasedFilenameGenerator.class);

    public String generate(String filename, final String basePath) {
        try {
            String basePathVal = Config.getStorePath();
            if (basePath != null) {
                basePathVal = FilenameUtils.concat(basePathVal, encode(basePath));
            }
            if (filename == null || filename.isEmpty()) {
                filename = "_root_";
            }

            String fullPath =  encode(filename);

            File file = new File(basePathVal, fullPath);

            LOG.debug("Generated filename: " + file.getPath());

            return file.getPath();
        } catch (UnsupportedEncodingException ex) {
            LOG.error(null, ex);
        }
        return null;

    }

    private String encode(final String input)
            throws UnsupportedEncodingException {

        final String encoded = URLEncoder.encode(input, "UTF-8");
        return encoded
                .replaceAll("%2F", "/")
                .replaceAll("%5B", "[")
                .replaceAll("%5D", "]")
                .replaceAll("\\+", " ")
                .replaceAll("%3D", "=")
                .replaceAll("%26", "&")
                .replaceAll("%2C", ",");
    }
}
