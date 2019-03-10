package io.joshatron.tak.server.logic;

import io.joshatron.tak.server.logic.utils.HttpUtils;
import io.joshatron.tak.server.logic.utils.RandomUtils;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

//Suite D
public class AdminTest {

    private HttpClient client;
    private String test;

    public AdminTest() throws IOException {
        client = HttpUtils.createHttpClient();
        HttpUtils.initializeAdminAccount(client);
    }

    @Before
    public void initializeTest() {
        test = "D" + RandomUtils.generateTest(10);
    }

    //Change Password
    @Test
    public void changePassword_Normal_204PasswordChanged() throws IOException {
    }

    @Test
    public void changePassword_WrongPassword_401PasswordNotChanged() throws IOException {
    }

    @Test
    public void changePassword_WrongUsername_401PasswordNotChanged() throws IOException {
    }

    //Reset User Password
    @Test
    public void resetUserPassword_Normal_200UserPasswordChanged() throws IOException {
    }

    @Test
    public void resetUserPassword_MultipleUsers_200UserPasswordChanged() throws IOException {
    }

    @Test
    public void resetUserPassword_InvalidPassword_401UserPasswordNotChanged() throws IOException {
    }

    @Test
    public void resetUserPassword_InvalidUser_404UserPasswordNotChanged() throws IOException {
    }

    //Ban User
    @Test
    public void banUser_Normal_200UserBanned() throws IOException {
    }

    @Test
    public void banUser_MultipleUsers_200UserBanned() throws IOException {
    }

    @Test
    public void banUser_InvalidPassword_401UserNotBanned() throws IOException {
    }

    @Test
    public void banUser_InvalidUser_404UserNotBanned() throws IOException {
    }

    @Test
    public void banUser_UserBanned_401UserNotBanned() throws IOException {
    }

    //Unban User
    @Test
    public void unbanUser_Normal_200UserUnbanned() throws IOException {
    }

    @Test
    public void unbanUser_MultipleUsers_200UserUnbanned() throws IOException {
    }

    @Test
    public void unbanUser_InvalidPassword_401UserNotUnbanned() throws IOException {
    }

    @Test
    public void unbanUser_InvalidUser_404UserNotUnbanned() throws IOException {
    }

    @Test
    public void unbanUser_NotBanned_401UserNotUnbanned() throws IOException {
    }
}
