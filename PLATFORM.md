# Platform & Implementation Path

> Companion to `PROJECT_GOAL.md`. That document says *what* we are building and
> what we refuse to build. This one records *how* it can be built, and the
> tradeoffs behind the base-platform choice.

## The key insight: AAOS is designed to be taken over

The natural assumption is that owning the whole screen on an Android-based
device means fighting Android. It does not. Android Automotive OS (AAOS) is
built on the premise that the carmaker — not Google — owns everything the
driver sees. Google ships a reference HMI and *expects OEMs to discard it*.

That makes this an **OEM system-image project**, not app development. We are
not writing an app that runs on a head unit; we *are* the system UI.

## The OEM levers

Ordered roughly by how much Android identity each one removes.

### 1. Drop the AOSP automotive look at build time

`packages/services/Car/car_product/overlay` is the build-time overlay that
*produces* the stock AAOS appearance. The AOSP docs state explicitly that OEMs
may exclude it and supply their own. Removing it from the product makefile and
providing our own `PRODUCT_PACKAGE_OVERLAYS` is the single largest lever.

### 2. Disable the system bars entirely

CarSystemUI's bars are config-gated:

- `config_enableTopSystemBar`
- `config_enableLeftSystemBar`
- `config_enableBottomSystemBar`
- `config_enableRightSystemBar`

All `false` means no status bar, no nav bar, and no notification-shade handle —
directly satisfying the "no status bar, no notification shade" constraint in
`PROJECT_GOAL.md`.

> **Verified.** Done for real on an AAOS 15 emulator (`sdk_gcar_arm64`) as an
> RRO against `com.android.systemui`: both stock bars disappear and the launcher
> takes the full display. Source, build script and the full writeup are in
> `oem/`. Two corrections to what the docs say:
>
> - `config_enableTopSystemBar` exists and works. The "Implement the System UI"
>   page claims the status bar cannot be disabled by config flag; that is stale
>   for AAOS 15.
> - The flags are **not independent**. `SystemBarConfigs.init()` throws at
>   startup on an inconsistent combination rather than degrading, which
>   crash-loops CarSystemUI to a black screen. Disabling the top bar also
>   requires overriding `config_notificationPanelViewMediator`, which otherwise
>   still names `TopNotificationPanelViewMediator`.

For *logic* rather than resources, the two documented extension points are:

- `config_statusBarComponent` — system bar and notification behavior
- `config_systemUIFactoryComponent` — also covers volume UI and keyguard

Both are subclass-and-point-at-it hooks, so we extend rather than patch AOSP
and stay upgradable.

> **Path, resolved:** `packages/apps/Car/SystemUI` is correct for AAOS 15
> (older trees used `frameworks/base/packages/CarSystemUI`). On device the APK
> is `/system_ext/priv-app/CarSystemUI/CarSystemUI.apk`, package
> `com.android.systemui`. Re-check if a different branch is chosen.

### 3. Our shell replaces CarLauncher

Remove `CarLauncher` from `PRODUCT_PACKAGES` and preinstall our own privileged
system app as the **sole** `CATEGORY_HOME` activity. That becomes the
boot-to-shutdown surface. Combined with a custom bootanimation and no setup
wizard, the Android identity is effectively gone from the user's view.

### 4. car-ui-lib / RROs / OEM design tokens — mostly skippable

This machinery exists so third-party and Google apps adopt an OEM's look
*without being recompiled* (RROs keyed on `ro.product.sku`, tokens as a
Material implementation). It only matters if foreign apps run on the unit. If
every surface is our own code, skip it.

### 5. Scalable UI (AAOS 16+) — optional for us

XML-driven panels / variants / transitions over task views for multi-panel
layouts, with window management and Z-order handled by the framework. Docs
recommend it for all new programs and note AAOS 16+ **does not pass CTS with it
disabled** — but CTS is irrelevant here: we are not shipping GAS, so no
certification applies. Worth reading if we want a persistent map panel with
cards layered over it.

### 6. VHAL is the real prize

This resolves the vehicle-integration question in `PROJECT_GOAL.md`. Rather
than parsing OBD-II inside the UI, write a Vehicle HAL that reads the CAN/OBD
tap and publishes vehicle properties; the UI consumes them via
`CarPropertyManager`. Clean boundary, and it comes with the automotive audio
policy (focus, ducking, zones) and the Bluetooth stack for free.

## The honest tradeoff

No Honda Fit generation ships an AAOS board, so this route means an SBC behind
the dash — and **board bringup is where the cost lives**: touch, audio routing,
backup camera, ignition-tied power. AOSP automotive builds run to hundreds of
GB and hours per build.

| | AAOS (OEM system image) | Linux + Wayland compositor + fullscreen shell |
|---|---|---|
| Owns the display | Yes | Yes |
| Non-Android-looking | Yes, with the levers above | Yes, by construction |
| Real media apps (Spotify, YT Music) | Yes | No |
| Car audio policy (focus, ducking, zones) | Free | Build it yourself |
| Bluetooth stack (phone, HFP, A2DP) | Free | Significant work |
| Vehicle data abstraction | VHAL | Roll your own |
| Board bringup cost | High | Moderate |
| Build/iteration cost | Hours, hundreds of GB | Fast |

**Decision rule:** if real media apps in the dash are a requirement, AAOS earns
its cost. If every surface is our own code, Linux + compositor reaches the same
"owns the display, isn't Android-like" endpoint far more cheaply.

## Status

**Not yet decided.** Current leaning is the AAOS OEM path, on the strength of
VHAL, the audio policy, and media-app support. The decision gates on whether
third-party media apps are in scope — see Open Questions in `PROJECT_GOAL.md`.

Either way, the OEM framing is the correct mental model and does not change.

## Sources

- [Implement the System UI](https://source.android.com/docs/automotive/hmi/system_ui)
- [Scalable UI overview](https://source.android.com/docs/automotive/scalableui)
- [Customize apps](https://source.android.com/docs/automotive/hmi/car_ui/customize)
- [Car UI library integration guide](https://source.android.com/docs/automotive/hmi/car_ui)
- [OEM design tokens](https://source.android.com/docs/automotive/unbundled_apps/design-tokens)
- [Change the value of an app's resources at runtime (RROs)](https://source.android.com/docs/core/runtime/rros)
