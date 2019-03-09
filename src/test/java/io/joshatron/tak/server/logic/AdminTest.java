package io.joshatron.tak.server.logic;

import io.joshatron.tak.server.logic.utils.HttpUtils;
import io.joshatron.tak.server.logic.utils.RandomUtils;
import org.apache.http.client.HttpClient;
import org.junit.Before;

import java.io.IOException;

//Suite D
public class AdminTest {

    private HttpClient client;
    private String test;

    public AdminTest() throws IOException {
        client = HttpUtils.createHttpClient();
    }

    @Before
    public void initializeTest() {
        test = "D" + RandomUtils.generateTest(10);
    }

    //Initialize
    //Change Password
    //Reset User Password
    //Ban User
    //Unban User
}
