package io.joshatron.tak.server;

import io.joshatron.tak.server.utils.HttpUtils;
import io.joshatron.tak.server.utils.RandomUtils;
import org.apache.http.client.HttpClient;
import org.junit.Test;

import java.io.IOException;

//Suite: C
//Current final test: 077
public class GameTest {

    private String suite;
    private HttpClient client;

    public GameTest() throws IOException {
        client = HttpUtils.createHttpClient();
        suite = "C" + RandomUtils.generateSuite(10);
    }

    //Request a Game
    @Test
    public void requestGame_RequestFriend_204RequestMade() throws IOException {
        String test = "001";
    }

    @Test
    public void requestGame_RequestNonFriend_403RequestNotMade() throws IOException {
        String test = "002";
    }

    @Test
    public void requestGame_RequestPendingFriend_403RequestNotMade() throws IOException {
        String test = "003";
    }

    @Test
    public void requestGame_RequestBlocked_403RequestNotMade() throws IOException {
        String test = "004";
    }

    @Test
    public void requestGame_RequestNonexistent_403RequestNotMade() throws IOException {
        String test = "005";
    }

    @Test
    public void requestGame_RequestWithExisting_403RequestNotMade() throws IOException {
        String test = "006";
    }

    @Test
    public void requestGame_RequestYourself_403RequestNotMade() throws IOException {
        String test = "007";
    }

    @Test
    public void requestGame_RequestExistingGame_403RequestNotMade() throws IOException {
        String test = "008";
    }

    @Test
    public void requestGame_InvalidUser_403RequestNotMade() throws IOException {
        String test = "009";
    }

    @Test
    public void requestGame_InvalidCredentials_403RequestNotMade() throws IOException {
        String test = "010";
    }

    //Cancel a Game Request
    @Test
    public void cancelGameRequest_BasicRequest_204RequestRemoved() throws IOException {
        String test = "078";
    }

    @Test
    public void cancelGameRequest_InvalidUser_401() throws IOException {
        String test = "079";
    }

    @Test
    public void cancelGameRequest_CancelInvalidUser_404() throws IOException {
        String test = "080";
    }

    @Test
    public void cancelGameRequest_InvalidCredentials_401() throws IOException {
        String test = "081";
    }

    @Test
    public void cancelGameRequest_RequestNotMade_403() throws IOException {
        String test = "082";
    }

    //Respond to Game Request
    @Test
    public void respondToGameRequest_RespondAccept_204GameStartedRequestRemoved() throws IOException {
        String test = "011";
    }

    @Test
    public void respondToGameRequest_RespondDeny_204GameNotStartedRequestRemoved() throws IOException {
        String test = "012";
    }

    @Test
    public void respondToGameRequest_RespondBadFormatting_403GameNotStartedRequestStillThere() throws IOException {
        String test = "013";
    }

    @Test
    public void respondToGameRequest_RespondNoRequest_403NoGameStarted() throws IOException {
        String test = "014";
    }

    @Test
    public void respondToGameRequest_InvalidUser_403GameNotStartedRequestStillThere() throws IOException {
        String test = "015";
    }

    @Test
    public void respondToGameRequest_InvalidCredentials_403GameNotStartedRequestStillThere() throws IOException {
        String test = "016";
    }

    //Check Incoming Game Request
    @Test
    public void checkIncomingGames_NoRequests_200EmptyArray() throws IOException {
        String test = "017";
    }

    @Test
    public void checkIncomingGames_OneRequest_200ArrayWithOne() throws IOException {
        String test = "018";
    }

    @Test
    public void checkIncomingGames_MultipleRequests_200ArrayWithMultiple() throws IOException {
        String test = "019";
    }

    @Test
    public void checkIncomingGames_OneOutgoing_200EmptyArray() throws IOException {
        String test = "020";
    }

    @Test
    public void checkIncomingGames_InvalidUser_403() throws IOException {
        String test = "021";
    }

    @Test
    public void checkIncomingGames_InvalidCredentials_403() throws IOException {
        String test = "022";
    }

    //Check Outgoing Game Request
    @Test
    public void checkOutgoingGames_NoRequests_200EmptyArray() throws IOException {
        String test = "023";
    }

    @Test
    public void checkOutgoingGames_200ArrayWithOne() throws IOException {
        String test = "024";
    }

    @Test
    public void checkOutgoingGames_200ArrayWithMultiple() throws IOException {
        String test = "025";
    }

    @Test
    public void checkOutgoingGames_OneIncoming_200EmptyArray() throws IOException {
        String test = "026";
    }

    @Test
    public void checkOutgoingGames_InvalidUser_403() throws IOException {
        String test = "027";
    }

    @Test
    public void checkOutgoingGames_InvalidCredentials_403() throws IOException {
        String test = "028";
    }

    //Request a Random Game
    @Test
    public void requestRandomGame_OneRequest_204NoGameCreated() throws IOException {
        String test = "029";
    }

    @Test
    public void requestRandomGame_TwoMatchingRequests_204GameCreated() throws IOException {
        String test = "030";
    }

    @Test
    public void requestRandomGame_TwoMismatchingRequests_204NoGameCreated() throws IOException {
        String test = "031";
    }

    @Test
    public void requestRandomGame_TwoMatchingAlreadyInGame_204NoGameCreated() throws IOException {
        String test = "032";
    }

    @Test
    public void requestRandomGame_TwoFriendsMatching_204GameCreated() throws IOException {
        String test = "033";
    }

    @Test
    public void requestRandomGame_TwoRandomMatching_204GameCreated() throws IOException {
        String test = "034";
    }

    @Test
    public void requestRandomGame_TwoMatchingBlocked_204NoGameCreated() throws IOException {
        String test = "035";
    }

    @Test
    public void requestRandomGame_TwoMismatchThenThirdMatch_204OneGameCreated() throws IOException {
        String test = "036";
    }

    @Test
    public void requestRandomGame_TwoBlockedMatchedThenNonBlockMatch_204OneGameCreated() throws IOException {
        String test = "037";
    }

    @Test
    public void requestRandomGame_InvalidUser_403() throws IOException {
        String test = "038";
    }

    @Test
    public void requestRandomGame_InvalidCredentials_403() throws IOException {
        String test = "039";
    }

    @Test
    public void requestRandomGame_GameSizeIllegalNumber_403() throws IOException {
        String test = "040";
    }

    @Test
    public void requestRandomGame_GameSizeString_403() throws IOException {
        String test = "041";
    }

    //Cancel a Random Game Request
    @Test
    public void cancelRandomRequest_BasicRequest_204RequestRemoved() throws IOException {
        String test = "083";
    }

    @Test
    public void cancelRandomRequest_InvalidUser_401() throws IOException {
        String test = "084";
    }

    @Test
    public void cancelRandomRequest_InvalidCredentials_401() throws IOException {
        String test = "085";
    }

    @Test
    public void cancelRandomRequest_RequestNotMade_404() throws IOException {
        String test = "086";
    }

    //Check Random Game Request
    @Test
    public void checkRandomRequest_BasicRequest_200Size() throws IOException {
        String test = "083";
    }

    @Test
    public void checkRandomRequest_InvalidUser_401() throws IOException {
        String test = "084";
    }

    @Test
    public void checkRandomRequest_InvalidCredentials_401() throws IOException {
        String test = "085";
    }

    @Test
    public void checkRandomRequest_RequestNotMade_404() throws IOException {
        String test = "086";
    }

    //List Games
    //TODO: review
    @Test
    public void listGames_NoGames_200EmptyArray() throws IOException {
        String test = "042";
    }

    @Test
    public void listGames_OneGame_200ArrayWithOne() throws IOException {
        String test = "043";
    }

    @Test
    public void listGames_MultipleGames_200ArrayWithMultiple() throws IOException {
        String test = "044";
    }

    @Test
    public void listGames_GameInProgress_200EmptyArray() throws IOException {
        String test = "045";
    }

    @Test
    public void listGames_OneOpponent_200GamesFromOpponent() throws IOException {
        String test = "046";
    }

    @Test
    public void listGames_MultipleOpponents_200GamesFromMultipleOpponents() throws IOException {
        String test = "047";
    }

    @Test
    public void listGames_StartInPast_200GamesFromPastOn() throws IOException {
        String test = "048";
    }

    @Test
    public void listGames_StartInCurrent_200EmptyGames() throws IOException {
        String test = "049";
    }

    @Test
    public void listGames_StartInFuture_403() throws IOException {
        String test = "050";
    }

    @Test
    public void listGames_OneSize_200GamesWithSize() throws IOException {
        String test = "051";
    }

    @Test
    public void listGames_SizeBadNumber_403() throws IOException {
        String test = "052";
    }

    @Test
    public void listGames_InvalidUser_403() throws IOException {
        String test = "053";
    }

    @Test
    public void listGames_InvalidCredentials_403() throws IOException {
        String test = "054";
    }

    @Test
    public void listGames_CompleteGame_200EmptyArray() throws IOException {
        String test = "058";
    }

    @Test
    public void listGames_Pending_200OnlyPending() throws IOException {
        String test = "059";
    }

    @Test
    public void listGames_NotPending_200OnlyNotPending() throws IOException {
        String test = "060";
    }

    @Test
    public void listGames_InvalidPending_403() throws IOException {
        String test = "061";
    }

    //Get Info on a Game
    @Test
    public void getGame_ValidGame_200GameInfo() throws IOException {
        String test = "064";
    }

    @Test
    public void getGame_NotYourGame_403() throws IOException {
        String test = "065";
    }

    @Test
    public void getGame_NotRealGame_403() throws IOException {
        String test = "066";
    }

    @Test
    public void getGame_InvalidUser_403() throws IOException {
        String test = "067";
    }

    @Test
    public void getGame_InvalidCredentials_403() throws IOException {
        String test = "068";
    }

    //Get Possible Next Turns For Game
    @Test
    public void getPossibleTurns_YourTurn_200PossibleTurns() throws IOException {
        String test = "087";
    }

    @Test
    public void getPossibleTurns_FinishedGame_200Empty() throws IOException {
        String test = "093";
    }

    @Test
    public void getPossibleTurns_TheirTurn_403() throws IOException {
        String test = "088";
    }

    @Test
    public void getPossibleTurns_NotYourGame_403() throws IOException {
        String test = "089";
    }

    @Test
    public void getPossibleTurns_InvalidGame_403() throws IOException {
        String test = "090";
    }

    @Test
    public void getPossibleTurns_InvalidUser_401() throws IOException {
        String test = "091";
    }

    @Test
    public void getPossibleTurns_InvalidCredential_401() throws IOException {
        String test = "092";
    }

    //Play Turn
    @Test
    public void playTurn_YourTurn_200TurnMadeConfirmationOfTurn() throws IOException {
        String test = "069";
    }

    @Test
    public void playTurn_NotYourTurn_403TurnNotMade() throws IOException {
        String test = "070";
    }

    @Test
    public void playTurn_NotYourGame_403TurnNotMade() throws IOException {
        String test = "071";
    }

    @Test
    public void playTurn_InvalidGame_403() throws IOException {
        String test = "072";
    }

    @Test
    public void playTurn_IllegalTurn_200TurnNotMadeWithReason() throws IOException {
        String test = "073";
    }

    @Test
    public void playTurn_IllFormattedTurn_403TurnNotMade() throws IOException {
        String test = "074";
    }

    @Test
    public void playTurn_WinGame_200WinMessage() throws IOException {
        String test = "075";
    }

    @Test
    public void playTurn_InvalidUser_403TurnNotMade() throws IOException {
        String test = "076";
    }

    @Test
    public void playTurn_InvalidCredentials_403TurnNotMade() throws IOException {
        String test = "077";
    }

    //Get Notifications
    @Test
    public void getNotifications_NoRequests_200RequestsFieldZero() throws IOException {
        String test = "094";
    }

    @Test
    public void getNotifications_NonZeroRequests_200RequestsFieldMoreThanZero() throws IOException {
        String test = "095";
    }

    @Test
    public void getNotifications_NoGames_200YourTurnFieldZero() throws IOException {
        String test = "096";
    }

    @Test
    public void getNotifications_GamesNoneYourTurn_200YourTurnFieldZero() throws IOException {
        String test = "097";
    }

    @Test
    public void getNotifications_GamesSomeYourTurn_200YourTurnFieldOnlyYourTurn() throws IOException {
        String test = "098";
    }

    @Test
    public void getNotifications_InvalidUser_401() throws IOException {
        String test = "099";
    }

    @Test
    public void getNotifications_InvalidCredential_401() throws IOException {
        String test = "100";
    }
}
