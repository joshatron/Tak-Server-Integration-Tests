package io.joshatron.tak.server.logic.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;

public class AdminUtils {

    public static String initialize(HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.initializeAdminAccount(client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        //This is from the server default password
        return "password";
    }

    public static void changePassword(User user, String newPass, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        if(user != null) {
            response = HttpUtils.changeAdminPassword(user.getUsername(), user.getPassword(), newPass, client);
        }
        else {
            response = HttpUtils.changeAdminPassword(null, null, newPass, client);
        }
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(expected == HttpStatus.SC_NO_CONTENT) {
            user.setPassword(newPass);
        }
    }

    public static String resetUserPassword(User user, String toReset, HttpClient client, int expected) throws IOException {
        Response response;
        if(user != null) {
            response = HttpUtils.resetUserPassword(user.getUsername(), user.getPassword(), toReset, client);
        }
        else {
            response = HttpUtils.resetUserPassword(null, null, toReset, client);
        }
        Assert.assertEquals(expected, response.getStatus());
        if(response.getStatus() == HttpStatus.SC_OK) {
            JSONObject json = new JSONObject(response.getContents());

            return json.getString("text");
        }

        return null;
    }

    public static void banUser(User user, String toBan, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        if(user != null) {
            response = HttpUtils.banUser(user.getUsername(), user.getPassword(), toBan, client);
        }
        else {
            response = HttpUtils.banUser(null, null, toBan, client);
        }
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void unbanUser(User user, String toUnban, HttpClient client, int expected) throws IOException {
        HttpResponse response;
        if(user != null) {
            response = HttpUtils.unbanUser(user.getUsername(), user.getPassword(), toUnban, client);
        }
        else {
            response = HttpUtils.unbanUser(null, null, toUnban, client);
        }
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }
}
