#!/usr/bin/env bash
set -euo pipefail

# Convert all JPEGs in a drawable folder to WebP at high quality (q=95),
# keeping dimensions, then remove the original JPEGs only on success.
#
# Usage:
#   scripts/convert_drawable_to_webp.sh [drawable_dir]
#
# Default drawable_dir: app/src/main/res/drawable-nodpi

dir=${1:-app/src/main/res/drawable-nodpi}

if ! command -v cwebp >/dev/null 2>&1; then
  echo "Error: cwebp not found. Install libwebp (e.g., 'brew install webp')." >&2
  exit 1
fi

if [ ! -d "$dir" ]; then
  echo "Error: directory not found: $dir" >&2
  exit 1
fi

echo "Converting JPEGs in: $dir"

# Backup JPEGs before conversion
ts=$(date +%Y%m%d_%H%M%S)
backup_dir="$dir/_backup_pre_webp_$ts"
mkdir -p "$backup_dir"

shopt -s nullglob
jpegs=("$dir"/*.jpg "$dir"/*.jpeg "$dir"/*.JPG "$dir"/*.JPEG)
if [ ${#jpegs[@]} -eq 0 ]; then
  echo "No JPEG files found in $dir" >&2
  exit 0
fi

cp -p -- "${jpegs[@]}" "$backup_dir"
echo "Backup saved to: $backup_dir"

converted=0
failed=0

for f in "${jpegs[@]}"; do
  base="${f%.*}"
  out="$base.webp"
  if [ -f "$out" ]; then
    echo "Skip (exists): $(basename "$out")"
    continue
  fi
  if cwebp -q 95 -m 6 -mt -af -metadata icc -- "$f" -o "$out" >/dev/null 2>&1; then
    echo "Created: $(basename "$out")"
    rm -f -- "$f"
    converted=$((converted+1))
  else
    echo "Failed: $(basename "$f")" >&2
    failed=$((failed+1))
  fi
done

echo "Done. Converted: $converted, Failed: $failed"

exit 0

