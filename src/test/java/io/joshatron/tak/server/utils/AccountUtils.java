package io.joshatron.tak.server.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;

public class AccountUtils {

    public static User addUser(String suite, String test, String user, String password, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        if(suite == null || test == null || user == null) {
            response = HttpUtils.createUser(null, password, client);
        }
        else {
            response = HttpUtils.createUser(suite + test + user, password, client);
        }
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(expected == HttpStatus.SC_NO_CONTENT) {
            UserInfo info = seachUsers(suite + test + user, null, client, HttpStatus.SC_OK);
            User u = new User(suite + test + user, password, info.getUserId());
            authenticate(u, client, HttpStatus.SC_NO_CONTENT);

            return u;
        }
        else {
            return null;
        }
    }

    public static void changeUsername(User user, String newName, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        if(user != null) {
            response = HttpUtils.changeUsername(user.getUsername(), user.getPassword(), newName, client);
        }
        else {
            response = HttpUtils.changeUsername(null, null, newName, client);
        }
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(expected == HttpStatus.SC_NO_CONTENT) {
            user.setUsername(newName);
            authenticate(user, client, HttpStatus.SC_NO_CONTENT);
        }
    }

    public static void changePassword(User user, String newPass, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        if(user != null) {
            response = HttpUtils.changePassword(user.getUsername(), user.getPassword(), newPass, client);
        }
        else {
            response = HttpUtils.changePassword(null, null, newPass, client);
        }
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(expected == HttpStatus.SC_NO_CONTENT) {
            user.setPassword(newPass);
            authenticate(user, client, HttpStatus.SC_NO_CONTENT);
        }
    }

    public static void authenticate(User user, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        if(user != null) {
            response = HttpUtils.authenticate(user.getUsername(), user.getPassword(), client);
        }
        else {
            response = HttpUtils.authenticate(null, null, client);
        }
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static UserInfo seachUsers(String username, String userId, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        response = HttpUtils.searchUser(username, userId, client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());

        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(contents);

            UserInfo info = new UserInfo(json.getString("username"), json.getString("userId"));

            if(username != null) {
                Assert.assertEquals(username, info.getUsername());
            }
            else {
                Assert.assertNotNull(info.getUsername());
            }

            if(userId != null) {
                Assert.assertEquals(userId, info.getUserId());
            }
            else {
                Assert.assertEquals(15, info.getUserId().length());
            }

            return info;
        }
        else {
            return null;
        }
    }
}
