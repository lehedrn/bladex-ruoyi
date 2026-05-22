#!/usr/bin/env bash
# Export drawio .drawio files to .svg
# Handles WSL2 GPU limitations with fallback strategies

set -euo pipefail

DRAWIO_DIR="${1:-.}"

export_to_svg() {
    local input="$1"
    local output="${input%.drawio}.svg"

    # Strategy 1: Standard drawio GPU export
    if drawio --no-sandbox -x -f svg -e -b 10 -o "$output" "$input" 2>/dev/null; then
        echo "✅ $input → $output (drawio GPU)"
        return 0
    fi

    # Strategy 2: Drawio with --disable-gpu
    if drawio --no-sandbox --disable-gpu -x -f svg -e -b 10 -o "$output" "$input" 2>/dev/null; then
        echo "✅ $input → $output (drawio no-GPU)"
        return 0
    fi

    # Strategy 3: Python fallback - parse drawio XML to SVG
    local script_dir
    script_dir="$(cd "$(dirname "$0")" && pwd)"
    if python3 "$script_dir/drawio-to-svg.py" "$input" "$output" 2>/dev/null; then
        echo "⚠️ $input → $output (Python fallback, simplified rendering)"
        return 0
    fi

    echo "❌ FAILED: $input"
    return 1
}

# Find and export all .drawio files
count=0
failed=0
for f in "$DRAWIO_DIR"/*.drawio; do
    [ -f "$f" ] || continue
    export_to_svg "$f" || ((failed++))
    ((count++))
done

# Recurse into subdirectories
for dir in "$DRAWIO_DIR"/*/; do
    [ -d "$dir" ] || continue
    for f in "$dir"*.drawio; do
        [ -f "$f" ] || continue
        export_to_svg "$f" || ((failed++))
        ((count++))
    done
done

echo ""
echo "Done: $count processed, $failed failed"
