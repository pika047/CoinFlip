# Publishing FlipCoin on Google Play

Everything in `docs/play-store/` is ready to upload: `icon-512.png`, `feature-graphic.png`
(1024×500) and `listing.md`. The privacy policy is `docs/privacy-policy.md`.

## 1. Create the upload key (once, on your laptop)

Keep this file and its passwords safe and backed up. You need the same key for every update.

```bash
keytool -genkeypair -v -keystore flipcoin-upload.jks -alias upload \
  -keyalg RSA -keysize 2048 -validity 10000
```

Never commit the `.jks` file. The repo's `.gitignore` already ignores it.

## 2. Add GitHub secrets

Repo → Settings → Secrets and variables → Actions → New repository secret:

| Secret | Value |
| --- | --- |
| `UPLOAD_KEYSTORE_BASE64` | output of `base64 -w0 flipcoin-upload.jks` (macOS: `base64 -i flipcoin-upload.jks`) |
| `UPLOAD_KEYSTORE_PASSWORD` | the keystore password |
| `UPLOAD_KEY_ALIAS` | `upload` |
| `UPLOAD_KEY_PASSWORD` | the key password (same as the keystore password if you pressed Enter) |

## 3. Build the bundle

Actions → **Release** → Run workflow. When it finishes, download the `flipcoin-release-aab`
artifact. It holds the `.aab` to upload. Each run gets a higher version code automatically.

## 4. Play Console

1. Create the app: name "FlipCoin", app (not game), free.
2. Leave **Play App Signing** on (the default). Google keeps the real signing key, and your key above is only the upload key.
3. **Privacy policy**: put your contact email in `docs/privacy-policy.md` and use its public URL.
4. **App content**:
   - Ads: no.
   - Data safety: the app collects and shares no data.
   - Target audience: 13+ is simplest. Choosing under-13 brings in the Families policy.
   - Content rating questionnaire: answer it yourself. The Tails side's cartoon butt may count as mild crude humour, and answering it honestly is safer.
5. **Store listing**: copy the text from `listing.md`, then upload the icon, the feature graphic and at least 2 phone screenshots. The `device-screenshots` artifact from any CI run has them.
6. **Testing → Closed testing**: upload the `.aab`. Personal accounts created after 13 Nov 2023 need at least 12 testers opted in for 14 days in a row before they can apply for production access.
