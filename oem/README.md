# OEM side

System-image customization. This is where the project's real deliverable lives —
`app/` is only the prototyping harness (`PROJECT_GOAL.md`).

The first change here is `overlays/systembars`, which removes every CarSystemUI
system bar. It exists to make the OEM customization mechanism concrete; the
mechanism matters more than the change.

## How an OEM change reaches the screen

AAOS exposes three kinds of lever (`PLATFORM.md`). This overlay uses the
cheapest one: **override a resource that AOSP code already reads.**

CarSystemUI reads `config_enableTopSystemBar` and friends at startup to decide
which bars to build. An RRO (Runtime Resource Overlay) is an APK with no code
that replaces those values in the target's resource table. CarSystemUI is
untouched; it just reads different numbers.

```
overlays/systembars/
  AndroidManifest.xml      <overlay android:targetPackage="com.android.systemui">
  res/values/config.xml    our replacement values
  res/xml/overlays.xml     explicit target-resource -> our-resource map
  Android.bp               how it ships (built into the AOSP tree)
```

## Why it is allowed to override CarSystemUI

An overlay may only modify a target that opts in via `<overlayable>` — and
CarSystemUI declares none. AOSP's fallback rule then applies: the overlay must
either be **preinstalled on a partition** or **signed with the target's
signature**.

- **Shipping path** — `Android.bp` builds it into the tree, `product_specific`
  installs it to `/product/overlay`. Preinstalled, so the rule is satisfied.
- **Dev path** — the AAOS emulator is a `test-keys` build whose CarSystemUI is
  signed with the *public* AOSP platform key. `build-overlay.sh` signs with that
  same key, which makes it a same-signature overlay, installable with
  `adb install` — no writable `/product`, no reboot. This is a dev shortcut and
  nothing more; a real head unit must never use AOSP test keys.

`/product` on the stock AVD is read-only (`adb remount` fails: bootloader
locked), which is why the signature route is the practical loop here.

## Dev loop

With an AAOS emulator running:

```sh
oem/tools/build-overlay.sh systembars     # compile, sign, install, enable
adb shell pkill -TERM -f com.android.systemui
```

CarSystemUI reads these configs once at startup, so it must be restarted.
To compare against stock:

```sh
adb shell cmd overlay disable --user 0 com.myhondafit.overlay.systembars
adb shell cmd overlay disable --user current com.myhondafit.overlay.systembars
adb shell pkill -TERM -f com.android.systemui
```

Verified on `sdk_gcar_arm64` (AAOS 15, 1408x792): stock shows a top status bar
and a bottom nav bar; with the overlay enabled both are gone and CarLauncher
occupies the full display.

## Gotcha worth remembering

The bar flags are **not independent**. `SystemBarConfigs.init()` runs a set of
consistency checks at startup and *throws* rather than degrading, so a bad
combination crash-loops CarSystemUI and leaves a black screen.

Turning off the top bar alone produced:

```
java.lang.RuntimeException: Top System Bar must be enabled to use
  com.android.systemui.car.notification.TopNotificationPanelViewMediator
```

`config_notificationPanelViewMediator` still named the Top variant. Overriding
it to the base `NotificationPanelViewMediator` (which asserts nothing about
bars) fixed it. Other checks in the same method cover unique bar types,
Z-orders, and keyboard-hide sync — expect to satisfy them as a group when
changing bars.

## Shipping this

In the AOSP tree:

1. Add `MyHondaFitSystemBarsOverlay` to `PRODUCT_PACKAGES`.
2. Mark it enabled and immutable in `/product/overlay/config/config.xml`:
   `<overlay package="com.myhondafit.overlay.systembars" enabled="true" mutable="false"/>`
   Without this it installs disabled and mutable, as it is during development.

## Sources

- [Implement the System UI](https://source.android.com/docs/automotive/hmi/system_ui)
- [Change the value of an app's resources at runtime](https://source.android.com/docs/core/runtime/rros)
- [Troubleshoot runtime resource overlays](https://source.android.com/docs/core/runtime/rro-troubleshoot)
