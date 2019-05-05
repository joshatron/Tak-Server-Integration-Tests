package io.joshatron.tak.server.logic;

import io.joshatron.tak.server.logic.utils.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

//Suite D
public class AdminTest {

    private static final User ADMIN = new User("admin", "password");

    private HttpClient client;

    @BeforeSuite(groups = {"parallel", "serial"})
    public void initializeSuite() throws IOException {
        client = HttpUtils.createHttpClient();
        HttpUtils.initializeAdminAccount(client);
    }

    private String getTest() {
        return "D" + RandomUtils.generateTest(10);
    }

    //Change Password
    @Test(groups = {"serial"})
    public void changePassword_Normal_204PasswordChanged() throws IOException {
        String test = getTest();
        AdminUtils.changePassword(ADMIN, "other_pass", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.changePassword(new User("admin", "other_pass"), "password", client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"serial"})
    public void changePassword_WrongPassword_401PasswordNotChanged() throws IOException {
        String test = getTest();
        AdminUtils.changePassword(new User("admin", "other_pass"), "new_pass", client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test(groups = {"serial"})
    public void changePassword_WrongUsername_401PasswordNotChanged() throws IOException {
        String test = getTest();
        AdminUtils.changePassword(new User("notAdmin", "password"), "new_pass", client, HttpStatus.SC_UNAUTHORIZED);
    }

    //Reset User Password
    @Test(groups = {"parallel"})
    public void resetUserPassword_Normal_200UserPasswordChanged() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword(AdminUtils.resetUserPassword(ADMIN, user.getUserId(), client, HttpStatus.SC_OK));
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void resetUserPassword_MultipleUsers_200UserPasswordChanged() throws IOException {
        String test = getTest();
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword(AdminUtils.resetUserPassword(ADMIN, user1.getUserId(), client, HttpStatus.SC_OK));
        AccountUtils.authenticate(user1, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void resetUserPassword_InvalidPassword_401UserPasswordNotChanged() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.resetUserPassword(new User("admin", "not_pass"), user.getUserId(), client, HttpStatus.SC_UNAUTHORIZED);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void resetUserPassword_InvalidUser_404UserPasswordNotChanged() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.resetUserPassword(ADMIN, "000000000000000", client, HttpStatus.SC_NOT_FOUND);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    //Ban User
    @Test(groups = {"parallel"})
    public void banUser_Normal_200UserBanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test(groups = {"parallel"})
    public void banUser_MultipleUsers_200UserBanned() throws IOException {
        String test = getTest();
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user1.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user1, client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.authenticate(user2, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void banUser_InvalidPassword_401UserNotBanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(new User("admin", "drowssap"), user.getUserId(), client, HttpStatus.SC_UNAUTHORIZED);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void banUser_InvalidUserToBan_404UserNotBanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, "000000000000000", client, HttpStatus.SC_NOT_FOUND);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void banUser_UserBanned_403UserStillBanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
    }

    //Unban User
    @Test(groups = {"parallel"})
    public void unbanUser_Normal_200UserUnbanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
        AdminUtils.unbanUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void unbanUser_MultipleUsers_200UserUnbanned() throws IOException {
        String test = getTest();
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user1.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user1, client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.authenticate(user2, client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.unbanUser(ADMIN, user1.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user1, client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user2, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void unbanUser_InvalidPassword_401UserNotUnbanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
        AdminUtils.unbanUser(new User("admin", "drowssap"), user.getUserId(), client, HttpStatus.SC_UNAUTHORIZED);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test(groups = {"parallel"})
    public void unbanUser_InvalidUserToUnban_404UserNotUnbanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
        AdminUtils.unbanUser(ADMIN, "000000000000000", client, HttpStatus.SC_NOT_FOUND);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test(groups = {"parallel"})
    public void unbanUser_NotBanned_403UserNotUnbanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.unbanUser(ADMIN, user.getUserId(), client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    private void lockUser(User u) throws IOException {
        User user = new User(u.getUsername(), u.getPassword() + "_not", u.getUserId());

        for(int i = 0; i < AccountUtils.TRIES_TO_LOCK; i++) {
            AccountUtils.authenticate(user, client, HttpStatus.SC_UNAUTHORIZED);
        }
    }

    //Unlock user
    @Test(groups = {"parallel"})
    public void unlockUser_Normal_204UserUnlocked() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        lockUser(user);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
        AdminUtils.unlockUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test(groups = {"parallel"})
    public void unlockUser_MultipleUsers_204UserUnlocked() throws IOException {
        String test = getTest();
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        lockUser(user1);
        lockUser(user2);
        AccountUtils.authenticate(user1, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
        AccountUtils.authenticate(user2, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
        AdminUtils.unlockUser(ADMIN, user1.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user1, client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user2, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
    }

    @Test(groups = {"parallel"})
    public void unlockUser_NotLocked_403UserStillUnlocked() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.unlockUser(ADMIN, user.getUserId(), client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }


    @Test(groups = {"parallel"})
    public void unlockUser_Banned_403NotLockedStillBanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "BANNED");
        AdminUtils.unlockUser(ADMIN, user.getUserId(), client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "BANNED");
    }


    @Test(groups = {"parallel"})
    public void unlockUser_LockedThenBanned_403NotLockedStillBanned() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        lockUser(user);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
        AdminUtils.banUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "BANNED");
        AdminUtils.unlockUser(ADMIN, user.getUserId(), client, HttpStatus.SC_FORBIDDEN);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "BANNED");
        AdminUtils.unbanUser(ADMIN, user.getUserId(), client, HttpStatus.SC_NO_CONTENT);
        AccountUtils.authenticate(user, client, HttpStatus.SC_NO_CONTENT);
    }


    @Test(groups = {"parallel"})
    public void unlockUser_InvalidPassword_401NotUnlocked() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        lockUser(user);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
        AdminUtils.unlockUser(new User("admin", "not_pass"), user.getUserId(), client, HttpStatus.SC_UNAUTHORIZED);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
    }


    @Test(groups = {"parallel"})
    public void unlockUser_InvalidUser_404NothingUnlocked() throws IOException {
        String test = getTest();
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        lockUser(user);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
        AdminUtils.unlockUser(ADMIN, "000000000000000", client, HttpStatus.SC_NOT_FOUND);
        AccountUtils.authenticate(user, client, HttpStatus.SC_FORBIDDEN, "LOCKED");
    }
}
