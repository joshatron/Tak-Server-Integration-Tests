package io.joshatron.tak.server;

import io.joshatron.tak.server.utils.HttpUtils;
import io.joshatron.tak.server.utils.RandomUtils;
import org.apache.http.client.HttpClient;
import org.junit.Test;

//Suite: C
//Current final test: 077
public class GameTest {

    private String suite;
    private HttpClient client;

    public GameTest() {
        client = HttpUtils.createHttpClient();
        suite = "C" + RandomUtils.generateSuite(10);
    }

    //Request a Game
    @Test
    public void requestGame_RequestFriend_204RequestMade() {
        String test = "001";
    }

    @Test
    public void requestGame_RequestNonFriend_403RequestNotMade() {
        String test = "002";
    }

    @Test
    public void requestGame_RequestPendingFriend_403RequestNotMade() {
        String test = "003";
    }

    @Test
    public void requestGame_RequestBlocked_403RequestNotMade() {
        String test = "004";
    }

    @Test
    public void requestGame_RequestNonexistent_403RequestNotMade() {
        String test = "005";
    }

    @Test
    public void requestGame_RequestWithExisting_403RequestNotMade() {
        String test = "006";
    }

    @Test
    public void requestGame_RequestYourself_403RequestNotMade() {
        String test = "007";
    }

    @Test
    public void requestGame_RequestExistingGame_403RequestNotMade() {
        String test = "008";
    }

    @Test
    public void requestGame_InvalidUser_403RequestNotMade() {
        String test = "009";
    }

    @Test
    public void requestGame_InvalidCredentials_403RequestNotMade() {
        String test = "010";
    }

    //Cancel a Game Request
    //TODO: implement

    //Respond to Game Request
    @Test
    public void respondToGameRequest_RespondAccept_204GameStartedRequestRemoved() {
        String test = "011";
    }

    @Test
    public void respondToGameRequest_RespondDeny_204GameNotStartedRequestRemoved() {
        String test = "012";
    }

    @Test
    public void respondToGameRequest_RespondBadFormatting_403GameNotStartedRequestStillThere() {
        String test = "013";
    }

    @Test
    public void respondToGameRequest_RespondNoRequest_403NoGameStarted() {
        String test = "014";
    }

    @Test
    public void respondToGameRequest_InvalidUser_403GameNotStartedRequestStillThere() {
        String test = "015";
    }

    @Test
    public void respondToGameRequest_InvalidCredentials_403GameNotStartedRequestStillThere() {
        String test = "016";
    }

    //Check Incoming Game Request
    @Test
    public void checkIncomingGames_NoRequests_200EmptyArray() {
        String test = "017";
    }

    @Test
    public void checkIncomingGames_OneRequest_200ArrayWithOne() {
        String test = "018";
    }

    @Test
    public void checkIncomingGames_MultipleRequests_200ArrayWithMultiple() {
        String test = "019";
    }

    @Test
    public void checkIncomingGames_OneOutgoing_200EmptyArray() {
        String test = "020";
    }

    @Test
    public void checkIncomingGames_InvalidUser_403() {
        String test = "021";
    }

    @Test
    public void checkIncomingGames_InvalidCredentials_403() {
        String test = "022";
    }

    //Check Outgoing Game Request
    @Test
    public void checkOutgoingGames_NoRequests_200EmptyArray() {
        String test = "023";
    }

    @Test
    public void checkOutgoingGames_200ArrayWithOne() {
        String test = "024";
    }

    @Test
    public void checkOutgoingGames_200ArrayWithMultiple() {
        String test = "025";
    }

    @Test
    public void checkOutgoingGames_OneIncoming_200EmptyArray() {
        String test = "026";
    }

    @Test
    public void checkOutgoingGames_InvalidUser_403() {
        String test = "027";
    }

    @Test
    public void checkOutgoingGames_InvalidCredentials_403() {
        String test = "028";
    }

    //Request a Random Game
    @Test
    public void requestRandomGame_OneRequest_204NoGameCreated() {
        String test = "029";
    }

    @Test
    public void requestRandomGame_TwoMatchingRequests_204GameCreated() {
        String test = "030";
    }

    @Test
    public void requestRandomGame_TwoMismatchingRequests_204NoGameCreated() {
        String test = "031";
    }

    @Test
    public void requestRandomGame_TwoMatchingAlreadyInGame_204NoGameCreated() {
        String test = "032";
    }

    @Test
    public void requestRandomGame_TwoFriendsMatching_204GameCreated() {
        String test = "033";
    }

    @Test
    public void requestRandomGame_TwoRandomMatching_204GameCreated() {
        String test = "034";
    }

    @Test
    public void requestRandomGame_TwoMatchingBlocked_204NoGameCreated() {
        String test = "035";
    }

    @Test
    public void requestRandomGame_TwoMismatchThenThirdMatch_204OneGameCreated() {
        String test = "036";
    }

    @Test
    public void requestRandomGame_TwoBlockedMatchedThenNonBlockMatch_204OneGameCreated() {
        String test = "037";
    }

    @Test
    public void requestRandomGame_InvalidUser_403() {
        String test = "038";
    }

    @Test
    public void requestRandomGame_InvalidCredentials_403() {
        String test = "039";
    }

    @Test
    public void requestRandomGame_GameSizeIllegalNumber_403() {
        String test = "040";
    }

    @Test
    public void requestRandomGame_GameSizeString_403() {
        String test = "041";
    }

    //Cancel a Random Game Request
    //TODO: implement

    //Check Random Game Request
    //TODO: implement

    //List Games
    //TODO: review
    @Test
    public void listGames_NoGames_200EmptyArray() {
        String test = "042";
    }

    @Test
    public void listGames_OneGame_200ArrayWithOne() {
        String test = "043";
    }

    @Test
    public void listGames_MultipleGames_200ArrayWithMultiple() {
        String test = "044";
    }

    @Test
    public void listGames_GameInProgress_200EmptyArray() {
        String test = "045";
    }

    @Test
    public void listGames_OneOpponent_200GamesFromOpponent() {
        String test = "046";
    }

    @Test
    public void listGames_MultipleOpponents_200GamesFromMultipleOpponents() {
        String test = "047";
    }

    @Test
    public void listGames_StartInPast_200GamesFromPastOn() {
        String test = "048";
    }

    @Test
    public void listGames_StartInCurrent_200EmptyGames() {
        String test = "049";
    }

    @Test
    public void listGames_StartInFuture_403() {
        String test = "050";
    }

    @Test
    public void listGames_OneSize_200GamesWithSize() {
        String test = "051";
    }

    @Test
    public void listGames_SizeBadNumber_403() {
        String test = "052";
    }

    @Test
    public void listGames_InvalidUser_403() {
        String test = "053";
    }

    @Test
    public void listGames_InvalidCredentials_403() {
        String test = "054";
    }

    @Test
    public void listGames_CompleteGame_200EmptyArray() {
        String test = "058";
    }

    @Test
    public void listGames_Pending_200OnlyPending() {
        String test = "059";
    }

    @Test
    public void listGames_NotPending_200OnlyNotPending() {
        String test = "060";
    }

    @Test
    public void listGames_InvalidPending_403() {
        String test = "061";
    }

    //Get Info on a Game
    @Test
    public void getGame_ValidGame_200GameInfo() {
        String test = "064";
    }

    @Test
    public void getGame_NotYourGame_403() {
        String test = "065";
    }

    @Test
    public void getGame_NotRealGame_403() {
        String test = "066";
    }

    @Test
    public void getGame_InvalidUser_403() {
        String test = "067";
    }

    @Test
    public void getGame_InvalidCredentials_403() {
        String test = "068";
    }

    //Get Possible Next Turns For Game
    //TODO: implement

    //Play Turn
    @Test
    public void playTurn_YourTurn_200TurnMadeConfirmationOfTurn() {
        String test = "069";
    }

    @Test
    public void playTurn_NotYourTurn_403TurnNotMade() {
        String test = "070";
    }

    @Test
    public void playTurn_NotYourGame_403TurnNotMade() {
        String test = "071";
    }

    @Test
    public void playTurn_InvalidGame_403() {
        String test = "072";
    }

    @Test
    public void playTurn_IllegalTurn_200TurnNotMadeWithReason() {
        String test = "073";
    }

    @Test
    public void playTurn_IllFormattedTurn_403TurnNotMade() {
        String test = "074";
    }

    @Test
    public void playTurn_WinGame_200WinMessage() {
        String test = "075";
    }

    @Test
    public void playTurn_InvalidUser_403TurnNotMade() {
        String test = "076";
    }

    @Test
    public void playTurn_InvalidCredentials_403TurnNotMade() {
        String test = "077";
    }

    //Get Notifications
    //TODO: implement
}
