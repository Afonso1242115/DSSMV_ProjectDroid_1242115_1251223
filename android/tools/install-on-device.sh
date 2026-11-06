#!/bin/sh
set -eu
cd "$(dirname "$0")/../.."
printf '%s\n' 'Building and installing Campus L&F on the connected Android device...'
./gradlew :app:installDebug
printf '%s\n' 'Installation completed. Open Campus L&F from the phone launcher.'
