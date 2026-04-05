# Running the server with ssl

```bash
java.exe "-Djavax.net.ssl.trustStore=[trust-store path]"
-Djavax.net.ssl.trustStorePassword=[key-store password]
-classpath "[output-path]" client.Main
```

For example:

```bash
java.exe "-Djavax.net.ssl.trustStore=public.jks" -Djavax.net.ssl.trustStorePassword=password ./out client.Main
```