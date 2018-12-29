package io.joshatron.tak.server.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

public class HttpUtils {

    public static String baseUrl = "https://localhost:8080";

    public static HttpClient createHttpClient() {
        try {
            SSLContextBuilder builder = SSLContexts.custom();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                    return true;
                }
            });
            SSLContext sslContext = builder.build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslContext, new X509HostnameVerifier() {
                @Override
                public void verify(String host, SSLSocket ssl)
                        throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert)
                        throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns,
                                   String[] subjectAlts) throws SSLException {
                }

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create().register("https", sslsf)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);

            return HttpClients.custom().setConnectionManager(cm).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getBasicAuthString(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public static HttpResponse createUser(String username, String password, HttpClient client) throws IOException {
        String payload = "{";
        if(username != null) {
            payload += "\"username\": \"" + username + "\"";
        }
        if(password != null) {
            if(username != null) {
                payload += ",";
            }
            payload += "\"password\": \"" + password + "\"";
        }
        payload += "}";

        HttpPost request = new HttpPost(baseUrl + "/account/register");
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        return client.execute(request);
    }

    public static HttpResponse changeUsername(String username, String password, String newPassword, HttpClient client) throws IOException {
        String payload = "{";
        if (newPassword != null) {
            payload += "\"text\": \"" + newPassword + "\"";
        }
        payload += "}";

        HttpPost request = new HttpPost(baseUrl + "/account/changename");
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        if (username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }
        request.setEntity(entity);

        return client.execute(request);
    }

    public static HttpResponse changePassword(String username, String password, String newPassword, HttpClient client) throws IOException {
        String payload = "{";
        if(newPassword != null) {
            payload += "\"text\": \"" + newPassword + "\"";
        }
        payload += "}";

        HttpPost request = new HttpPost(baseUrl + "/account/changepass");
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }
        request.setEntity(entity);

        return client.execute(request);
    }


    public static HttpResponse authenticate(String username, String password, HttpClient client) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/account/authenticate");
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse searchUser(String username, String userId, HttpClient client) throws IOException {
        StringBuilder params = new StringBuilder("?");
        if(username != null) {
            params.append("user=");
            params.append(username);
            if(userId != null) {
                params.append("&");
            }
        }
        if(userId != null) {
            params.append("id=");
            params.append(userId);
        }
        HttpGet request = new HttpGet(baseUrl + "/account/user" + params.toString());

        return client.execute(request);
    }

    public static HttpResponse requestFriend(String username, String password, String other, HttpClient client) throws IOException {
        HttpPost request = new HttpPost(baseUrl + "/social/request/create/" + other);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse cancelFriendRequest(String username, String password, String other, HttpClient client) throws IOException {
        HttpDelete request = new HttpDelete(baseUrl + "/social/request/cancel/" + other);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse respondToRequest(String username, String password, String other, String response, HttpClient client) throws IOException {
        String payload = "{\"text\": \"" + response + "\"}";

        HttpPost request = new HttpPost(baseUrl + "/social/request/respond/" + other);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        return client.execute(request);
    }

    public static HttpResponse checkIncomingFriendRequests(String username, String password, HttpClient client) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/social/request/incoming");
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse checkOutgoingFriendRequests(String username, String password, HttpClient client) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/social/request/outgoing");
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse unfriendUser(String username, String password, String other, HttpClient client) throws IOException {
        HttpPost request = new HttpPost(baseUrl + "/social/user/unfriend/" + other);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse blockUser(String username, String password, String other, HttpClient client) throws IOException {
        HttpPost request = new HttpPost(baseUrl + "/social/user/block/" + other);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse unblockUser(String username, String password, String other, HttpClient client) throws IOException {
        HttpDelete request = new HttpDelete(baseUrl + "/social/user/unblock/" + other);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse checkIfBlocked(String username, String password, String other, HttpClient client) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/social/user/blocked/" + other);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse getFriends(String username, String password, HttpClient client) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/social/user/friends");
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse getBlocked(String username, String password, HttpClient client) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/social/user/blocking");
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse sendMessage(String username, String password, String recipient, String message, HttpClient client) throws IOException {
        String payload = "{\"text\": \"" + message + "\"}";

        HttpPost request = new HttpPost(baseUrl + "/social/message/send/" + recipient);
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        return client.execute(request);
    }

    public static HttpResponse searchMessages(String username, String password, String senders, Date start, Date end, String read, String from, HttpClient client) throws IOException {
        boolean first = true;
        StringBuilder params = new StringBuilder("?");

        if(senders != null) {
            params.append("others=");
            params.append(senders);
            first = false;
        }
        if(start != null) {
            if(!first) {
                params.append("&");
            }
            params.append("start=");
            params.append(start.getTime());
            first = false;
        }
        if(end != null) {
            if(!first) {
                params.append("&");
            }
            params.append("end=");
            params.append(end.getTime());
            first = false;
        }
        if(read != null) {
            if(!first) {
                params.append("&");
            }
            params.append("read=");
            params.append(read);
            first = false;
        }
        if(from != null) {
            if(!first) {
                params.append("&");
            }
            params.append("from=");
            params.append(from);
            first = false;
        }

        HttpGet request;
        if(!first) {
            request = new HttpGet(baseUrl + "/social/message/search" + params);
        }
        else {
            request = new HttpGet(baseUrl + "/social/message/search");
        }
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }

    public static HttpResponse markRead(String username, String password, String[] ids, Date start, HttpClient client) throws IOException {
        StringBuilder payload = new StringBuilder("{");
        if(ids != null) {
            payload.append("\"ids\": [");
            boolean first = true;
            for(String id : ids) {
                if(!first) {
                    payload.append(",");
                }
                payload.append("\"");
                payload.append(id);
                payload.append("\"");
                first = false;
            }
            payload.append("]");
            if(start != null) {
                payload.append(",");
            }
        }
        if(start != null) {
            payload.append("\"start\": ");
            payload.append(start.getTime());
        }
        payload.append("}");

        HttpPost request = new HttpPost(baseUrl + "/social/message/markread");
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }
        StringEntity entity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        return client.execute(request);
    }

    public static HttpResponse getNotifications(String username, String password, HttpClient client) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/social/notifications");
        if(username != null && password != null) {
            request.setHeader("Authorization", getBasicAuthString(username, password));
        }

        return client.execute(request);
    }
}
