# Submission packet: Beyond the Screen

Hackathon: Build, Ship, Shape: Amazon Developer Hackathon, https://amazonappdev2026.devpost.com/
Deadline: **Friday, Oct 23, 2026, 12:00 PM PT (3:00 PM ET)**. Rules: https://amazonappdev2026.devpost.com/rules

The full form text (description, inspiration, build, Bedrock, challenges, next steps, product feedback, friction log, feature request) is in [`devpost-submission.md`](devpost-submission.md). This file has the field checklist, testing instructions, and what's still open.

## Form fields

| Field | Value | Status |
|---|---|---|
| Project name | Beyond the Screen | Ready |
| Tagline | Fire TV recommendations for the rest of Amazon: the book, the tool, the coat, handed to your phone by QR. | Ready |
| Primary track | Fire TV | Ready |
| Mini challenge | AWS Builder (Amazon Bedrock Converse API, SigV4-signed from the device) | Ready |
| Description and story | `devpost-submission.md` | Ready |
| Product feedback (required) | `devpost-submission.md` § Product feedback | Ready |
| Demo video URL (YouTube/Vimeo, public, < 3:00) | **TBD. Needs recording and upload.** Shot list: `demo-script.md` | **Open** |
| Code repo URL | https://github.com/nightmoose/nmb-beyond-the-screen | **Open: the repo is private.** Make it public (MIT LICENSE is already there) or add the 5 Amazon reviewers (below) |
| APK / testing build | https://github.com/nightmoose/nmb-beyond-the-screen-apk/releases/tag/v0.1.0-debug | Ready (public) |
| Testing instructions | Below | Ready |
| Built with | Kotlin, Jetpack Compose, Media3 ExoPlayer, ZXing, Amazon Bedrock (Converse), Fire OS, GitHub Actions | Ready |
| New or updated during the window | Repo created Oct 17, 2026, inside the submission period (Aug 31 – Oct 23) | Ready |
| Devpost account, team members, eligibility checkboxes | Submitter fills these in | **Open** |

### Repo access (rules)
The repo has to be either public with an open-source license (detectable in the About section) or private and shared with these Amazon DevRel accounts: `knmeiss`, `giolaq`, `anishamalde`, `mosesroth`, `emersonsklar`.
Right now it is private, and `nightmoose` is the only collaborator.

## Testing instructions (paste into the form)

**Quickest path (no build, no AWS account):**
1. On a Fire TV: Settings → My Fire TV → Developer options → turn on ADB debugging and Apps from Unknown Sources. Alternatively, use an Android TV emulator (API 24+), which the hackathon FAQ accepts.
2. Download `beyond-the-screen-debug.apk` from https://github.com/nightmoose/nmb-beyond-the-screen-apk/releases/tag/v0.1.0-debug
3. Run `adb connect <fire-tv-ip>:5555`, then `adb install -r beyond-the-screen-debug.apk`. You can also sideload it with the Downloader app.
4. Open **Beyond the Screen** from Your Apps & Channels. You only need the D-pad.

**What to try:**
- Home: pick the featured title or a title on either shelf.
- Detail: move across the category rail, then across a row of suggestions. Each one shows a one-line reason and a QR code. Scan it with a phone and it opens an Amazon search (no affiliate tag).
- Play: the Sintel trailer is bundled and plays offline. Three CC shorts stream over HTTPS. Press **Beyond the screen** during playback to open recommendations without leaving the film.
- Without Bedrock credentials the app uses the curated catalog of ten titles and says so on screen. This is the expected judge path.

**Live Bedrock path (optional):** copy `bedrock.properties.example` to `bedrock.properties`, then set the region, access key, secret, an optional session token, and an optional model id (default `us.anthropic.claude-haiku-4-5-20251001-v1:0`). Build with `./gradlew :app:assembleDebug`. If a call fails or the reply can't be read, the app falls back to the curated set.

**Build from source:** JDK 17 and the Android SDK. `./gradlew test :app:assembleDebug`. CI runs the same command on every push.

## Pre-submit checklist
- [ ] Record the demo on a Fire TV, the Fire TV/Vega simulator, or an Android TV emulator. Keep it under 3:00, in English, with no copyrighted music. Follow `demo-script.md`.
- [ ] Upload it to YouTube or Vimeo as **public**, and paste the link here: `TBD`
- [ ] Make the repo public, or add the 5 reviewers.
- [ ] Fill in the Devpost form from `devpost-submission.md` and this file, then submit before 3:00 PM ET on Fri Oct 23.
