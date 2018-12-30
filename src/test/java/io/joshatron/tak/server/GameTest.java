package io.joshatron.tak.server;

import io.joshatron.tak.server.utils.HttpUtils;
import io.joshatron.tak.server.utils.RandomUtils;
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
    }

    @Test
    public void requestGame_RequestNonFriend_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_RequestPendingFriend_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_RequestBlocked_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_RequestNonexistent_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_RequestWithExisting_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_RequestYourself_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_RequestExistingGame_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_InvalidUser_403RequestNotMade() throws IOException {
    }

    @Test
    public void requestGame_InvalidCredentials_403RequestNotMade() throws IOException {
    }

    //Cancel a Game Request
    @Test
    public void cancelGameRequest_BasicRequest_204RequestRemoved() throws IOException {
    }

    @Test
    public void cancelGameRequest_InvalidUser_401() throws IOException {
    }

    @Test
    public void cancelGameRequest_CancelInvalidUser_404() throws IOException {
    }

    @Test
    public void cancelGameRequest_InvalidCredentials_401() throws IOException {
    }

    @Test
    public void cancelGameRequest_RequestNotMade_403() throws IOException {
    }

    //Respond to Game Request
    @Test
    public void respondToGameRequest_RespondAccept_204GameStartedRequestRemoved() throws IOException {
    }

    @Test
    public void respondToGameRequest_RespondDeny_204GameNotStartedRequestRemoved() throws IOException {
    }

    @Test
    public void respondToGameRequest_RespondBadFormatting_403GameNotStartedRequestStillThere() throws IOException {
    }

    @Test
    public void respondToGameRequest_RespondNoRequest_403NoGameStarted() throws IOException {
    }

    @Test
    public void respondToGameRequest_InvalidUser_403GameNotStartedRequestStillThere() throws IOException {
    }

    @Test
    public void respondToGameRequest_InvalidCredentials_403GameNotStartedRequestStillThere() throws IOException {
    }

    //Check Incoming Game Request
    @Test
    public void checkIncomingGames_NoRequests_200EmptyArray() throws IOException {
    }

    @Test
    public void checkIncomingGames_OneRequest_200ArrayWithOne() throws IOException {
    }

    @Test
    public void checkIncomingGames_MultipleRequests_200ArrayWithMultiple() throws IOException {
    }

    @Test
    public void checkIncomingGames_OneOutgoing_200EmptyArray() throws IOException {
    }

    @Test
    public void checkIncomingGames_InvalidUser_403() throws IOException {
    }

    @Test
    public void checkIncomingGames_InvalidCredentials_403() throws IOException {
    }

    //Check Outgoing Game Request
    @Test
    public void checkOutgoingGames_NoRequests_200EmptyArray() throws IOException {
    }

    @Test
    public void checkOutgoingGames_200ArrayWithOne() throws IOException {
    }

    @Test
    public void checkOutgoingGames_200ArrayWithMultiple() throws IOException {
    }

    @Test
    public void checkOutgoingGames_OneIncoming_200EmptyArray() throws IOException {
    }

    @Test
    public void checkOutgoingGames_InvalidUser_403() throws IOException {
    }

    @Test
    public void checkOutgoingGames_InvalidCredentials_403() throws IOException {
    }

    //Request a Random Game
    @Test
    public void requestRandomGame_OneRequest_204NoGameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoMatchingRequests_204GameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoMismatchingRequests_204NoGameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoMatchingAlreadyInGame_204NoGameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoFriendsMatching_204GameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoRandomMatching_204GameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoMatchingBlocked_204NoGameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoMismatchThenThirdMatch_204OneGameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_TwoBlockedMatchedThenNonBlockMatch_204OneGameCreated() throws IOException {
    }

    @Test
    public void requestRandomGame_InvalidUser_403() throws IOException {
    }

    @Test
    public void requestRandomGame_InvalidCredentials_403() throws IOException {
    }

    @Test
    public void requestRandomGame_GameSizeIllegalNumber_403() throws IOException {
    }

    @Test
    public void requestRandomGame_GameSizeString_403() throws IOException {
    }

    //Cancel a Random Game Request
    @Test
    public void cancelRandomRequest_BasicRequest_204RequestRemoved() throws IOException {
    }

    @Test
    public void cancelRandomRequest_InvalidUser_401() throws IOException {
    }

    @Test
    public void cancelRandomRequest_InvalidCredentials_401() throws IOException {
    }

    @Test
    public void cancelRandomRequest_RequestNotMade_404() throws IOException {
    }

    //Check Random Game Request
    @Test
    public void checkRandomRequest_BasicRequest_200Size() throws IOException {
    }

    @Test
    public void checkRandomRequest_InvalidUser_401() throws IOException {
    }

    @Test
    public void checkRandomRequest_InvalidCredentials_401() throws IOException {
    }

    @Test
    public void checkRandomRequest_RequestNotMade_404() throws IOException {
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
    }

    @Test
    public void getGame_NotYourGame_403() throws IOException {
    }

    @Test
    public void getGame_NotRealGame_403() throws IOException {
    }

    @Test
    public void getGame_InvalidUser_403() throws IOException {
    }

    @Test
    public void getGame_InvalidCredentials_403() throws IOException {
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
