package io.joshatron.tak.server.logic;

import io.joshatron.tak.server.logic.utils.HttpUtils;
import io.joshatron.tak.server.logic.utils.RandomUtils;
import org.apache.http.client.HttpClient;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;

public class BaseTest {

    protected HttpClient client;

    @BeforeSuite(groups = {"parallel", "serial"})
    public void initializeSuite() throws IOException {
        client = HttpUtils.createHttpClient();
    }

    protected String getTest() {
        String test = RandomUtils.generateTest(10);
        System.out.println("Test Base ID: " + test);
        return test;
    }
}
