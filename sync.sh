#!/bin/bash

git add .
git commit -m "Update Project"
git pull --rebase origin main
git push
