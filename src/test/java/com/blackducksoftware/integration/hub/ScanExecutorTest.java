package com.blackducksoftware.integration.hub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.blackducksoftware.integration.hub.ScanExecutor.Result;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.util.TestLogger;

public class ScanExecutorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static Properties testProperties;

    @BeforeClass
    public static void testInit() {
        testProperties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("test.properties");
        try {
            testProperties.load(is);
        } catch (IOException e) {
            System.err.println("reading test.properties failed!");
        }
        // p.load(new FileReader(new File("test.properties")));
        System.out.println(testProperties.getProperty("TEST_HUB_SERVER_URL"));
        System.out.println(testProperties.getProperty("TEST_USERNAME"));
        System.out.println(testProperties.getProperty("TEST_PASSWORD"));

    }

    @Test
    public void testNoURL() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No Hub URL provided.");
        new ScanExecutor(null, null, null, null, null) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
    }

    @Test
    public void testNoUsername() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No Hub username provided.");
        new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), null, null, null, null) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
    }

    @Test
    public void testNoPassword() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No Hub password provided.");
        new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"), null, null,
                null) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
    }

    @Test
    public void testNoTargets() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No scan targets provided.");
        new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), null, null) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
    }

    @Test
    public void testNoBuildNumber() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No build number provided.");

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, null) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
    }

    @Test
    public void testValidConstructor() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
    }

    @Test
    public void testSetupNoLogger() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
        Result result = executor.setupAndRunScan(null, null, null);
        assertEquals(Result.FAILURE, result);
    }

    @Test
    public void testSetupNoScanExec() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        Result result = executor.setupAndRunScan(null, null, null);
        assertEquals(Result.FAILURE, result);

        String output = logger.getOutputString();
        assertTrue(output, output.contains("Please provide the Hub scan CLI."));
    }

    @Test
    public void testSetupScanExecDoesNotExist() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        Result result = executor.setupAndRunScan(new File("./Fake"), null, null);
        assertEquals(Result.FAILURE, result);

        String output = logger.getOutputString();
        assertTrue(output, output.contains("The Hub scan CLI provided does not exist."));
    }

    @Test
    public void testSetupNoOneJarFile() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        Result result = executor.setupAndRunScan(new File("."), null, null);
        assertEquals(Result.FAILURE, result);

        String output = logger.getOutputString();
        assertTrue(output, output.contains("Please provide the path for the CLI cache."));
    }

    @Test
    public void testSetupNoJavaExec() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        Result result = executor.setupAndRunScan(new File("."), new File("."), null);
        assertEquals(Result.FAILURE, result);

        String output = logger.getOutputString();
        assertTrue(output, output.contains("Please provide the java home directory."));
    }

    @Test
    public void testSetupJavaExecDoesNotExist() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return null;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        Result result = executor.setupAndRunScan(new File("."), new File("."), new File("./Fake"));
        assertEquals(Result.FAILURE, result);

        String output = logger.getOutputString();
        assertTrue(output, output.contains("The Java home provided does not exist."));
    }

    @Test
    public void testSetupNoMemoryProvided() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                return Result.SUCCESS;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        Result result = executor.setupAndRunScan(new File("."), new File("."), new File("."));
        assertEquals(Result.SUCCESS, result);

        String output = logger.getOutputString();
        assertTrue(output, output.contains("No memory set for the HUB CLI. Will use the default memory, "));
    }

    @Test
    public void testSetupValid() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        final List<String> cmdList = new ArrayList<String>();

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                cmdList.addAll(cmd);
                return Result.SUCCESS;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        executor.setScanMemory(8192);

        Result result = executor.setupAndRunScan(new File("."), new File("."), new File("."));
        assertEquals(Result.SUCCESS, result);

        String output = logger.getOutputString();

        assertTrue(output, !cmdList.isEmpty());

        StringBuilder builder = new StringBuilder();
        for (String currCmd : cmdList) {
            builder.append(currCmd);
            builder.append(" ");
        }
        String actualCmd = builder.toString();
        assertTrue(actualCmd, StringUtils.isNotBlank(actualCmd));

        assertTrue(actualCmd, actualCmd.contains("-Done-jar.silent=true"));
        assertTrue(actualCmd, actualCmd.contains("-Done-jar.jar.path="));
        assertTrue(actualCmd, actualCmd.contains("-Xmx"));
        assertTrue(actualCmd, actualCmd.contains("-jar"));
        assertTrue(actualCmd, actualCmd.contains("--scheme"));
        assertTrue(actualCmd, actualCmd.contains("--host"));
        assertTrue(actualCmd, actualCmd.contains("--username"));
        assertTrue(actualCmd, actualCmd.contains("--password"));
        assertTrue(actualCmd, actualCmd.contains("--port"));
        assertTrue(actualCmd, actualCmd.contains(scanTargets.get(0).getCanonicalPath()));

    }

    @Test
    public void testSetupHubSupportsLogOption() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        final List<String> cmdList = new ArrayList<String>();

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                cmdList.addAll(cmd);
                return Result.SUCCESS;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);
        executor.setScanMemory(8192);
        executor.setHubSupportLogOption(true);

        Result result = executor.setupAndRunScan(new File("."), new File("."), new File("."));
        assertEquals(Result.SUCCESS, result);

        String output = logger.getOutputString();

        assertTrue(output, !cmdList.isEmpty());

        StringBuilder builder = new StringBuilder();
        for (String currCmd : cmdList) {
            builder.append(currCmd);
            builder.append(" ");
        }
        String actualCmd = builder.toString();
        assertTrue(actualCmd, StringUtils.isNotBlank(actualCmd));

        assertTrue(actualCmd, actualCmd.contains("-Done-jar.silent=true"));
        assertTrue(actualCmd, actualCmd.contains("-Done-jar.jar.path="));
        assertTrue(actualCmd, actualCmd.contains("-Xmx"));
        assertTrue(actualCmd, actualCmd.contains("-jar"));
        assertTrue(actualCmd, actualCmd.contains("--scheme"));
        assertTrue(actualCmd, actualCmd.contains("--host"));
        assertTrue(actualCmd, actualCmd.contains("--username"));
        assertTrue(actualCmd, actualCmd.contains("--password"));
        assertTrue(actualCmd, actualCmd.contains("--port"));
        assertTrue(actualCmd, actualCmd.contains("--logDir"));
        assertTrue(actualCmd, actualCmd.contains(scanTargets.get(0).getCanonicalPath()));

    }

    @Test
    public void testSetupProxySettings() throws Exception {

        ArrayList<File> scanTargets = new ArrayList<File>();
        scanTargets.add(new File(""));

        final List<String> cmdList = new ArrayList<String>();

        ScanExecutor executor = new ScanExecutor(testProperties.getProperty("TEST_HUB_SERVER_URL"), testProperties.getProperty("TEST_USERNAME"),
                testProperties.getProperty("TEST_PASSWORD"), scanTargets, 123) {

            @Override
            protected Result executeScan(List<String> cmd) throws HubIntegrationException, InterruptedException {
                cmdList.addAll(cmd);
                return Result.SUCCESS;
            }

        };
        TestLogger logger = new TestLogger();
        executor.setLogger(logger);

        ArrayList<Pattern> noProxyHosts = new ArrayList<Pattern>();
        noProxyHosts.add(Pattern.compile("test"));
        executor.setProxyHost(testProperties.getProperty("TEST_PROXY_HOST_BASIC"));
        executor.setProxyPort(Integer.valueOf(testProperties.getProperty("TEST_PROXY_PORT_BASIC")));
        executor.setProxyUsername(testProperties.getProperty("TEST_PROXY_USER_BASIC"));
        executor.setProxyPassword(testProperties.getProperty("TEST_PROXY_PASSWORD_BASIC"));
        executor.setNoProxyHosts(noProxyHosts);

        Result result = executor.setupAndRunScan(new File("."), new File("."), new File("."));
        assertEquals(Result.SUCCESS, result);

        String output = logger.getOutputString();

        assertTrue(output, !cmdList.isEmpty());

        StringBuilder builder = new StringBuilder();
        for (String currCmd : cmdList) {
            builder.append(currCmd);
            builder.append(" ");
        }
        String actualCmd = builder.toString();
        assertTrue(actualCmd, StringUtils.isNotBlank(actualCmd));

        assertTrue(actualCmd, actualCmd.contains("-Done-jar.silent=true"));
        assertTrue(actualCmd, actualCmd.contains("-Done-jar.jar.path="));

        assertTrue(actualCmd, actualCmd.contains("-Dhttp.proxyHost="));
        assertTrue(actualCmd, actualCmd.contains("-Dhttp.proxyPort="));
        assertTrue(actualCmd, actualCmd.contains("-Dhttps.proxyHost="));
        assertTrue(actualCmd, actualCmd.contains("-Dhttps.proxyPort="));
        assertTrue(actualCmd, actualCmd.contains("-Dhttp.nonProxyHosts="));

        assertTrue(actualCmd, actualCmd.contains("-Dhttp.proxyUser="));
        assertTrue(actualCmd, actualCmd.contains("-Dhttp.proxyPassword="));

        assertTrue(actualCmd, actualCmd.contains("-Xmx"));
        assertTrue(actualCmd, actualCmd.contains("-jar"));
        assertTrue(actualCmd, actualCmd.contains("--scheme"));
        assertTrue(actualCmd, actualCmd.contains("--host"));
        assertTrue(actualCmd, actualCmd.contains("--username"));
        assertTrue(actualCmd, actualCmd.contains("--password"));
        assertTrue(actualCmd, actualCmd.contains("--port"));
        assertTrue(actualCmd, actualCmd.contains(scanTargets.get(0).getCanonicalPath()));

    }

}
