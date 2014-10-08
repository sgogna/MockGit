package com.sabre.sabresonic.mockserver.core.generator;

import com.sabre.sabresonic.mockserver.core.config.Config;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class UriBasedFilenameGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(UriBasedFilenameGenerator.class);

    public String generate(final URI uri, final String basePath) {
        try {
            String basePathVal = Config.getStorePath();
            if (basePath != null) {
                basePathVal = FilenameUtils.concat(basePathVal, encode(basePath));
            }

            String filename = FilenameUtils.getName(uri.getPath());
            if (uri.getQuery() != null) {
                filename = uri.getQuery() + "?" + filename;
            }
//StringBuilder sb = new StringBuilder();
            if (filename == null || filename.isEmpty()) {
                filename = "_root_";
            }
            String encodedFilename = encode(filename);

            String path = FilenameUtils.getPath(uri.getPath());
            String encodedPath = encode(path);

            String fullPath = encodedPath + encodedFilename;

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
