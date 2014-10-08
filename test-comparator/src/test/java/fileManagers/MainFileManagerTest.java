package fileManagers;

import com.sabre.ssw.proxy.concurrent.fileManager.ConcurrentRequestsManager;
import com.sabre.ssw.proxy.concurrent.fileManager.ConcurrentRequestsRuleFilesManager;
import com.sabre.ssw.proxy.concurrent.managment.ConcurrentRequestRule;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * MainFileManagerTest
 */
public class MainFileManagerTest {
    @BeforeMethod
    public void setUp() throws Exception {
        setupConsoleLogging();
        ConcurrentRequestsManager crm = new ConcurrentRequestsManager();
        crm.setRequestsRuleFilesManager(new ConcurrentRequestsRuleFilesManager() {
            @Override
            public List<ConcurrentRequestRule> readRules() {
                return new ArrayList<ConcurrentRequestRule>();
            }
        });
        crm.readRules();
    }

    private void setupConsoleLogging() {
        ConsoleAppender console = new ConsoleAppender();
        console.setWriter(new PrintWriter(System.out));
        console.setLayout(new SimpleLayout());
        Logger.getRootLogger().addAppender(console);
    }

    @Test
    public void testSanitize() throws Exception {
        assert "VI~1".equals(MainFileManager.sanitize("VI*1"));

    }
}
