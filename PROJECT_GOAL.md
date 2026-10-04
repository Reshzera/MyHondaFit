# MyHondaFit — Project Goal

> Read this first in any new conversation. It defines what this repo is and is not.

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

## Open questions (to resolve as the project moves)

- Target hardware for the head unit (SBC + display, or replacing an existing
  unit's board).
- Vehicle integration path: OBD-II, CAN bus tap, Honda-specific buses.
- Base platform choice: minimal AOSP with a custom system UI vs. Linux +
  compositor + custom shell.
- Boot/shutdown behavior tied to ignition, and persistence across power cuts.
