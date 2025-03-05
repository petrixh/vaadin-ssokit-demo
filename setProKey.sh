#!/bin/sh
# Define the .vaadin directory path in the user's home directory
VAADIN_DIR="$HOME/.vaadin"

# Create the .vaadin directory if it doesn't exist
mkdir -p "$VAADIN_DIR"

# Define the proKey file path
PROKEY_FILE="$VAADIN_DIR/proKey"

# Check if the proKey file exists and warn the user
if [ -f "$PROKEY_FILE" ]; then
  echo "Warning: The file 'proKey' already exists and will be overwritten."
fi

# Prompt the user for their proKey
echo "Enter your proKey:"
read proKeyInput

# Write the input to the proKey file (this will create or overwrite the file)
echo "$proKeyInput" > "$PROKEY_FILE"

echo "The proKey file has been updated."