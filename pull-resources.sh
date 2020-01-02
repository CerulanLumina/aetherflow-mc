#!/bin/sh
rsync -rv --include="*/" --include="*.png" --include="*.json" --exclude="*" resource-dev/default/assets/luminality src/main/resources/assets
