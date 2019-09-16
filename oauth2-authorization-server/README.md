### Simple authorization server with JWT token support

#### Test using curl

##### Request client authorization using authorization_code flow
````
curl -i -c cookies.txt "http://localhost:9191/as/oauth/authorize" -d "response_type=code&client_id=oauth2-client"
````
Result => Redirect to user authentication URL

##### Authenticate user
````
curl -i -b cookies.txt -c cookies.txt "http://localhost:9191/as/login" -d "username=oauth2-user&password=user-password"
````
Result => Redirect to authorization URL

#### Request authorization code
````
curl -i -b cookies.txt "http://localhost:9191/as/oauth/authorize" -d "redirect_uri=http://localhost:9291/login"
````
Result => Redirect to client redirect_uri, authentication code in query parameter

#### Request access token using authorization code + client credentials
````
curl -i -u "oauth2-client:client-password" "http://localhost:9191/as/oauth/token" -d "code=<authorization code>&grant_type=authorization_code&redirect_uri=http://localhost:9291/login"
````
Result => JWT-encoded access token

#### All in one - client secret
````
curl -i -c cookies.txt "http://localhost:9191/as/oauth/authorize" -d "response_type=code&client_id=oauth2-client"
curl -i -b cookies.txt -c cookies.txt "http://localhost:9191/as/login" -d "username=oauth2-user&password=user-password"
code=$(curl -si -b cookies.txt "http://localhost:9191/as/oauth/authorize" -d "redirect_uri=http://localhost:9291/login" | ggrep -oP 'Location:.*code=\K\w+')
curl -i -u "oauth2-client:client-password" "http://localhost:9191/as/oauth/token" -d "code=$code&grant_type=authorization_code&redirect_uri=http://localhost:9291/login"
````

#### All-in-one - TLS / client certificate
```
export SK_KSTORE='jks/client/oauth2-client.pem'
export SK_KSTORE_PASS='Super Secret JWT Keypass'
export SK_TSTORE='jks/authorization-server/authorization-server-public.pem'
curl -i --cacert "${SK_TSTORE}" -c cookies.txt "https://localhost:9191/as/oauth/authorize" -d "response_type=code&client_id=oauth2-client"
curl -i --cacert "${SK_TSTORE}" -b cookies.txt -c cookies.txt "https://localhost:9191/as/login" -d "username=oauth2-user&password=user-password"
#curl -i --cacert "${SK_TSTORE}" -b cookies.txt "https://localhost:9191/as/oauth/authorize" -d "redirect_uri=https://localhost:9291/login"
code=$(curl -si --cacert "${SK_TSTORE}" -b cookies.txt "https://localhost:9191/as/oauth/authorize" -d "redirect_uri=https://localhost:9291/login" | ggrep -oP 'Location:.*code=\K\w+')
curl -i --cacert "${SK_TSTORE}" --cert "${SK_KSTORE}:${SK_KSTORE_PASS}" "https://localhost:9192/as/oauth/token" -d "code=$code&grant_type=authorization_code&redirect_uri=https://localhost:9291/login"
```
