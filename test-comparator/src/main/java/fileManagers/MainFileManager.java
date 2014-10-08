/* Copyright 2009 EB2 International Limited */
package fileManagers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sabre.ice.client.cache.shared.SharedCacheException;
import com.sabre.sabresonic.common.cache.api.S3CFactory;
import com.sabre.sabresonic.common.cache.api.ISabreSonicSharedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proxy.compare.FolderComparator;

import com.sabre.ssw.proxy.compare.profile.IERecord;
import com.sabre.ssw.proxy.concurrent.fileManager.ConcurrentRequestsManager;
import com.sabre.ssw.proxy.defines.CommonTestProxyConstants;
import com.sabre.ssw.proxy.defines.ProxyMode;
import com.sabre.ssw.proxy.message.converter.JsonConverter;
import com.sabre.ssw.proxy.message.converter.JsonConverterInterface;

public class MainFileManager {

    private static final Logger LOG = LoggerFactory.getLogger(MainFileManager.class);
    public static final String XML_FILE_EXTENSION = ".xml";
    public static final String HEADER_FILE_EXTENSION = ".zhd";
    public static final String BASE = "BASE";
    private String mainDirPath;

    public static String getTestPath(ProxyMode mode, String airline, String run, String testId) {
        String endTestPath = (run != null ? File.separator + run : "") + (testId != null ? File.separator + testId : "");
        if (mode == null) {
            return airline + File.separator + BASE + endTestPath;
        }
        switch (mode) {
            case RECORD:
                return airline + File.separator + BASE + File.separator + testId;
            case REPLAY:
                return airline + File.separator + ProxyMode.REPLAY.toString() + endTestPath;
            case COMPARE:
                return airline + File.separator + ProxyMode.COMPARE.toString() + endTestPath;
        }
        return "";
    }

    public String getFullTestPath(ProxyMode mode, String airline, String run, String testId) {

        return mainDirPath + File.separator + getTestPath(mode, airline, run, testId);
    }

    public static String getFullTestPath(String mainDirPath, ProxyMode mode, String airline, String run, String testId) {
        return mainDirPath + File.separator + getTestPath(mode, airline, run, testId);
    }

    public void setMainDirPath(String mainDirPath) {
        this.mainDirPath = mainDirPath;
    }


    public static void saveMessage(byte[] arrayByte, String requestName, String path, Properties headers, boolean isRequest) {
        if (requestName == null)
            return;
        LOG.info("Before truncate request name:" + requestName);
        if ( requestName.length() > 200 ) {
            requestName = requestName.substring(0, 200);
            LOG.info("After truncate request name:" + requestName);
        }

        String filepath = path + File.separator + sanitize(requestName) + "_" + (isRequest ? CommonTestProxyConstants.RQ_CONST : CommonTestProxyConstants.RS_CONST);
        File tmp = new File(path);
        if (!tmp.exists()) {
            if ( !tmp.mkdirs() ) {
                LOG.warn("Directory path [" + path + "] could not be created. " + requestName + " will not be saved.");
                return;
            }
        }
        saveFile(arrayByte, filepath + XML_FILE_EXTENSION);
        saveHeaderFile(new File(filepath + HEADER_FILE_EXTENSION), headers);
        if (isRequest && ConcurrentRequestsManager.isRuleFor(requestName)) {
            InfoFileManager.addFileToHashList(path, requestName, ConcurrentRequestsManager.computeHash(requestName, new String(arrayByte), headers));
        }
    }

    private static void saveHeaderFile(File headerFile, Properties headers) {
        if (headers != null) {
            try {
                OutputStream out = new FileOutputStream(headerFile);
                headers.store(out, null);
                out.close();
            } catch (IOException e) {
                LOG.error("Can't write file", e);
            }
        }
    }

    private static void saveFile(byte[] arr, String filepath) {
        ByteArrayInputStream bin = new ByteArrayInputStream(arr);
        try {
            File tmp = new File(filepath);
            if (!tmp.exists()) {
                if ( !tmp.createNewFile() ) {
                    LOG.warn(filepath + " could not be created. file will not be saved.");
                    return;
                }
            }
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filepath), "UTF-8");
            int next = bin.read();
            while (next > -1) {
                out.write(next);
                next = bin.read();
            }
            bin.close();
            out.close();

        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private static byte[] readFile(String filepath) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Reader in = new InputStreamReader(new FileInputStream(filepath), "UTF-8");
            int next = in.read();
            while (next > -1) {
                bos.write(next);
                next = in.read();
            }
            bos.flush();
            in.close();
        } catch (Exception e) {
            LOG.error("Error:", e);
        }
        return bos.toByteArray();
    }

    public static String readFile(File f) {
        String result;
        try {
            result = new String(readFile(f.getAbsolutePath()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("IncorrectEncoding ", e);
            result = "";
        } catch (NullPointerException e) {
            result = "";
        }
        return result;
    }

    public static void readHeaderFile(File headerFile, Properties headers) {
        try {
            FileReader reader = new FileReader(headerFile);
            headers.load(reader);
            for (String key : headers.stringPropertyNames()) {
                String value = headers.getProperty(key);
                if (value.startsWith("==")) {
                    headers.setProperty(key, value.substring(2));
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            LOG.error("NotFoundFile " + headerFile.getName(), e);
        } catch (IOException e) {
            LOG.error("Can't read file " + headerFile.getName(), e);
        }
    }

    public static byte[] readMessage(String requestName, String path, Properties headers, boolean isRequest, String requestText, Properties requestHeader, boolean useSharedCache) {
        if (ConcurrentRequestsManager.isRuleFor(requestName) && !isRequest) {
            String oldRequestName = requestName;
            requestName = InfoFileManager.getHash(path, ConcurrentRequestsManager.computeHash(requestName, requestText, requestHeader));
            LOG.info("Reading file: " + requestName + " instead of " + oldRequestName);
        }

        String filepath = path + File.separator + sanitize(requestName) + "_" + (isRequest ? CommonTestProxyConstants.RQ_CONST : CommonTestProxyConstants.RS_CONST);
        File f = new File(filepath + XML_FILE_EXTENSION);
        if (!f.exists())
            return new byte[0];

        if (headers != null) {
            readHeaderFile(new File(filepath + HEADER_FILE_EXTENSION), headers);
        }

        // Shared cache use (governed by property "use.shared.cache" in proxy.properties)
        // @todo: the sharedCache instance should probably be created in the ProxyHttpServlet and
        // @todo: used here
        if ( useSharedCache ) {
            try {
                ISabreSonicSharedCache sharedCache = S3CFactory.getInstance().getSharedCache();
                sharedCache.setDefaultTTL(100000);
                String response = (String) sharedCache.get(filepath);
                if ( response == null ) {
                    byte[] bytes = readFile(filepath + XML_FILE_EXTENSION);
                    response = new String(bytes, "UTF-8");
                    sharedCache.set(filepath, response);
                }
                return response.getBytes("UTF-8");
            } catch (SharedCacheException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return readFile(filepath + XML_FILE_EXTENSION);
    }

    // Replaces questionable characters with acceptable ones. currently only "*" with "~"
    static String sanitize(String requestName) {
        return requestName.replaceAll("\\*", "~");
    }

    public static File getEqualFileFromSecondDir(String basePath, String path, File f) {
        String requestName = f.getName();
        if (ConcurrentRequestsManager.isRuleFor(f.getName())) {
            Properties headers = new Properties();
            requestName = getRequestNameFromFile(f.getName());
            byte[] message = MainFileManager.readMessage(requestName, basePath, headers, true, null, null, false);
            requestName = InfoFileManager.getHash(path, ConcurrentRequestsManager.computeHash(requestName, new String(message), headers));
            if (requestName == null) {
                requestName = f.getName();
            } else {
                requestName = requestName + f.getName().substring(f.getName().lastIndexOf("_"));
            }
        }
        return new File(path + File.separator + requestName);
    }

    private static String getRequestNameFromFile(String filename) {
        String result = filename.substring(0, filename.lastIndexOf("."));
        result = result.substring(0, result.lastIndexOf("_"));
        return result;
    }

    public List<String> getAirlines() {
        ArrayList<String> airLinesList = new ArrayList<String>();
        File dir = new File(mainDirPath);

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                airLinesList.add(f.getName());
            }
        }
        return airLinesList;
    }

    public Set<String> getRuns() {
        Set<String> runList = new HashSet<String>();
        for (String airline : getAirlines()) {
            runList.addAll(getRunForAirline(airline));
        }
        return runList;
    }

    public Set<String> getRunForAirline(String airline) {
        Set<String> runList = new HashSet<String>();
        File dir = new File(getFullTestPath(ProxyMode.REPLAY, airline, null, null));
        if (dir.exists()) {
            for (String file : dir.list()) {
                if (!file.startsWith(".")) {
                    runList.add(file);
                }
            }
        }
        dir = new File(getFullTestPath(ProxyMode.COMPARE, airline, null, null));
        if (dir.exists()) {
            for (String file : dir.list()) {
                if (!file.startsWith(".")) {
                    runList.add(file);
                }
            }
        }
        return runList;
    }

    public List<File> getFilesForTest(File test, List<IERecord> include, List<IERecord> exclude) {
        return Arrays.asList(FolderComparator.getFileList(test, include, exclude));
    }

    public List<File> getFilesForAll(File dir, List<IERecord> include, List<IERecord> exclude) {
        List<File> fileList = new ArrayList<File>();
        List<String> nameList = new ArrayList<String>();
        for (File test : dir.listFiles()) {
            for (File f : getFilesForTest(test, include, exclude)) {
                if (!nameList.contains(f.getName())) {
                    nameList.add(f.getName());
                    fileList.add(f);
                }
            }
        }
        return fileList;
    }

    public List<File> getFilesFromAllBases() {
        List<File> fileList = new ArrayList<File>();
        List<String> nameList = new ArrayList<String>();
        for (String airline : getAirlines()) {
            File baseDir = new File(getFullTestPath(null, airline, null, null));
            if (baseDir.exists()) {
                for (File f : getFilesForAll(baseDir, null, null)) {
                    if (!nameList.contains(f.getName())) {
                        nameList.add(f.getName());
                        fileList.add(f);
                    }
                }
            }
        }
        return fileList;
    }

    public boolean isTestDir(File f) {
        if (f.isDirectory() && Arrays.asList(f.list()).contains(".info")) {
            return true;
        }
        return false;
    }

    public String getBasicTestFolderPath(File compareFolder, String airline) {
        String test = compareFolder.getName();
        String fileName = compareFolder.getParentFile().getParentFile().getParentFile().getName();
        if (airline == null) {
            airline = fileName;
        }
        return getFullTestPath(null, airline, null, test);
    }

    public Set<File> getTestForRun(String run) {
        Set<File> filesList = new HashSet<File>();
        for (String airline : getAirlines()) {
            filesList.addAll(getTestForRunAndAirline(airline, run));
        }
        return filesList;
    }

    public Set<File> getTestForRunAndAirline(String airline, String run) {
        Set<File> filesList = new HashSet<File>();
        File dir = new File(getFullTestPath(ProxyMode.COMPARE, airline, run, null));
        if (dir.exists()) {
            filesList.addAll(Arrays.asList(dir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    if (name.startsWith(".")) {
                        return false;
                    }
                    return true;
                }
            })));
        }
        dir = new File(getFullTestPath(ProxyMode.REPLAY, airline, run, null));
        if (dir.exists()) {
            filesList.addAll(Arrays.asList(dir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    if (name.startsWith(".")) {
                        return false;
                    }
                    return true;
                }
            })));
        }
        return filesList;

    }

    public static boolean isRunInCompareFolder(File dir) {
        return dir.getParentFile().getName().startsWith(ProxyMode.COMPARE.toString());
    }

    public static boolean isRunInReplayFolder(File dir) {
        return dir.getParentFile().getName().startsWith(ProxyMode.REPLAY.toString());
    }

    public static boolean deleteTest(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                MainFileManager.deleteTest(f);
            }
        }
        return file.delete();
    }

    public static String getFile(File path) {
        return path.getName();
    }

    public static String getHeaderOriginalHostString(Properties header) {
        String testIdString = header.getProperty("testId").split("@")[0];
        JsonConverterInterface convert = new JsonConverter();
        Map<String, String> properties;
        try {
            properties = (Map<String, String>) convert.getMapFromJSON(testIdString);
            String oryginalHost = properties.get("original_host");
            return oryginalHost;
        } catch (Exception e) {
            LOG.error("Incorrect header file. Incorrect testId parameter!", e);
        }
        return null;
    }

    public static Properties getHeaderOriginalHostAttrProperties(String attributeStirng) {
        Properties properties = new Properties();
        for (String s : attributeStirng.split("&")) {
            String[] pv = s.split("=");

            properties.setProperty(pv[0], pv[1]);
        }
        return properties;
    }

    public static List<String> getHeaderOriginalHostAttrList(Properties header) {
        List<String> list = new ArrayList<String>();
        String oryginalHost = getHeaderOriginalHostString(header);
        if (oryginalHost != null && oryginalHost.contains("?")) {
            String[] array = oryginalHost.split("\\?");
            Properties p = getHeaderOriginalHostAttrProperties(array[1]);
            return new ArrayList<String>(p.stringPropertyNames());
        }
        return list;
    }
}
