
# Server

## Build

```bash
javac -d bin/ "src/common/*.java" "src/common/models/*.java" "src/common/models/messages/*.java" "src/common/models/requests/*.java" "src/common/models/responses/*.java" "src/common/ui/*.java" "src/server/*.java" "src/server/repositories/*.java" "src/server/ui/*.java"
```

Or, import as an Eclipse project.

## Run

```bash
java.exe "-Djdk.tls.server.protocols=TLSv1.2" -classpath "bin/" server.Main "[key_store_path]" "[key_store_password]"
```

# Client

## Build

```bash
javac -d bin/ "src/common/*.java" "src/common/models/*.java" "src/common/models/messages/*.java" "src/common/models/requests/*.java" "src/common/models/responses/*.java" "src/common/ui/*.java" "src/client/*.java" "src/client/ui/*.java"
```

## Run

```bash
java.exe "-Djdk.tls.client.protocols=TLSv1.2" "-Djavax.net.ssl.trustStore=[trust_store_path]" "-Djavax.net.ssl.trustStorePassword=[trust_store_password]" -classpath "bin/" client.Main
```

# SSL Setup

## Generating a demo key-store

```bash
keytool -genkeypair -v -keystore herong.jks -alias JKS
```

1. Enter & re-enter password: `password`.
2. Enter nothing until confirmation.
3. Enter `yes`.

## Generating client certificate

```bash
keytool -exportcert -alias JKS -file my_home.crt -keystore herong.jks -storepass password
```

## Generating client public key-store

```bash
keytool.exe -importcert -alias herong_home -file my_home.crt -keystore public.jks -storepass password
```

1. Enter `yes`.