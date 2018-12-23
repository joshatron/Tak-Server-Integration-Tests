package io.joshatron.tak.server;

import io.joshatron.tak.server.utils.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

//Current final test: 025
public class AccountTest {

    private final String suite = RandomUtils.generateSuite(10);
    private HttpClient client;

    public AccountTest() {
        client = HttpUtils.createHttpClient();
    }

    //Register User
    @Test
    public void registerUser_OneUser_204() throws IOException {
        String test = "001";
        AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void registerUser_MultipleUsers_204() throws IOException {
        String test = "002";
        AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.addUser(suite, test, "03", "drowssap", client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void registerUser_UserAlreadyCreated_403() throws IOException {
        String test = "003";
        AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.addUser(suite, test, "01", "drowssap", client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void registerUser_TryToRegisterWithDifferentCase_403() throws IOException {
        String test = "013";
        AccountUtils.addUser(suite, test, "aa", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.addUser(suite, test, "AA", "password", client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void registerUser_BlankUsername_400() throws IOException {
        String test = "014";
        AccountUtils.addUser("", "", "", "password", client, HttpStatus.SC_BAD_REQUEST);
        AccountUtils.addUser(null, null, null, "password", client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void registerUser_BlankPassword_400() throws IOException {
        String test = "018";
        AccountUtils.addUser(suite, test, "01", "", client, HttpStatus.SC_BAD_REQUEST);
        AccountUtils.addUser(suite, test, "02", null, client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void registerUser_ColonInUsername_400() throws IOException {
        String test = "016";
        AccountUtils.addUser(suite, test, ":01", "password", client, HttpStatus.SC_BAD_REQUEST);
        AccountUtils.addUser(suite, test, "01:", "password", client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void registerUser_NonAlphanumericUsername_400() throws IOException {
        String test = "019";
        AccountUtils.addUser(suite, test, "01!", "password", client, HttpStatus.SC_BAD_REQUEST);
        AccountUtils.addUser(suite, test, "01,", "password", client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void registerUser_ColonInPassword_204() throws IOException {
        String test = "017";
        AccountUtils.addUser(suite, test, "01", ":password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.addUser(suite, test, "02", "pass:word", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.addUser(suite, test, "03", "password:", client, HttpStatus.SC_NO_CONTENT);
    }

    //Change Username
    @Test
    public void changeUsername_OneUser_204() throws IOException {
        String test = "020";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changeUsername(user, suite + test + "02", client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void changeUsername_WrongPassword_401() throws IOException {
        String test = "021";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("pass");
        AccountUtils.changeUsername(user, suite + test + "02", client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void changeUsername_InvalidUser_401() throws IOException {
        String test = "022";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setUsername(suite + test + "02");
        AccountUtils.changeUsername(user, suite + test + "03", client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void changeUsername_BlankFields_400() throws IOException {
        String test = "023";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changeUsername(user, "", client, HttpStatus.SC_BAD_REQUEST);
        AccountUtils.changeUsername(user, null, client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void changeUsername_MultipleUsers_204() throws IOException {
        String test = "024";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "12345678", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changeUsername(user1, suite + test + "03", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changePassword(user2, suite + test + "04", client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void changeUsername_OtherUserUsername_403() throws IOException {
        String test = "025";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "12345678", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changeUsername(user1, suite + test + "02", client, HttpStatus.SC_FORBIDDEN);
    }

    //Change Password
    @Test
    public void changePassword_OneUser_204() throws IOException {
        String test = "004";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changePassword(user, "drowssap", client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void changePassword_WrongPassword_401() throws IOException {
        String test = "005";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("pass");
        AccountUtils.changePassword(user, "drowssap", client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void changePassword_InvalidUser_401() throws IOException {
        String test = "006";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setUsername(suite + test + "02");
        AccountUtils.changePassword(user, "drowssap", client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void changePassword_BlankFields_400() throws IOException {
        String test = "007";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changePassword(user, "", client, HttpStatus.SC_BAD_REQUEST);
        AccountUtils.changePassword(user, null, client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void changePassword_MultipleUsers_204() throws IOException {
        String test = "008";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "12345678", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changePassword(user1, "drowssap", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.changePassword(user2, "87654321", client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void changePassword_OtherUserPassword_401() throws IOException {
        String test = "009";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "12345678", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("12345678");
        AccountUtils.changePassword(user1, "drowssap", client, HttpStatus.SC_UNAUTHORIZED);
    }

    //Authenticate User
    @Test
    public void authenticate_Valid_204() throws IOException {
        String test = "010";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void authenticate_WrongPassword_401() throws IOException {
        String test = "011";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        AccountUtils.authenticate(user, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void authenticate_InvalidUser_401() throws IOException {
        String test = "012";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setUsername(suite + test + "02");
        AccountUtils.authenticate(user, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void authenticate_BlankFields_400() throws IOException {
        String test = "015";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User blankName = new User("", "password");
        User blankPass = new User(user.getUsername(), "");
        AccountUtils.authenticate(blankName, client, HttpStatus.SC_BAD_REQUEST);
        AccountUtils.authenticate(blankPass, client, HttpStatus.SC_BAD_REQUEST);
    }

    //Search User
    @Test
    public void searchUser_ExistingUserFromUsername_200() throws IOException {
        String test = "026";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.seachUsers(user.getUsername(), null, client, HttpStatus.SC_OK);
    }

    @Test
    public void searchUser_ExistingUserFromUserId_200() throws IOException {
        String test = "027";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        UserInfo info = AccountUtils.seachUsers(user.getUsername(), null, client, HttpStatus.SC_OK);
        UserInfo info2 = AccountUtils.seachUsers(null, info.getUserId(), client, HttpStatus.SC_OK);
        Assert.assertEquals(user.getUsername(), info2.getUsername());
    }

    @Test
    public void searchUser_invalidUsername_404() throws IOException {
        String test = "028";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.seachUsers(suite + test + "02", null, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void searchUser_invalidUserId_404() throws IOException {
        String test = "029";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.seachUsers(null, "000000000000000", client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void searchUser_invalidUserIdLength_400() throws IOException {
        String test = "030";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.seachUsers(null, "0000000", client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void searchUser_BlankUsername_400() throws IOException {
        String test = "031";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.seachUsers("", null, client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void searchUser_bothNull_400() throws IOException {
        String test = "032";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.seachUsers(null, null, client, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void searchUser_bothFilled_400() throws IOException {
        String test = "033";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        UserInfo info = AccountUtils.seachUsers(user.getUsername(), null, client, HttpStatus.SC_OK);
        AccountUtils.seachUsers(info.getUsername(), info.getUserId(), client, HttpStatus.SC_BAD_REQUEST);
    }
}
