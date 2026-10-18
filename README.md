# Beyond the Screen

A Fire TV app that turns a movie or show into recommendations from the rest of Amazon: books, tools, clothes, food, music, and things you can go do. It is built for the Fire TV track of the Build, Ship, Shape hackathon, with Amazon Bedrock as an optional AWS Builder integration.

Watching stays on the television. Buying happens on a phone, through an on-screen QR code that opens an Amazon search. The app does not scrape Amazon, does not call the Product Advertising API, and does not add affiliate tags.

## What you can do in the app

- Browse ten demo titles. Six are bridge titles (recommendations only, original geometric art). Four are Creative Commons films from the Blender Foundation, with credit on screen.
- Open a title and move through category rows with the D-pad. The focused suggestion shows a one-line reason, an `amazon.com/s?k=` search link, and a QR code.
- Play the bundled Sintel trailer with no network, or stream Big Buck Bunny, Tears of Steel, and Elephants Dream when the network allows.
- During playback, press **Beyond the screen**. The film pauses and the same recommendations open beside the picture. Back resumes.

If Bedrock credentials are present, the app calls the Bedrock Converse API and uses that reply. If they are missing, the call fails, or the reply is not usable JSON, the app shows a curated set for that title. Judges and CI do not need keys.

## Layout

| Path | Role |
| --- | --- |
| `recommendation/` | Models, curated catalog, JSON parser, SigV4 signer, Bedrock client, engine. Plain JVM, so the tests do not need a device. |
| `app/` | Fire TV UI (Jetpack Compose), ExoPlayer playback, QR codes. |
| `app/src/main/res/raw/sintel_trailer.mp4` | Official Sintel trailer, bundled so the main playback demo works offline. |
| `docs/devpost-submission.md` | Draft text for the Devpost form. |
| `docs/demo-script.md` | Shot list for a public video under three minutes. |

## Build

Install JDK 17 or newer and the Android SDK (platform 35, build-tools 35). Point `ANDROID_HOME` at the SDK, or put `sdk.dir` in `local.properties` (that file is gitignored).

```bash
./gradlew :recommendation:test :app:testDebugUnitTest :app:assembleDebug
```

The debug APK is `app/build/outputs/apk/debug/app-debug.apk`.

CI runs the same tasks on Ubuntu. See `.github/workflows/ci.yml`.

## Android TV emulator

Use a television system image, not a phone image. In Android Studio: Device Manager, Create Device, TV, 1080p, a recent Android TV image.

From the command line, after the emulator package and a TV system image are installed:

```bash
avdmanager create avd -n beyond-tv -k "system-images;android-34;android-tv;x86_64" -d television_1080p
emulator -avd beyond-tv
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.beyondthescreen/.MainActivity
```

Arrow keys move focus. Enter selects. Esc is Back.

The hackathon FAQ accepts an Android TV emulator. The rules text also accepts a real Fire TV or the Fire TV / Vega simulator. Record the submission video on a real Fire TV if you have one.

## Sideload to a Fire TV

1. On the device: Settings, My Fire TV, About, select the device name seven times.
2. Settings, My Fire TV, Developer options, ADB debugging, ON.
3. Find the device IP under Settings, My Fire TV, About, Network.
4. From the computer:

```bash
adb connect FIRE_TV_IP:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.beyondthescreen/.MainActivity
```

`minSdk` is 24, which covers Fire OS 6 and newer. The manifest declares a Leanback launcher activity, a 320×180 banner, landscape orientation, and touchscreen as optional.

## Amazon Bedrock

Bedrock is optional. With no keys, every title uses the curated fallback and the home chip reads **Offline curated**.

The client calls `POST https://bedrock-runtime.{region}.amazonaws.com/model/{modelId}/converse` and signs the request with SigV4. The default model id is `us.anthropic.claude-haiku-4-5-20251001-v1:0`. Newer Claude models on Bedrock usually need that cross-region profile id, not the bare model id. Other ids that use the same Converse API:

- `amazon.nova-lite-v1:0`
- `us.anthropic.claude-3-5-haiku-20241022-v1:0`
- `anthropic.claude-3-haiku-20240307-v1:0`

In the AWS console for the same region: Bedrock, Model access, enable the model. Then an IAM principal needs `bedrock:InvokeModel` on the foundation model and, for profile ids, on the inference profile.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "bedrock:InvokeModel",
      "Resource": [
        "arn:aws:bedrock:*::foundation-model/*",
        "arn:aws:bedrock:*:*:inference-profile/*"
      ]
    }
  ]
}
```

### Configure without committing secrets

Copy the example and fill it in locally. `bedrock.properties` is gitignored.

```bash
cp bedrock.properties.example bedrock.properties
```

```properties
BEDROCK_REGION=us-east-1
BEDROCK_MODEL_ID=us.anthropic.claude-haiku-4-5-20251001-v1:0
BEDROCK_ACCESS_KEY_ID=
BEDROCK_SECRET_ACCESS_KEY=
BEDROCK_SESSION_TOKEN=
```

`BEDROCK_SESSION_TOKEN` is only for temporary credentials. Leave it empty for a long-lived access key.

Gradle reads those keys from the environment, from `bedrock.properties`, or from `local.properties`, and places non-empty values in the debug `BuildConfig`. Rebuild after changing them:

```bash
export BEDROCK_REGION=us-east-1
export BEDROCK_MODEL_ID=us.anthropic.claude-haiku-4-5-20251001-v1:0
export BEDROCK_ACCESS_KEY_ID=...
export BEDROCK_SECRET_ACCESS_KEY=...
./gradlew :app:assembleDebug
```

A build made this way contains the keys. Do not publish that APK. Prefer a device file on a build that was compiled without keys.

Push the same properties file to the debug app (debuggable, so `run-as` works):

```bash
adb push bedrock.properties /data/local/tmp/bedrock.properties
adb shell run-as com.beyondthescreen cp /data/local/tmp/bedrock.properties files/bedrock.properties
adb shell rm /data/local/tmp/bedrock.properties
```

You can also copy it to the external files directory:

```bash
adb shell mkdir -p /sdcard/Android/data/com.beyondthescreen/files
adb push bedrock.properties /sdcard/Android/data/com.beyondthescreen/files/bedrock.properties
```

On Android 11 and some Fire OS builds, `adb push` into `Android/data` is blocked. The `run-as` path is the reliable one for a debug build. Restart the app after copying. Non-blank keys in the device file override values baked into the APK. The home chip should read `Bedrock · claude-haiku-4-5` (or the short name of whatever model you set). A failed call keeps the curated rows and says so on the title screen.

The request sends the title, year, logline, synopsis, and themes. It does not send a shopper identity. There is no analytics SDK.

## Tests

`recommendation/` covers the curated catalog, search-link encoding (a single `k` parameter, no `tag`), the JSON parser, the official AWS SigV4 `get-vanilla` and `post-vanilla` vectors, and the engine: no keys means no network call, a valid model reply is used, and HTTP or unreadable replies fall back.

```bash
./gradlew :recommendation:test :app:testDebugUnitTest
```

## Playback and credit

| Title | How it plays | Credit |
| --- | --- | --- |
| Sintel trailer (52 sec) | Bundled in the APK | Blender Foundation, CC BY 3.0. https://studio.blender.org/films/sintel/ |
| Big Buck Bunny (about 10 min) | Streamed | Blender Foundation, CC BY 3.0. https://peach.blender.org/ |
| Tears of Steel (about 12 min) | Streamed | Blender Foundation, CC BY 3.0. https://mango.blender.org/ |
| Elephants Dream (about 11 min) | Streamed | (c) 2006 Blender Foundation / Netherlands Media Art Institute / elephantsdream.org, CC BY 2.5. https://orange.blender.org/ |

The picture and sound of those films are used under those licenses. This project does not use their logos. Bridge titles are identified by name only, with art drawn in the app, and they do not play.

Fraunces and Outfit are bundled under the SIL Open Font License. See `third_party/fonts/`.

## Design

The UI is a 10-foot layout: large type, a 36 dp screen margin, a visible focus ring, and D-pad targets instead of touch targets. Home is a featured bridge title plus two shelves. A title screen keeps categories on the left, suggestions in the middle, and the phone code on the right. Playback keeps credit on screen and puts **Beyond the screen** on the transport row.

## Owner checklist before submitting

- Record the video on a Fire TV, or on the Fire TV / Vega simulator. Keep it under three minutes, public, in English, on YouTube or Vimeo. `docs/demo-script.md` is the shot list. Do not add a copyrighted music bed.
- Paste `docs/devpost-submission.md` into the Devpost form. Primary track: Fire TV. Mini challenge: AWS Builder.
- Deadline: Friday, October 23, 2026, 12:00 pm Pacific (3:00 pm Eastern).
- This repository is MIT licensed. A public repo satisfies the open-source requirement. If the repo stays private, add the reviewer accounts named in the hackathon rules when you submit.
- Enable Bedrock only if you want the live path in the video. The curated path is a complete demo.
- Confirm no `bedrock.properties`, keystore, or filled-in key is committed.

## License

MIT. See `LICENSE`. Creative Commons films and the SIL fonts stay under their own licenses.
