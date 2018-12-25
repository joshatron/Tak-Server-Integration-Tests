package io.joshatron.tak.server;

import io.joshatron.tak.server.utils.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.junit.Test;

import java.io.IOException;

//Suite: B
//Current final test: 130
public class SocialTest {

    private String suite;
    private HttpClient client;

    public SocialTest() {
        client = HttpUtils.createHttpClient();
        suite = "B" + RandomUtils.generateSuite(10);
        System.out.println("Suite: " + suite);
    }

    //Create Friend Request
    @Test
    public void friendRequest_BasicRequest_204Listed() throws IOException {
        String test = "001";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void friendRequest_InvalidUser_401() throws IOException {
        String test = "002";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(suite + test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user3});
    }

    @Test
    public void friendRequest_BlockedUser_403() throws IOException {
        String test = "003";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void friendRequest_InvalidCredentials_401() throws IOException {
        String test = "004";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void friendRequest_RequestAlreadyMade_403() throws IOException {
        String test = "005";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void friendRequest_AlreadyFriends_403() throws IOException {
        String test = "006";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void friendRequest_RequestYourself_403() throws IOException {
        String test = "007";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user, user, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user, client, HttpStatus.SC_OK, null, new User[]{user});
    }

    @Test
    public void friendRequest_RequestInvalidUser_404() throws IOException {
        String test = "088";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(suite + test + "02", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    //Cancel Friend Request
    @Test
    public void cancelFriendRequest_BasicRequest_204RequestRemoved() throws IOException {
        String test = "008";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.cancelRequest(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void cancelFriendRequest_InvalidUser_401() throws IOException {
        String test = "009";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(suite + test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.cancelRequest(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void cancelFriendRequest_CancelInvalidUser_404() throws IOException {
        String test = "089";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(suite + test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.cancelRequest(user1, user3, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void cancelFriendRequest_InvalidCredentials_401() throws IOException {
        String test = "010";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        user1.setPassword("drowssap");
        SocialUtils.cancelRequest(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void cancelFriendRequest_RequestNotMade_403() throws IOException {
        String test = "011";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.cancelRequest(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    //Respond to Friend Request
    @Test
    public void respondToFriendRequest_BasicAccept_204FriendAddedRequestRemoved() throws IOException {
        String test = "012";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void respondToFriendRequest_BasicDeny_204FriendNotAddedRequestRemoved() throws IOException {
        String test = "013";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "deny", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void respondToFriendRequest_BadFormattedAnswer_400() throws IOException {
        String test = "087";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user2, user1, "blah", client, HttpStatus.SC_BAD_REQUEST);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void respondToFriendRequest_InvalidUser_401() throws IOException {
        String test = "014";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(suite + test + "03", "password", "000000000000000");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        SocialUtils.respondToRequest(user3, user1, "accept", client, HttpStatus.SC_UNAUTHORIZED);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void respondToFriendRequest_InvalidCredentials_401() throws IOException {
        String test = "015";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
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
        String test = "016";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkFriends(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    //Check Incoming Requests
    @Test
    public void checkIncomingRequests_NoIncoming_200BlankArray() throws IOException {
        String test = "017";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void checkIncomingRequest_OneIncoming_200ArrayWithOne() throws IOException {
        String test = "018";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void checkIncomingRequest_MultipleIncoming_200ArrayWithMultiple() throws IOException {
        String test = "019";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(suite, test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user3, client, HttpStatus.SC_OK, new User[]{user1, user2}, null);
    }

    @Test
    public void checkIncomingRequest_OneOutgoing_200BlankArray() throws IOException {
        String test = "020";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void checkIncomingRequest_InvalidUser_401() throws IOException {
        String test = "021";
        User user1 = new User(suite + test + "01", "password", "000000000000000");
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    @Test
    public void checkIncomingRequest_InvalidCredentials_401() throws IOException {
        String test = "022";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    //Check Outgoing Requests
    @Test
    public void checkOutgoingRequests_NoOutgoing_200BlankArray() throws IOException {
        String test = "023";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void checkOutgoingRequest_OneOutgoing_200ArrayWithOne() throws IOException {
        String test = "024";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void checkOutgoingRequest_MultipleOutgoing_200ArrayWithMultiple() throws IOException {
        String test = "025";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(suite, test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, new User[]{user2, user3}, null);
    }

    @Test
    public void checkOutgoingRequest_OneIncoming_200BlankArray() throws IOException {
        String test = "026";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkOutgoing(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void checkOutgoingRequest_InvalidUser_401() throws IOException {
        String test = "027";
        User user = new User(suite + test + "01", "password", "000000000000000");
        SocialUtils.checkOutgoing(user, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    @Test
    public void checkOutgoingRequest_InvalidCredentials_401() throws IOException {
        String test = "028";
        User user = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        SocialUtils.checkOutgoing(user, client, HttpStatus.SC_UNAUTHORIZED, null, null);
    }

    //Unfriend User
    @Test
    public void unfriendUser_UnfriendFriend_204NoLongerFriends() throws IOException {
        String test = "097";
    }

    @Test
    public void unfriendUser_UnfriendNonfriend_404() throws IOException {
        String test = "098";
    }

    @Test
    public void unfriendUser_UnfriendBlocked_403() throws IOException {
        String test = "099";
    }

    @Test
    public void unfriendUser_UnfriendWithRequest_404() throws IOException {
        String test = "100";
    }

    @Test
    public void unfriendUser_UnfriendInvalidUser_404() throws IOException {
        String test = "101";
    }

    @Test
    public void unfriendUser_InvalidUser_401() throws IOException {
        String test = "102";
    }

    @Test
    public void unfriendUser_InvalidPassword_401() throws IOException {
        String test = "103";
    }

    //Block User
    @Test
    public void blockUser_BlockNonFriend_204BlockAdded() throws IOException {
        String test = "029";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void blockUser_BlockFriend_204FriendRemovedBlockAdded() throws IOException {
        String test = "030";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user1, user2, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkFriends(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void blockUser_BlockWhileRequested_204RequestRemovedBlockAdded() throws IOException {
        String test = "031";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIncoming(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void blockUser_BlockWhileRequesting_204RequestRemovedBlockAdded() throws IOException {
        String test = "032";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void blockUser_BlockWhileBlocked_204BlockAdded() throws IOException {
        String test = "033";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user1, user2, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void blockUser_InvalidUser_401() throws IOException {
        String test = "034";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(suite + test + "03", "password", "000000000000000");
        SocialUtils.blockUser(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void blockUser_InvalidCredentials_401() throws IOException {
        String test = "035";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        user1.setPassword("password");
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void blockUser_BlockingInvalidUser_404() throws IOException {
        String test = "036";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(suite + test + "02", "password", "000000000000000");
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        SocialUtils.checkIfBlocked(user1, user2, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void blockUser_BlockingAlreadyBlocked_403() throws IOException {
        String test = "037";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(suite, test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.checkIfBlocked(user2, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void blockUser_BlockingSelf_403() throws IOException {
        String test = "038";
        User user1 = AccountUtils.addUser(suite, test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user1, client, HttpStatus.SC_FORBIDDEN);
        SocialUtils.checkIfBlocked(user1, user1, client, HttpStatus.SC_NO_CONTENT);
    }

    //Unblock User
    @Test
    public void unblockUser_Normal_204BlockRemoved() {
        String test = "039";
    }

    @Test
    public void unblockUser_UnblockedWhileBlocked_204OnlyYourBlockRemoved() {
        String test = "040";
    }

    @Test
    public void unblockUser_UnblockWhileNoBlock_403() {
        String test = "041";
    }

    @Test
    public void unblockUser_InvalidUser_403() {
        String test = "042";
    }

    @Test
    public void unblockUser_InvalidCredentials_403() {
        String test = "043";
    }

    @Test
    public void unblockUser_InvalidUserToUnblock_403() {
        String test = "044";
    }

    //Check If Blocked
    @Test
    public void checkIfBlocked_UserBlocked_403() {
        String test = "104";
    }

    @Test
    public void checkIfBlocked_UserNotBlocked_204() {
        String test = "105";
    }

    @Test
    public void checkIfBlocked_UserBlocking_204() {
        String test = "106";
    }

    @Test
    public void checkIfBlocked_InvalidUser_401() {
        String test = "107";
    }

    @Test
    public void checkIfBlocked_InvalidPassword_401() {
        String test = "108";
    }

    //List Friends
    @Test
    public void listFriends_NoFriends_200EmptyArray() {
        String test = "045";
    }

    @Test
    public void listFriends_OneFriend_200ArrayWithOne() {
        String test = "046";
    }

    @Test
    public void listFriends_MultipleFriends_200ArrayWithMultiple() {
        String test = "047";
    }

    @Test
    public void listFriends_CreatedByYou_200ArrayWithOne() {
        String test = "048";
    }

    @Test
    public void listFriends_CreatedByOther_200ArrayWithOne() {
        String test = "049";
    }

    @Test
    public void listFriends_IncomingRequest_200EmptyArray() {
        String test = "050";
    }

    @Test
    public void listFriends_OutgoingRequest_200EmptyArray() {
        String test = "051";
    }

    @Test
    public void listFriends_InvalidUser_403() {
        String test = "052";
    }

    @Test
    public void listFriends_InvalidCredentials_403() {
        String test = "053";
    }

    //List Blocking Users
    @Test
    public void listBlocking_NoBlocked_200EmptyArray() {
        String test = "054";
    }

    @Test
    public void listBlocking_OneBlocked_200ArrayWithOne() {
        String test = "055";
    }

    @Test
    public void listBlocking_MultipleBlocked_200ArrayWithMultiple() {
        String test = "056";
    }

    @Test
    public void listBlocking_BeenBlocked_200EmptyArray() {
        String test = "057";
    }

    @Test
    public void listBlocking_InvalidUser_403() {
        String test = "058";
    }

    @Test
    public void listBlocking_InvalidCredentials_403() {
        String test = "059";
    }

    //Send a Message
    @Test
    public void sendMessage_NormalMessage_204MessageSent() {
        String test = "060";
    }

    @Test
    public void sendMessage_EmptyMessage_403NotSent() {
        String test = "061";
    }

    @Test
    public void sendMessage_InvalidRecipient_403NotSent() {
        String test = "062";
    }

    @Test
    public void sendMessage_InvalidUser_403NotSent() {
        String test = "063";
    }

    @Test
    public void sendMessage_InvalidCredentials_403NotSent() {
        String test = "064";
    }

    @Test
    public void sendMessage_SentToBlocked_403NotSent() {
        String test = "065";
    }

    @Test
    public void sendMessage_SentToNonFriend_204Sent() {
        String test = "066";
    }

    @Test
    public void sendMessage_SentToFriend_204Sent() {
        String test = "067";
    }

    @Test
    public void sendMessage_SuperLongMessage_403NotSent() {
        String test = "068";
    }

    //Read Messages
    @Test
    public void readMessages_NoParameters_200AllMessages() {
        String test = "069";
    }

    @Test
    public void readMessages_AllParameters_200FilteredMessages() {
        String test = "130";
    }
    @Test
    public void readMessages_InvalidUser_403() {
        String test = "070";
    }

    @Test
    public void readMessages_InvalidCredentials_403() {
        String test = "071";
    }

    @Test
    public void readMessages_OneSender_200MessagesFromSender() {
        String test = "072";
    }

    @Test
    public void readMessages_MultipleSenders_200MessagesFromSenders() {
        String test = "073";
    }

    @Test
    public void readMessages_NormalStart_200MessagesAfterStart() {
        String test = "074";
    }

    @Test
    public void readMessages_StartAtCurrent_200NoMessages() {
        String test = "075";
    }

    @Test
    public void readMessages_StartAtFuture_403() {
        String test = "076";
    }

    @Test
    public void readMessages_EndTimeNormal_200MessagesBeforeEnd() {
        String test = "121";
    }

    @Test
    public void readMessages_EndTimeBeforeAll_200NoMessages() {
        String test = "122";
    }

    @Test
    public void readMessages_StartAndEnd_200MessagesBetweenStartAndEnd() {
        String test = "123";
    }

    @Test
    public void readMessages_StartAfterEnd_400() {
        String test = "124";
    }

    @Test
    public void readMessages_OnlyUnread_200OnlyUnreadMessages() {
        String test = "077";
    }

    @Test
    public void readMessages_OnlyRead_200OnlyReadMessages() {
        String test = "078";
    }

    @Test
    public void readMessages_ReadBadFormat_400() {
        String test = "079";
    }

    @Test
    public void readMessages_StartBadFormat_400() {
        String test = "080";
    }

    @Test
    public void readMessages_EndBadFormat_400() {
        String test = "125";
    }

    @Test
    public void readMessages_InvalidSender_404() {
        String test = "081";
    }

    @Test
    public void readMessages_InvalidAndValidSender_404MessagesFromValidSenders() {
        String test = "126";
    }

    @Test
    public void readMessages_SenderNoMessages_200NoMessages() {
        String test = "082";
    }

    @Test
    public void readMessages_FromMe_200MessagesYouSentOnly() {
        String test = "127";
    }

    @Test
    public void readMessages_FromOther_200MessagesYouRecievedOnly() {
        String test = "128";
    }

    @Test
    public void readMessages_InvalidFrom_400() {
        String test = "129";
    }

    //Mark Messages Read
    @Test
    public void markRead_NoParameters_204AllMessagesRead() {
        String test = "109";
    }

    @Test
    public void markRead_Ids_204SpecifiedMessagesRead() {
        String test = "110";
    }

    @Test
    public void markRead_StartTime_204SpecifiedMessagesRead() {
        String test = "111";
    }

    @Test
    public void markRead_IdsAndStartTime_204SpecifiedMessagesRead() {
        String test = "112";
    }

    @Test
    public void markRead_InvalidIds_404() {
        String test = "113";
    }

    @Test
    public void markRead_InvalidAndValidIds_404AllValidMarked() {
        String test = "114";
    }

    @Test
    public void markRead_InvalidAndValidTime_404AllValidMarked() {
        String test = "115";
    }

    @Test
    public void markRead_StartTimeAfterNow_404() {
        String test = "116";
    }

    @Test
    public void markRead_IdNotYourMessage_403() {
        String test = "117";
    }

    @Test
    public void markRead_IdYouAreSender_403() {
        String test = "118";
    }

    @Test
    public void markRead_InvalidUser_401() {
        String test = "119";
    }

    @Test
    public void markRead_InvalidPassword_401() {
        String test = "120";
    }

    //Get Notifications
    @Test
    public void getNotifications_NoRequests_200RequestsFieldZero() {
        String test = "090";
    }

    @Test
    public void getNotifications_NonZeroRequests_200RequestsFieldMoreThanZero() {
        String test = "091";
    }

    @Test
    public void getNotifications_NoMessages_200MessagesFieldMoreThanZero() {
        String test = "092";
    }

    @Test
    public void getNotifications_NonZeroMessages_200MessagesFieldMoreThanZero() {
        String test = "093";
    }

    @Test
    public void getNotifications_NonZeroMessagesSomeRead_200MessagesFieldNonZeroNotIncludingRead() {
        String test = "094";
    }

    @Test
    public void getNotifications_InvalidUser_401() {
        String test = "095";
    }

    @Test
    public void getNotifications_InvalidPassword_401() {
        String test = "096";
    }
}
