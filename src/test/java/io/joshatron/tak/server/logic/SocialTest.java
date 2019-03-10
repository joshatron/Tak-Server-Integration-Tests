package io.joshatron.tak.server.logic;

import io.joshatron.tak.server.logic.utils.AccountUtils;
import io.joshatron.tak.server.logic.utils.HttpUtils;
import io.joshatron.tak.server.logic.utils.RandomUtils;
import io.joshatron.tak.server.logic.utils.SocialUtils;
import io.joshatron.tak.server.logic.utils.User;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

//Suite: B
public class SocialTest {

    private HttpClient client;
    private String test;

    @Before
    public void initializeTest() {
        test = "B" + RandomUtils.generateTest(10);
        client = HttpUtils.createHttpClient();
    }

    //Create Friend Request
    @Test
    public void friendRequest_BasicRequest_204Listed() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void friendRequest_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user3});
    }

    @Test
    public void friendRequest_BlockedUser_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void friendRequest_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void friendRequest_RequestAlreadyMade_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void friendRequest_AlreadyFriends_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void friendRequest_RequestYourself_403() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user, user, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user, client, HttpStatus.SC_OK, null, new User[]{user});
    }

    @Test
    public void friendRequest_RequestInvalidUser_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    //Cancel Friend Request
    @Test
    public void cancelFriendRequest_BasicRequest_204RequestRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.cancelRequest(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void cancelFriendRequest_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.cancelRequest(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void cancelFriendRequest_CancelInvalidUser_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.cancelRequest(user1, user3, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void cancelFriendRequest_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        user1.setPassword("drowssap");
        SocialUtils.cancelRequest(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void cancelFriendRequest_RequestNotMade_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.cancelRequest(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    //Respond to Friend Request
    @Test
    public void respondToFriendRequest_BasicAccept_204FriendAddedRequestRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void respondToFriendRequest_BasicDeny_204FriendNotAddedRequestRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "deny", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void respondToFriendRequest_BadFormattedAnswer_400() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "blah", client, HttpStatus.SC_BAD_REQUEST);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void respondToFriendRequest_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user3, user1, "accept", client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void respondToFriendRequest_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        user2.setPassword("drowssap");
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_UNAUTHORIZED);
        user2.setPassword("password");
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void respondToFriendRequest_RequestNotMade_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    //Check Incoming Requests
    @Test
    public void checkIncomingRequests_NoIncoming_200BlankArray() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void checkIncomingRequest_OneIncoming_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void checkIncomingRequest_MultipleIncoming_200ArrayWithMultiple() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user3, client, HttpStatus.SC_OK, new User[]{user1, user2}, null);
    }

    @Test
    public void checkIncomingRequest_OneOutgoing_200BlankArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void checkIncomingRequest_InvalidUser_401() throws IOException {
        User user1 = new User(test + "01", "password", "000000000000000");
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    @Test
    public void checkIncomingRequest_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    //Check Outgoing Requests
    @Test
    public void checkOutgoingRequests_NoOutgoing_200BlankArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void checkOutgoingRequest_OneOutgoing_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void checkOutgoingRequest_MultipleOutgoing_200ArrayWithMultiple() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, new User[]{user2, user3}, null);
    }

    @Test
    public void checkOutgoingRequest_OneIncoming_200BlankArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void checkOutgoingRequest_InvalidUser_401() throws IOException {
        User user = new User(test + "01", "password", "000000000000000");
        SocialUtils.checkOutgoing(user, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    @Test
    public void checkOutgoingRequest_InvalidCredentials_401() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        SocialUtils.checkOutgoing(user, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    //Unfriend User
    @Test
    public void unfriendUser_UnfriendFriend_204NoLongerFriends() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
        SocialUtils.unfriendUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void unfriendUser_UnfriendNonfriend_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.unfriendUser(user1, user2, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void unfriendUser_UnfriendBlocked_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.unfriendUser(user1, user2, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void unfriendUser_UnfriendWithRequest_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.unfriendUser(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void unfriendUser_UnfriendInvalidUser_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password");
        SocialUtils.unfriendUser(user1, user2, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void unfriendUser_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
        user1.setUsername(test + "03");
        SocialUtils.unfriendUser(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        user1.setUsername(test + "01");
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void unfriendUser_InvalidPassword_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
        user1.setPassword("drowssap");
        SocialUtils.unfriendUser(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        user1.setPassword("password");
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    //Block User
    @Test
    public void blockUser_BlockNonFriend_204BlockAdded() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void blockUser_BlockFriend_204FriendRemovedBlockAdded() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user1, user2, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void blockUser_BlockWhileRequested_204RequestRemovedBlockAdded() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void blockUser_BlockWhileRequesting_204RequestRemovedBlockAdded() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void blockUser_BlockWhileBlocked_204BlockAdded() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void blockUser_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password", "000000000000000");
        SocialUtils.blockUser(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void blockUser_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        user1.setPassword("password");
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void blockUser_BlockingInvalidUser_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password", "000000000000000");
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkIfBlocked(user1, user2, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void blockUser_BlockingAlreadyBlocked_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void blockUser_BlockingSelf_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIfBlocked(user1, user1, client, HttpStatus.SC_NO_CONTENT);
    }

    //Unblock User
    @Test
    public void unblockUser_Normal_204BlockRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.unblockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void unblockUser_UnblockedWhileBlocked_204OnlyYourBlockRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.unblockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user1, user2, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void unblockUser_UnblockWhileNoBlock_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.unblockUser(user1, user2, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void unblockUser_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.unblockUser(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void unblockUser_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.unblockUser(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        user1.setPassword("password");
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void unblockUser_InvalidUserToUnblock_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.unblockUser(user1, user3, client, HttpStatus.SC_NOT_FOUND);
    }

    //Check If Blocked
    @Test
    public void checkIfBlocked_UserBlocked_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void checkIfBlocked_UserNotBlocked_204() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void checkIfBlocked_UserBlocking_204() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user1, user2, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void checkIfBlocked_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.checkIfBlocked(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void checkIfBlocked_InvalidPassword_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        user2.setPassword("drowssap");
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_UNAUTHORIZED);
    }

    //List Friends
    @Test
    public void listFriends_NoFriends_200EmptyArray() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void listFriends_OneFriend_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void listFriends_MultipleFriends_200ArrayWithMultiple() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user3, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2, user3}, null);
    }

    @Test
    public void listFriends_CreatedByYou_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void listFriends_CreatedByOther_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user1, user2, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void listFriends_IncomingRequest_200EmptyArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void listFriends_OutgoingRequest_200EmptyArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void listFriends_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password");
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_UNAUTHORIZED, null, null);

    }

    @Test
    public void listFriends_InvalidCredentials_401() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        SocialUtils.checkFriends(user, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    //List Blocking Users
    @Test
    public void listBlocking_NoBlocked_200EmptyArray() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkBlocking(user, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void listBlocking_OneBlocked_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkBlocking(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void listBlocking_MultipleBlocked_200ArrayWithMultiple() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkBlocking(user1, client, HttpStatus.SC_OK, new User[]{user2, user3}, null);
    }

    @Test
    public void listBlocking_BeenBlocked_200EmptyArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkBlocking(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void listBlocking_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password");
        SocialUtils.checkBlocking(user2, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    @Test
    public void listBlocking_InvalidCredentials_401() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        SocialUtils.checkBlocking(user, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    //Send a Message
    @Test
    public void sendMessage_NormalMessage_204MessageSent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user2, null, null, null, null, null, client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void sendMessage_EmptyMessage_400NotSent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "", client, HttpStatus.SC_BAD_REQUEST);
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void sendMessage_InvalidRecipient_404NotSent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.sendMessage(user1, user3, "hello world", client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void sendMessage_InvalidUser_401NotSent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.sendMessage(user3, user1, "hello world", client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void sendMessage_InvalidCredentials_401NotSent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.sendMessage(user1, user2, "hello world", client, HttpStatus.SC_UNAUTHORIZED);
        user1.setPassword("password");
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void sendMessage_SentToBlocked_403NotSent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hello world", client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void sendMessage_SentToNonFriend_204Sent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void sendMessage_SentToFriend_204Sent() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void sendMessage_SuperLongMessage_400NotSent() throws IOException {
        String message5001Letters = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur tellus ipsum, pretium in sapien dignissim, lobortis porttitor nulla. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque aliquam quam quis odio finibus sollicitudin. Curabitur eget faucibus lorem. Sed tristique tincidunt congue. Vestibulum scelerisque commodo lectus non feugiat. Curabitur pulvinar sit amet sem ac tempus. Curabitur ut malesuada velit. Aliquam non pretium turpis, et elementum ex. Aenean congue magna quis arcu vehicula rhoncus. Proin lacinia mauris eget venenatis posuere. Nam iaculis et ligula ut pellentesque. Cras finibus tellus eget facilisis varius. Pellentesque malesuada auctor odio ultricies hendrerit. Pellentesque fringilla nibh non tellus dignissim fermentum. Mauris id urna lectus. Pellentesque posuere vitae mauris sit amet vehicula. In magna magna, molestie eget condimentum rhoncus, lacinia nec sapien. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Etiam facilisis tincidunt dolor, id egestas massa dapibus et. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur sit amet cursus nisl. Aenean sodales dolor ligula. Mauris condimentum leo elementum velit pellentesque pellentesque. Nunc nec est odio. Phasellus finibus tellus leo, et aliquam ipsum dictum vitae. Praesent sit amet quam a dolor euismod viverra vel ut ante. Phasellus elementum elementum nisl, sed molestie elit sagittis eu. Ut ante purus, gravida eget sollicitudin non, accumsan in tortor. Nullam ac fringilla nisi, sed semper risus. Cras vitae ornare magna, sed congue odio. Duis nec justo finibus libero semper porttitor. Ut pellentesque metus finibus finibus ultricies. Nam imperdiet pharetra neque a egestas. In malesuada finibus ullamcorper. Phasellus ullamcorper vehicula quam et auctor. Vestibulum ultricies augue a elit malesuada, id faucibus eros tincidunt. Aliquam at tincidunt odio, id maximus ligula. Vivamus congue sem mauris, in commodo erat mattis eget. Etiam id leo fermentum, eleifend urna gravida, vestibulum enim. Integer id tempus nulla. Donec in sollicitudin odio, nec pellentesque tortor. Aliquam dictum lectus vitae ornare elementum. Quisque lobortis mi et tincidunt sodales. Nam ut finibus tortor. Integer ornare, elit eget hendrerit ultricies, massa ligula elementum purus, ut molestie augue odio a ligula. Nullam ornare feugiat tellus, ut laoreet ante mattis iaculis. Mauris purus urna, semper quis viverra et, dapibus sit amet velit. Pellentesque cursus risus diam, et varius libero aliquet in. Curabitur quis sapien sed turpis ultrices ornare sit amet pellentesque nisi. Donec mollis ultrices tellus non tincidunt. Proin pharetra justo ut nisi bibendum dapibus. Mauris placerat sollicitudin ante, eu laoreet ipsum posuere nec. Sed nunc lorem, rutrum sed erat sed, accumsan pulvinar dolor. Morbi ut tempus lacus, sed varius enim. Suspendisse convallis quam vel tempus laoreet. In mattis felis luctus elit scelerisque, vitae accumsan enim gravida. Etiam rhoncus neque ac neque pharetra pellentesque. Duis imperdiet libero eget sagittis maximus. Nulla vehicula efficitur felis in porta. In fermentum erat et consequat tristique. Nam interdum posuere nisl, eu tempor urna fermentum sit amet. Sed pretium dictum consequat. Etiam justo enim, viverra a porttitor eu, semper a nunc. Sed euismod, lacus at ullamcorper venenatis, felis purus fermentum lorem, ac feugiat velit metus id nisi. Integer aliquet leo ac turpis dictum rhoncus. Vestibulum varius dui nec mi imperdiet tincidunt. Donec porttitor faucibus lobortis. Quisque pretium nisl et mauris convallis, quis blandit nunc porttitor. Sed auctor ultrices mi pharetra semper. Ut imperdiet lacus lorem, quis tincidunt nunc ultricies scelerisque. Donec ligula felis, elementum non sodales iaculis, venenatis quis quam. Duis malesuada nibh dictum facilisis facilisis. Curabitur fermentum mattis sapien, eget laoreet nisi finibus vel. Curabitur felis urna, sagittis et dictum ac, volutpat nec nisi. Aliquam efficitur elementum efficitur. Aliquam dolor nulla, malesuada a erat nec, feugiat ultrices magna.;Sed vestibulum nec diam non tristique. Nulla sed ante non massa convallis egestas non eget urna. Integer molestie suscipit ex, mattis scelerisque mi tincidunt quis. Quisque id porta purus. Maecenas a facilisis ligula, eu feugiat tellus. Donec leo arcu, lobortis id laoreet in, sagittis vitae neque. Integer at vehicula lectus, ac dictum nunc. Donec molestie sit amet justo quis placerat. Proin quis gravida libero. Aenean tincidunt mi ligula, eget pharetra arcu sagittis eget. Maecenas id ipsum ut leo tempus tincidunt. Vivamus maximus dolor eros, eu dapibus urna aliquam vel. Aenean pretium fringilla risus sed aliquam. Phasellus blandit metus sit amet maximus dapibus. Donec id dolor eleifend, euismod tortor a, venenatis ipsum. Nulla rhoncus lorem laoreet tempor consectetur. Quisque mattis auctor euismod. Praesent quis dictum urna. Aened.";
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, message5001Letters, client, HttpStatus.SC_BAD_REQUEST);
        SocialUtils.searchAllMessages(user2,client, HttpStatus.SC_OK, 0);
    }

    //Search Messages
    @Test
    public void searchMessages_NoParameters_200AllMessages() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchAllMessages(user1, client, HttpStatus.SC_OK, 2);
    }

    @Test
    public void searchMessages_AllParameters_200FilteredMessages() throws IOException, InterruptedException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        Date start = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, null, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world redux", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hi world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user3, user1, "hello world tridux", client, HttpStatus.SC_NO_CONTENT);
        Date end = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, user2.getUserId(), start, end, "NOT_READ", "THEM", client, HttpStatus.SC_OK, 1);
    }
    @Test
    public void searchMessages_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchAllMessages(user3, client, HttpStatus.SC_UNAUTHORIZED, 0);
    }

    @Test
    public void searchMessages_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.searchAllMessages(user1, client, HttpStatus.SC_UNAUTHORIZED, 2);
    }

    @Test
    public void searchMessages_OneSender_200MessagesFromSender() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user3, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, user3.getUserId(), null, null, null, null, client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void searchMessages_MultipleSenders_200MessagesFromSenders() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        User user4 = AccountUtils.addUser(test, "04", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user3, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user4, user1, "hello world the last", client, HttpStatus.SC_NO_CONTENT);
        String users = user3.getUserId() + "," + user4.getUserId();
        SocialUtils.searchMessages(user1, users, null, null, null, null, client, HttpStatus.SC_OK, 2);
    }

    @Test
    public void searchMessages_NormalStart_200MessagesAfterStart() throws IOException, InterruptedException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        Date now = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, now, null, null, null, client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void searchMessages_StartAtCurrent_200NoMessages() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        Date now = new Date();
        SocialUtils.searchMessages(user1, null, now, null, null, null, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void searchMessages_StartAtFuture_400() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        SocialUtils.searchMessages(user1, null, calendar.getTime(), null, null, null, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void searchMessages_EndTimeNormal_200MessagesBeforeEnd() throws IOException, InterruptedException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        Date now = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, now, null, null, client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void searchMessages_EndTimeBeforeAll_200NoMessages() throws IOException, InterruptedException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        Date now = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, now, null, null, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void searchMessages_StartAndEnd_200MessagesBetweenStartAndEnd() throws IOException, InterruptedException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        Date start = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        Date end = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world last time", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, start, end, null, null, client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void searchMessages_StartAfterEnd_400() throws IOException, InterruptedException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        Date start = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        Date end = new Date();
        Thread.sleep(2000);
        SocialUtils.sendMessage(user2, user1, "hello world last time", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, end, start, null, null, client, HttpStatus.SC_BAD_REQUEST, 1);
    }

    @Test
    public void searchMessages_OnlyUnread_200OnlyUnreadMessages() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, null, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, null, "NOT_READ", null, client, HttpStatus.SC_OK, 2);
    }

    @Test
    public void searchMessages_OnlyRead_200OnlyReadMessages() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, null, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, null, "READ", null, client, HttpStatus.SC_OK, 2);
    }

    @Test
    public void searchMessages_ReadBadFormat_400() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, null, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, null, "NOTHING", null, client, HttpStatus.SC_BAD_REQUEST, 0);
    }

    @Test
    public void searchMessages_InvalidSender_200NoMessages() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, "000000000000000", null, null, null, null, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void searchMessages_InvalidAndValidSender_200MessagesFromValidSenders() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user3, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, "000000000000000," + user3.getUserId(), null, null, null, null, client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void searchMessages_SenderNoMessages_200NoMessages() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, user3.getUserId(), null, null, null, null, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void searchMessages_FromMe_200MessagesYouSentOnly() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hello world last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, null, null, "ME", client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void searchMessages_FromOther_200MessagesYouRecievedOnly() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hello world last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, null, null, "THEM", client, HttpStatus.SC_OK, 2);
    }

    @Test
    public void searchMessages_InvalidFrom_400() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user1, user2, "hello world last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.searchMessages(user1, null, null, null, null, "WHO_KNOWS", client, HttpStatus.SC_BAD_REQUEST, 0);
    }

    //Mark Messages Read
    @Test
    public void markRead_NoParameters_204AllMessagesRead() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, null, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 0);
    }

    @Test
    public void markRead_Ids_204SpecifiedMessagesRead() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        JSONArray array = SocialUtils.searchAllMessages(user1, client, HttpStatus.SC_OK, 2);
        SocialUtils.markMessagesRead(user1, new String[]{array.getJSONObject(0).getString("id")}, null, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 1);
    }

    @Test
    public void markRead_Senders_204SpecifiedMessagesRead() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user3, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, new User[]{user2}, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 1);
    }

    @Test
    public void markRead_IdsAndSenders_204SpecifiedMessagesRead() throws IOException, InterruptedException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        JSONArray array = SocialUtils.searchAllMessages(user1, client, HttpStatus.SC_OK, 1);
        SocialUtils.sendMessage(user3, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world the last", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, new String[]{array.getJSONObject(0).getString("id")}, new User[]{user3}, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 1);
    }

    @Test
    public void markRead_InvalidIds_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, new String[]{"00000000000000000000"}, null, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 2);
    }

    @Test
    public void markRead_InvalidSenders_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, new User[]{user3}, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 2);
    }

    @Test
    public void markRead_InvalidAndValidIds_404AllValidMarked() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        JSONArray array = SocialUtils.searchAllMessages(user1, client, HttpStatus.SC_OK, 2);
        SocialUtils.markMessagesRead(user1, new String[]{"00000000000000000000", array.getJSONObject(0).getString("id")}, null, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 1);
    }

    @Test
    public void markRead_InvalidAndValidSenders_404AllValidMarked() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, new User[]{user2, user3}, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 0);
    }

    @Test
    public void markRead_IdNotYourMessage_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        JSONArray array = SocialUtils.searchAllMessages(user1, client, HttpStatus.SC_OK, 2);
        SocialUtils.markMessagesRead(user3, new String[]{array.getJSONObject(0).getString("id")}, null, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 2);
    }

    @Test
    public void markRead_IdYouAreSender_403() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        JSONArray array = SocialUtils.searchAllMessages(user1, client, HttpStatus.SC_OK, 2);
        SocialUtils.markMessagesRead(user2, new String[]{array.getJSONObject(0).getString("id")}, null, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 2);
    }

    @Test
    public void markRead_SenderNoMessages_204NothingMarked() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, new User[]{user3}, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 2);
    }

    @Test
    public void markRead_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user3, null, null, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void markRead_InvalidPassword_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.markMessagesRead(user1, null, null, client, HttpStatus.SC_UNAUTHORIZED);
        user1.setPassword("password");
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 2);
    }

    //Get Notifications
    @Test
    public void getNotifications_NoRequests_200RequestsFieldZero() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user, client, HttpStatus.SC_OK, 0, 0);
    }

    @Test
    public void getNotifications_NonZeroRequests_200RequestsFieldMoreThanZero() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 1, 0);
    }

    @Test
    public void getNotifications_NoMessages_200MessagesFieldZero() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user, client, HttpStatus.SC_OK, 0, 0);
    }

    @Test
    public void getNotifications_NonZeroMessages_200MessagesFieldMoreThanZero() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 1);
    }

    @Test
    public void getNotifications_NonZeroMessagesSomeRead_200MessagesFieldNonZeroNotIncludingRead() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.markMessagesRead(user1, null, null, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.sendMessage(user2, user1, "hello world again", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkSocialNotifications(user1, client, HttpStatus.SC_OK, 0, 1);
    }

    @Test
    public void getNotifications_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password");
        SocialUtils.checkSocialNotifications(user2, client, HttpStatus.SC_UNAUTHORIZED, 0, 0);
    }

    @Test
    public void getNotifications_InvalidPassword_401() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        SocialUtils.checkSocialNotifications(user, client, HttpStatus.SC_UNAUTHORIZED, 0, 0);
    }
}
