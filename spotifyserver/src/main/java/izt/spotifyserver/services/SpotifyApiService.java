package izt.spotifyserver.services;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

@Service
public class SpotifyApiService {

    @Value("${spotify.clientid}")
    private String clientId;

    @Value("${spotify.clientsecret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Autowired
    private SpotifyApi spotifyApi;

   
    private RestTemplate restTemplate = new RestTemplate();

    public String getClientId(){
        return clientId;
    }

    // to generate the uri for login
    public String generateLoginUri(){
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                                                            .scope("user-read-private, user-read-email, user-top-read")
                                                            .show_dialog(true)
                                                            .build();
        URI uri = authorizationCodeUriRequest.execute();
        // String html = goToSpotifyLoginPage(uri);
        return uri.toString();
    }

    public String goToSpotifyLoginPage(URI uri){
        ResponseEntity<String> response =  restTemplate.getForEntity(uri, String.class);
        //System.out.println(response.getBody().toString());
        return response.getBody();
    }

    // use this for user-related stuff
    public String getUserAccessToken(String authKey)throws Exception{
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authKey).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
        String userAccessToken = authorizationCodeCredentials.getAccessToken();

        return userAccessToken;

    }

    public String redirectAfterAuth(String authKey)throws Exception{
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authKey).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
        String token = authorizationCodeCredentials.getAccessToken();
        return token;

    }

    public void getUserAccessTokenFromClient(String authCode, String verifier){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type","authorization_code");
        requestBody.add("code",authCode);
        requestBody.add("redirect_uri","http://localhost:4200/redirect");
        requestBody.add("client_id",clientId);
        requestBody.add("verifier", verifier);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(requestBody,headers);

        ResponseEntity<String> response = restTemplate.exchange("https://accounts.spotify.com/api/token",
                                                        HttpMethod.POST,
                                                        entity,
                                                        String.class);
        System.out.println(response.getBody());

    }

    
}
