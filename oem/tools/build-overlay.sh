#!/usr/bin/env bash
#
# Build an OEM RRO from oem/overlays/<name> and load it onto a running AAOS
# emulator. Dev loop only -- the shipping path is Android.bp + PRODUCT_PACKAGES.
#
#   usage: oem/tools/build-overlay.sh [name]   (default: systembars)
#
set -euo pipefail

NAME="${1:-systembars}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC="$ROOT/oem/overlays/$NAME"
OUT="$ROOT/oem/build/$NAME"
KEYS="$ROOT/oem/build/keys"

[ -d "$SRC" ] || { echo "no overlay at $SRC"; exit 1; }

SDK="${ANDROID_HOME:-$(sed -n 's/^sdk\.dir=//p' "$ROOT/local.properties")}"
BT="$SDK/build-tools/$(ls "$SDK/build-tools" | sort -V | tail -1)"
JAR="$SDK/platforms/$(ls "$SDK/platforms" | sort -V | tail -1)/android.jar"
ADB="$SDK/platform-tools/adb"

# The AAOS emulator image is a test-keys build: CarSystemUI is signed with the
# public AOSP platform key. Signing with the same key makes this a
# same-signature overlay, which PackageManager accepts from /data -- so no
# writable /product and no reboot. A real unit would never use these keys.
if [ ! -f "$KEYS/platform.pk8" ]; then
  mkdir -p "$KEYS"
  B=https://android.googlesource.com/platform/build/+/refs/heads/main/target/product/security
  curl -sfL "$B/platform.pk8?format=TEXT"      | base64 -d > "$KEYS/platform.pk8"
  curl -sfL "$B/platform.x509.pem?format=TEXT" | base64 -d > "$KEYS/platform.x509.pem"
fi

rm -rf "$OUT" && mkdir -p "$OUT"
"$BT/aapt2" compile --dir "$SRC/res" -o "$OUT/res.zip"
"$BT/aapt2" link -o "$OUT/unsigned.apk" \
  --manifest "$SRC/AndroidManifest.xml" \
  -I "$JAR" --min-sdk-version 35 --target-sdk-version 35 \
  "$OUT/res.zip"
"$BT/zipalign" -f 4 "$OUT/unsigned.apk" "$OUT/aligned.apk"
"$BT/apksigner" sign --key "$KEYS/platform.pk8" --cert "$KEYS/platform.x509.pem" \
  --out "$OUT/$NAME.apk" "$OUT/aligned.apk"

PKG=$("$BT/aapt2" dump packagename "$OUT/$NAME.apk")
echo "built $PKG"

"$ADB" install -r "$OUT/$NAME.apk"
"$ADB" shell cmd overlay enable --user 0 "$PKG" || true
"$ADB" shell cmd overlay enable --user current "$PKG" || true
"$ADB" shell cmd overlay list "$(sed -n 's/.*targetPackage="\([^"]*\)".*/\1/p' "$SRC/AndroidManifest.xml")" | grep -E "$PKG|^com\." || true
