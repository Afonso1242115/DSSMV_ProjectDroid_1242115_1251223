# Campus L&F - Physical Android device test protocol

**Status:** Not yet executed. Fill the results below after testing on the real phone.

## Preparation (OnePlus Nord CE 2 Lite 5G or another Android 8.0+ device)

1. Open this repository's root in IntelliJ IDEA. Keep JDK 17 and Android SDK 35 configured.
2. Confirm `local.properties` has `sdk.dir` (if required), `restdb.url` and the **restricted** `restdb.apiKey`. Never commit this file.
3. Enable Developer options and **USB debugging** on the Android phone. The option names vary with Android/OxygenOS version.
4. Connect by a data-capable USB cable and approve the computer's debugging fingerprint on the phone.
5. In IntelliJ, choose the connected Android device, or run from the repository root:
   - Windows: `android\tools\install-on-device.bat`
   - macOS/Linux: `./android/tools/install-on-device.sh`
6. If Android reports an incompatible existing installation, ensure the package was signed with a different debug keystore before considering uninstalling. **Uninstall deletes the local Room cache**; back up any data needed first.

## Manual tests - record PASS / FAIL / NOT TESTED

| Test | Action | Expected result | Result |
|---|---|---|---|
| Launch | Open Campus L&F on phone | Home screen appears; no crash | NOT TESTED |
| API sync | Wi-Fi enabled, tap Refresh | Remote list loads; timestamp updates | NOT TESTED |
| Search | Search title, description, building and location | Case-insensitive matches | NOT TESTED |
| Combined filters | Select building, category and search | Results satisfy all criteria | NOT TESTED |
| Item details | Open a listed object | Text/photo show correctly | NOT TESTED |
| Photo | Take a photo for a new item | Preview shows correct photo and orientation | NOT TESTED |
| GPS allowed | Grant location and request GPS | Coordinates appear when available | NOT TESTED |
| GPS declined | Deny permission | App remains usable with written location | NOT TESTED |
| Publish | Complete valid form, tap Publish once | One record created; shown in list | NOT TESTED |
| Invalid data | Empty title or missing photo | Clear error; no server record created | NOT TESTED |
| Map | Open details with coordinates | Installed mapping app opens coordinates | NOT TESTED |
| Share | Tap Share item details | Android share chooser opens | NOT TESTED |
| Offline cache | Load list, disable connectivity, reopen | Previously synchronized list remains visible | NOT TESTED |
| Offline image | Inspect photo with network disabled | Image may be unavailable unless Glide cached it | NOT TESTED |
| Connection failure | Refresh while offline | Error shown; cached items not erased | NOT TESTED |
| Rotation | Rotate screen while searching or composing draft | Current values/filters remain | NOT TESTED |
| Mobile data | Disable Wi-Fi, use cellular data | API access works as permitted by the network/key | NOT TESTED |

## Reporting issues

Note device model, Android version, whether Wi-Fi/mobile data was used, exact error text and reproduction steps. Avoid including personal data, photos of other people, or the API key in reports/screenshots. Do **not** retry publication blindly after a network timeout: check whether the object already exists on the server.
