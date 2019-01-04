package io.joshatron.tak.server.logic;

import io.joshatron.tak.server.logic.utils.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

//Suite: C
public class GameTest {

    private HttpClient client;
    private String test;

    public GameTest() throws IOException {
        client = HttpUtils.createHttpClient();
    }

    @Before
    public void initializeTest() {
        test = "C" + RandomUtils.generateTest(10);
    }

    //Request a Game
    @Test
    public void requestGame_RequestFriend_204RequestMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void requestGame_RequestNonFriend_403RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_FORBIDDEN);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void requestGame_RequestPendingFriend_403RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_FORBIDDEN);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void requestGame_RequestBlocked_403RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user2, user1, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_FORBIDDEN);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void requestGame_RequestNonexistent_404RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        GameUtils.requestGame(user1, user3, 5, "WHITE", "WHITE", client, HttpStatus.SC_NOT_FOUND);
        GameUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, null, new User[]{user3});
    }

    @Test
    public void requestGame_RequestWithExistingRequest_403RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_FORBIDDEN);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void requestGame_RequestYourself_403RequestNotMade() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user, user, 6, "BLACK", "BLACK", client, HttpStatus.SC_FORBIDDEN);
        GameUtils.checkOutgoing(user, client, HttpStatus.SC_OK, null, new User[]{user});
    }

    @Test
    public void requestGame_RequestExistingGame_403RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_FORBIDDEN);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void requestGame_InvalidUser_401RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        GameUtils.requestGame(user3, user1, 5, "WHITE", "WHITE", client, HttpStatus.SC_UNAUTHORIZED);
        GameUtils.checkIncoming(user1, client, HttpStatus.SC_OK, null, new User[]{user3});
    }

    @Test
    public void requestGame_InvalidCredentials_401RequestNotMade() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_UNAUTHORIZED);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    //Cancel a Game Request
    @Test
    public void cancelGameRequest_BasicRequest_204RequestRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.deleteGameRequest(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void cancelGameRequest_CancelInvalidUser_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.deleteGameRequest(user1, user3, client, HttpStatus.SC_NOT_FOUND);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void cancelGameRequest_RequestNotMade_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.deleteGameRequest(user1, user2, client, HttpStatus.SC_NOT_FOUND);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void cancelGameRequest_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.deleteGameRequest(user3, user2, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void cancelGameRequest_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        GameUtils.deleteGameRequest(user1, user2, client, HttpStatus.SC_UNAUTHORIZED);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    //Respond to Game Request
    @Test
    public void respondToGameRequest_RespondAccept_204GameStartedRequestRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 1);
    }

    @Test
    public void respondToGameRequest_RespondDeny_204GameNotStartedRequestRemoved() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "DENY", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void respondToGameRequest_RespondBadFormatting_400GameNotStartedRequestStillThere() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "INVALID", client, HttpStatus.SC_BAD_REQUEST);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void respondToGameRequest_RespondNoRequest_404NoGameStarted() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NOT_FOUND);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void respondToGameRequest_InvalidUser_401GameNotStartedRequestStillThere() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user3, user1, "ACCEPT", client, HttpStatus.SC_UNAUTHORIZED);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
    }

    @Test
    public void respondToGameRequest_InvalidCredentials_401GameNotStartedRequestStillThere() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        user2.setPassword("drowssap");
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_UNAUTHORIZED);
        user2.setPassword("password");
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
    }

    //Check Incoming Game Request
    @Test
    public void checkIncomingGames_NoRequests_200EmptyArray() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void checkIncomingGames_OneRequest_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1}, null);
    }

    @Test
    public void checkIncomingGames_MultipleRequests_200ArrayWithMultiple() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user3, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user3, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user3, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_OK, new User[]{user1, user3}, null);
    }

    @Test
    public void checkIncomingGames_OneOutgoing_200EmptyArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user1, client, HttpStatus.SC_OK, null, new User[]{user2});
    }

    @Test
    public void checkIncomingGames_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkIncoming(user3, client, HttpStatus.SC_UNAUTHORIZED, null, new User[]{user2, user1});
    }

    @Test
    public void checkIncomingGames_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        user2.setPassword("drowssap");
        GameUtils.checkIncoming(user2, client, HttpStatus.SC_UNAUTHORIZED, null, new User[]{user1});
    }

    //Check Outgoing Game Request
    @Test
    public void checkOutgoingGames_NoRequests_200EmptyArray() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkOutgoing(user, client, HttpStatus.SC_OK, null, null);
    }

    @Test
    public void checkOutgoingGames_200ArrayWithOne() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, new User[]{user2}, null);
    }

    @Test
    public void checkOutgoingGames_200ArrayWithMultiple() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user3, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user3, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user3, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkOutgoing(user1, client, HttpStatus.SC_OK, new User[]{user2, user3}, null);
    }

    @Test
    public void checkOutgoingGames_OneIncoming_200EmptyArray() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkOutgoing(user2, client, HttpStatus.SC_OK, null, new User[]{user1});
    }

    @Test
    public void checkOutgoingGames_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.checkOutgoing(user3, client, HttpStatus.SC_UNAUTHORIZED, null, new User[]{user2, user1});
    }

    @Test
    public void checkOutgoingGames_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        GameUtils.checkOutgoing(user1, client, HttpStatus.SC_UNAUTHORIZED, null, new User[]{user2});
    }

    //Request a Random Game
    @Test
    public void requestRandomGame_OneRequest_204NoGameCreated() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user, client, HttpStatus.SC_OK, 0);
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    @Test
    public void requestRandomGame_TwoMatchingRequests_204GameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 1);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
    }

    @Test
    public void requestRandomGame_TwoMismatchingRequests_204NoGameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 6, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 0);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
    }

    @Test
    public void requestRandomGame_TwoMatchingAlreadyInGame_204NoGameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 1);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
    }

    @Test
    public void requestRandomGame_TwoFriendsMatching_204GameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 1);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
    }

    @Test
    public void requestRandomGame_TwoRandomMatching_204GameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 1);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
    }

    @Test
    public void requestRandomGame_TwoMatchingBlocked_204NoGameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 0);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
    }

    @Test
    public void requestRandomGame_TwoMismatchThenThirdMatch_204OneGameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 3, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user3, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1);
        GameUtils.searchAllGames(user2, client, HttpStatus.SC_OK, 0);
        GameUtils.searchAllGames(user3, client, HttpStatus.SC_OK, 1);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
        GameUtils.rawDeleteRandomGameRequest(user3, client);
    }

    @Test
    public void requestRandomGame_TwoBlockedMatchedThenNonBlockMatch_204OneGameCreated() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.blockUser(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user2, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user3, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.searchAllGames(user3, client, HttpStatus.SC_OK, 1);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
        GameUtils.rawDeleteRandomGameRequest(user2, client);
        GameUtils.rawDeleteRandomGameRequest(user3, client);
    }

    @Test
    public void requestRandomGame_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password");
        GameUtils.requestRandomGame(user2, 3, client, HttpStatus.SC_UNAUTHORIZED);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
    }

    @Test
    public void requestRandomGame_InvalidCredentials_401() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        GameUtils.requestRandomGame(user, 5, client, HttpStatus.SC_UNAUTHORIZED);
        user.setPassword("password");
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    @Test
    public void requestRandomGame_GameSizeIllegalNumber_400() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user, 7, client, HttpStatus.SC_BAD_REQUEST);
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    //Cancel a Random Game Request
    @Test
    public void cancelRandomRequest_BasicRequest_204RequestRemoved() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user, 3, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.deleteRandomGameRequest(user, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    @Test
    public void cancelRandomRequest_RequestNotMade_404() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.deleteRandomGameRequest(user, client, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void cancelRandomRequest_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password");
        GameUtils.deleteRandomGameRequest(user2, client, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void cancelRandomRequest_InvalidCredentials_401() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user, 3, client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        GameUtils.deleteRandomGameRequest(user, client, HttpStatus.SC_UNAUTHORIZED);
        user.setPassword("password");
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    //Check Random Game Request
    @Test
    public void checkRandomRequest_BasicRequest_200Size() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.getRandomRequestSize(user, client, HttpStatus.SC_OK, 5);
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    @Test
    public void checkRandomRequest_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = new User(test + "02", "password");
        GameUtils.requestRandomGame(user1, 5, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.getRandomRequestSize(user2, client, HttpStatus.SC_UNAUTHORIZED, 0);
        GameUtils.rawDeleteRandomGameRequest(user1, client);
    }

    @Test
    public void checkRandomRequest_InvalidCredentials_401() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestRandomGame(user, 5, client, HttpStatus.SC_NO_CONTENT);
        user.setPassword("drowssap");
        GameUtils.getRandomRequestSize(user, client, HttpStatus.SC_UNAUTHORIZED, 0);
        user.setPassword("password");
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    @Test
    public void checkRandomRequest_RequestNotMade_404() throws IOException {
        User user = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.getRandomRequestSize(user, client, HttpStatus.SC_NOT_FOUND, 0);
        GameUtils.rawDeleteRandomGameRequest(user, client);
    }

    //List Games
    //TODO: review
    @Test
    public void listGames_NoGames_200EmptyArray() throws IOException {
    }

    @Test
    public void listGames_OneGame_200ArrayWithOne() throws IOException {
    }

    @Test
    public void listGames_MultipleGames_200ArrayWithMultiple() throws IOException {
    }

    @Test
    public void listGames_GameInProgress_200EmptyArray() throws IOException {
    }

    @Test
    public void listGames_OneOpponent_200GamesFromOpponent() throws IOException {
    }

    @Test
    public void listGames_MultipleOpponents_200GamesFromMultipleOpponents() throws IOException {
    }

    @Test
    public void listGames_StartInPast_200GamesFromPastOn() throws IOException {
    }

    @Test
    public void listGames_StartInCurrent_200EmptyGames() throws IOException {
    }

    @Test
    public void listGames_StartInFuture_403() throws IOException {
    }

    @Test
    public void listGames_OneSize_200GamesWithSize() throws IOException {
    }

    @Test
    public void listGames_SizeBadNumber_403() throws IOException {
    }

    @Test
    public void listGames_InvalidUser_403() throws IOException {
    }

    @Test
    public void listGames_InvalidCredentials_403() throws IOException {
    }

    @Test
    public void listGames_CompleteGame_200EmptyArray() throws IOException {
    }

    @Test
    public void listGames_Pending_200OnlyPending() throws IOException {
    }

    @Test
    public void listGames_NotPending_200OnlyNotPending() throws IOException {
    }

    @Test
    public void listGames_InvalidPending_403() throws IOException {
    }

    //Get Info on a Game
    @Test
    public void getGame_ValidGame_200GameInfo() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        String gameId = GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1).getJSONObject(0).getString("gameId");
        String turn = "ps a1";
        GameUtils.playTurn(user1, gameId, turn, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.getGame(user1, gameId, client, HttpStatus.SC_OK, user1, user2, new String[]{turn});
    }

    @Test
    public void getGame_NotYourGame_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = AccountUtils.addUser(test, "03", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        String gameId = GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1).getJSONObject(0).getString("gameId");
        GameUtils.getGame(user3, gameId, client, HttpStatus.SC_NOT_FOUND, null, null, null);
    }

    @Test
    public void getGame_NotRealGame_404() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.getGame(user1, "0000000000000000000000000", client, HttpStatus.SC_NOT_FOUND, null, null, null);
    }

    @Test
    public void getGame_InvalidUser_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        User user3 = new User(test + "03", "password");
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        String gameId = GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1).getJSONObject(0).getString("gameId");
        String turn = "ps a1";
        GameUtils.playTurn(user1, gameId, turn, client, HttpStatus.SC_NO_CONTENT);
        GameUtils.getGame(user3, gameId, client, HttpStatus.SC_UNAUTHORIZED, null, null, null);
    }

    @Test
    public void getGame_InvalidCredentials_401() throws IOException {
        User user1 = AccountUtils.addUser(test, "01", "password", client, HttpStatus.SC_NO_CONTENT);
        User user2 = AccountUtils.addUser(test, "02", "password", client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.requestFriend(user1, user2, client, HttpStatus.SC_NO_CONTENT);
        SocialUtils.respondToRequest(user2, user1, "accept", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.requestGame(user1, user2, 5, "WHITE", "WHITE", client, HttpStatus.SC_NO_CONTENT);
        GameUtils.respondToGameRequest(user2, user1, "ACCEPT", client, HttpStatus.SC_NO_CONTENT);
        String gameId = GameUtils.searchAllGames(user1, client, HttpStatus.SC_OK, 1).getJSONObject(0).getString("gameId");
        String turn = "ps a1";
        GameUtils.playTurn(user1, gameId, turn, client, HttpStatus.SC_NO_CONTENT);
        user1.setPassword("drowssap");
        GameUtils.getGame(user1, gameId, client, HttpStatus.SC_UNAUTHORIZED, null, null, null);
    }

    //Get Possible Next Turns For Game
    @Test
    public void getPossibleTurns_YourTurn_200PossibleTurns() throws IOException {
    }

    @Test
    public void getPossibleTurns_FinishedGame_200Empty() throws IOException {
    }

    @Test
    public void getPossibleTurns_TheirTurn_403() throws IOException {
    }

    @Test
    public void getPossibleTurns_NotYourGame_403() throws IOException {
    }

    @Test
    public void getPossibleTurns_InvalidGame_403() throws IOException {
    }

    @Test
    public void getPossibleTurns_InvalidUser_401() throws IOException {
    }

    @Test
    public void getPossibleTurns_InvalidCredential_401() throws IOException {
    }

    //Play Turn
    @Test
    public void playTurn_YourTurn_200TurnMadeConfirmationOfTurn() throws IOException {
    }

    @Test
    public void playTurn_NotYourTurn_403TurnNotMade() throws IOException {
    }

    @Test
    public void playTurn_NotYourGame_403TurnNotMade() throws IOException {
    }

    @Test
    public void playTurn_InvalidGame_403() throws IOException {
    }

    @Test
    public void playTurn_IllegalTurn_200TurnNotMadeWithReason() throws IOException {
    }

    @Test
    public void playTurn_IllFormattedTurn_403TurnNotMade() throws IOException {
    }

    @Test
    public void playTurn_WinGame_200WinMessage() throws IOException {
    }

    @Test
    public void playTurn_InvalidUser_403TurnNotMade() throws IOException {
    }

    @Test
    public void playTurn_InvalidCredentials_403TurnNotMade() throws IOException {
    }

    //Get Notifications
    @Test
    public void getNotifications_NoRequests_200RequestsFieldZero() throws IOException {
    }

    @Test
    public void getNotifications_NonZeroRequests_200RequestsFieldMoreThanZero() throws IOException {
    }

    @Test
    public void getNotifications_NoGames_200YourTurnFieldZero() throws IOException {
    }

    @Test
    public void getNotifications_GamesNoneYourTurn_200YourTurnFieldZero() throws IOException {
    }

    @Test
    public void getNotifications_GamesSomeYourTurn_200YourTurnFieldOnlyYourTurn() throws IOException {
    }

    @Test
    public void getNotifications_InvalidUser_401() throws IOException {
    }

    @Test
    public void getNotifications_InvalidCredential_401() throws IOException {
    }
}
