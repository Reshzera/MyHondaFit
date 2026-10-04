# MyHondaFit — Project Goal

> Read this first in any new conversation. It defines what this repo is and is not.
> For *how* it gets built — the OEM system-image levers and the base-platform
> tradeoff — see `PLATFORM.md`.

## What this is

A **custom car OS / head-unit system for a Honda Fit**.

The target is the *whole* user-facing environment of the in-dash unit: boot into
our own shell, our own interface, our own set of screens. Everything the driver
sees and touches belongs to this project.

## What this is NOT

- **Not an Android app** that runs on top of a stock Android head unit.
- **Not an Android-looking interface.** No launcher grid, no app drawer, no
  status bar, no notification shade, no Material "phone in the dash" feel, no
  recents/back/home navigation model.
- Not Android Auto / CarPlay projection, and not a skin over them.

Android (AOSP) may be used *underneath* as plumbing — kernel, drivers, HALs,
graphics stack — because it is the practical way to get a board with working
touch, audio, and video output. But that is an implementation detail. If a
design decision makes the result feel like an Android device, it is wrong.

This is not a contradiction. Android Automotive OS is explicitly built so the
OEM, not Google, owns the entire user-facing surface: the reference HMI is meant
to be discarded, the system bars can be switched off outright, and our own shell
replaces the launcher. Taking over the screen is a supported path, not a fight
against the platform. The mechanics are in `PLATFORM.md`.

## Design direction

- Single purpose-built UI that owns the display from boot to shutdown.
- Car-first interaction: large targets, glanceable layouts, usable without
  looking, safe while driving.
- Fixed, intentional set of surfaces (e.g. drive/cluster view, media, climate,
  navigation, vehicle data) rather than an open-ended app platform.
- No visible "general computer" affordances: no file manager, no settings tree
  that looks like a phone, no third-party app installation flow.

## Current state of the repo

The repo currently holds an **Android Studio / Jetpack Compose scaffold**
(`app/`, Gradle, `MainActivity.kt`). Treat it as a **prototyping harness** — a
fast way to iterate on the interface on a dev machine or tablet — not as the
final shipping form. The eventual deliverable is a system image / system-level
UI, not an APK a user installs.

When the prototype and the OS direction conflict, the OS direction wins.

Concretely, that system image's home surface is our own privileged system app,
installed as the sole home activity.

## Open questions (to resolve as the project moves)

- Target hardware for the head unit (SBC + display, or replacing an existing
  unit's board). No Fit generation ships an AAOS board, so this means an SBC
  behind the dash either way, and board bringup is the dominant cost.
- **Are third-party media apps (Spotify, YouTube Music) in scope?** This is now
  the question that decides the base platform — see `PLATFORM.md`.
- Base platform choice: AAOS as an OEM system image vs. Linux + compositor +
  custom shell. Researched and written up in `PLATFORM.md`; current leaning is
  AAOS, decision still open and gated on the question above.
- Vehicle integration path: OBD-II, CAN bus tap, Honda-specific buses. On the
  AAOS path the shape of this is settled — a Vehicle HAL publishes properties
  and the UI reads them via `CarPropertyManager`, so no vehicle parsing lives in
  the UI layer. The open part is purely which physical bus we tap.
- Boot/shutdown behavior tied to ignition, and persistence across power cuts.
