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