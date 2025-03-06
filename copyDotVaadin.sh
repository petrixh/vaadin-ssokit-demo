#!/bin/sh

# Check if the source file exists
if [ -f "/dot-vaadin/proKey" ]; then
  # Ensure the destination directory exists
  mkdir -p "$HOME/.vaadin"
  # Copy the file to the destination
  cp "/dot-vaadin/proKey" "$HOME/.vaadin/proKey"
  echo "proKey copied successfully to $HOME/.vaadin/"
else
  echo "File /dot-vaadin/proKey does not exist."
fi