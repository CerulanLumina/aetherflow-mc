#!/bin/sh
rsync -rv --include="*/" --include="*.png" --include="*.json" --exclude="*" resource-dev/default/assets/aetherflow src/main/resources/assets
