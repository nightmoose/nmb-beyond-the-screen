# Devpost draft — Beyond the Screen

Paste and edit this into the submission form. Keep the video link, repository URL, and any account fields for the person who submits. Do not add secrets.

## Project name

Beyond the Screen

## Primary track

Fire TV

## Mini challenge

AWS Builder (Amazon Bedrock Converse API)

The repository is also MIT licensed, which meets the public-repo license rule for the main submission. The Open Source mini challenge asks for an additional contribution alongside the primary project. This repository is the primary project, so the mini challenge to enter is AWS Builder.

## Short description

Beyond the Screen is a Fire TV app that starts from a film and recommends the rest of Amazon: the book, the tool, the coat, the ingredient, the record, the lesson. Each suggestion has a one-line reason, an Amazon search link with no affiliate tag, and a QR code you scan on your phone. A curated set of ten titles works with no API keys. When Amazon Bedrock is configured, the same screen is filled by the Converse API instead.

## Inspiration

A television app that only recommends more television leaves the rest of a retail catalog unused. The interesting moment is the one after the credits, when a naval film makes someone want a sextant, a model frigate, or an afternoon on a real boat. Fire TV is the screen in the room where that impulse starts, and a phone is where the search should finish. The app is that bridge, built so a judge can see it with the D-pad and without an AWS account.

## What it does

- Home screen with a featured bridge title, a shelf of films that play in the app, and a shelf of titles that exist for recommendations.
- Category groups for each title. Focus a suggestion to read why it belongs and to show a QR code for `https://www.amazon.com/s?k=...`.
- Playback of a bundled Creative Commons trailer, plus three streamed Creative Commons shorts, with credit on screen. **Beyond the screen** opens the recommendations without leaving the film.
- A provider boundary: Bedrock when credentials exist and the reply parses, curated data otherwise.

The app does not scrape product pages, does not call the Product Advertising API, and does not append an affiliate tag.

## How it is built

- Fire OS client in Kotlin and Jetpack Compose, `minSdk` 24, Leanback launcher, landscape, D-pad focus.
- ExoPlayer (Media3) for a bundled Sintel trailer and for HTTPS streams of Big Buck Bunny, Tears of Steel, and Elephants Dream.
- ZXing to draw QR codes on the television.
- A JVM module that owns the catalog, the curated fallback, search-link construction, a small SigV4 signer, and the Bedrock Converse call. The Android app depends on that module. Unit tests run on the host, and GitHub Actions runs them plus `assembleDebug`.
- Original geometric title art and SIL Open Font License fonts (Fraunces, Outfit). No studio key art and no studio logos.

## Bedrock usage

The AWS integration is a real Converse request, not a mention in the README.

- Endpoint: `bedrock-runtime.{region}.amazonaws.com`, path `/model/{modelId}/converse`.
- Signing: AWS Signature Version 4, service name `bedrock`, optional session token.
- Default model id: `us.anthropic.claude-haiku-4-5-20251001-v1:0`, overridable. The same client works with other Converse models, including Amazon Nova Lite.
- The prompt asks for 4–6 retail categories and three suggestions each, forbids more movies and affiliate tags, and demands one-sentence reasons. The parser drops streaming categories and `tag=` parameters. Fewer than two usable categories, an HTTP error, or a timeout returns the curated set and says so on screen.
- Keys come from the environment or `bedrock.properties` at build time, or from a file on the device. None are committed. CI and a fresh clone run entirely on curated data.

## Challenges

- A television should not be the place a long-lived cloud key lives. The hackathon demo still has to call Bedrock from the device the judge is holding, so credentials are optional, overridable on the device, and kept out of git. A shipped version would put the model behind a small backend.
- There is no pleasant supported way to recommend specific products without the Product Advertising API, and that API was out of scope. Search links plus a QR code are the honest integration: the shopper lands on Amazon’s own results.
- Public sample-video hosts are not stable. The Google sample bucket used by many ExoPlayer demos returned 403, so playback uses a Blender-hosted trailer bundled in the APK and Archive.org copies of three CC films.
- Ten-foot focus is a layout problem. The title screen is a category rail, one row of suggestions, and a persistent phone panel, instead of a dense grid.

## What is next

- A tiny backend that holds the Bedrock call, so the television never stores a key, with the same curated fallback when the network fails.
- Remote voice as a third input beside the D-pad and the phone: “more like the sextant” refines the row that is already on screen.
- A household profile that remembers which bridges were scanned, still without an affiliate tag.
- If a sanctioned retail API becomes available to the developer, replace the search link with a real product card. Until then, the search page is the handoff.

## Product feedback

**Tools used**

- Fire OS / Android TV application model (Leanback launcher, banner, D-pad, landscape).
- Jetpack Compose and Media3 ExoPlayer for the 10-foot UI and playback.
- Amazon Bedrock Converse API, signed from the device.

**What worked**

- The television launcher contract is clear: a Leanback activity, a banner, and no required touchscreen are enough to install on an Android TV emulator and sideload to Fire OS.
- Media3 plays a raw resource and an HTTPS stream with the same player, which let one bundled trailer carry the demo when a stream is blocked.
- Converse is one JSON shape for more than one model family, so the client can point at Claude or Nova by changing the model id.

**What needs work**

- Calling Bedrock from Fire OS means hand-rolled SigV4. A small, documented Android sample for Converse would remove that.
- Model access, inference-profile ids, and `bedrock:InvokeModel` resources are easy to misconfigure, and the error text is the only feedback on the television.
- Fire TV and Android TV documentation overlap but do not say, in one place, which emulator image is acceptable for a hackathon video. The rules text and the FAQ differ (device or Fire TV/Vega simulator, versus Android TV emulator).

**Onboarding**

Getting a “hello” picture on a TV image is fast. Getting a model reply is slower: enable the model, pick a profile id the region actually serves, and decide where the key lives. The app treats that second half as optional so a first run still does something.

**Build with these again?**

Yes. Fire TV is a good surface for a shared decision, and Bedrock is a good way to fill a catalog you cannot license. The next version would call Bedrock from a backend and keep this on-device path only as the offline demo.

## Friction log

1. **Task:** Stream the usual ExoPlayer sample MP4s (Big Buck Bunny, Sintel, Tears of Steel, Elephants Dream) from `commondatastorage.googleapis.com`.
   **Steps:** `curl -I` each HTTPS URL.
   **Expected:** 200 and `video/mp4`.
   **Actual:** HTTP 403 from the bucket.
   **Severity:** High for a playback demo.
   **Workaround:** Bundle the official Sintel trailer from `download.blender.org` and stream the other three films from Archive.org and Blender’s own host.
   **Suggestion:** Publish a stable, documented set of CC sample streams for Fire TV samples, or say plainly that the old Google bucket is closed.

2. **Task:** Invoke Bedrock from an Android/Fire OS app.
   **Steps:** Look for a first-party Android Converse sample that does not pull a large JVM SDK into a television APK.
   **Expected:** A short signed-request example aimed at mobile.
   **Actual:** The documented samples are server SDKs. The television client signs Converse itself.
   **Severity:** Medium.
   **Workaround:** A few dozen lines of SigV4, checked against the published `get-vanilla` and `post-vanilla` test vectors.
   **Suggestion:** Add an Android snippet for Converse that uses the same profile ids the console shows.

3. **Task:** Decide which emulator the submission video may use.
   **Steps:** Read the rules and the FAQ.
   **Expected:** One answer.
   **Actual:** The rules say a Fire TV device or the Fire TV/Vega simulator. The FAQ says an Android TV emulator is enough.
   **Severity:** Low, but it changes how people record.
   **Workaround:** Document both, and recommend a real Fire TV for the video.
   **Suggestion:** Put one sentence in the rules that matches the FAQ.

## Feature request

Let a Fire TV app hand a short list of search intents to the shopper’s phone without an affiliate tag and without embedding a key. Priority: Important. The QR code is the workaround that ships today.
