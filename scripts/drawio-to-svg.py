#!/usr/bin/env python3
"""Convert drawio .drawio XML to high-quality SVG (GPU-free fallback).

Parses drawio style attributes (fillColor, strokeColor, fontSize, fontStyle, etc.)
and generates proper SVG with rounded corners, text styling, and orthogonal edges.
"""

import xml.etree.ElementTree as ET
import html
import re
import sys
import os


def strip_html_tags(s):
    """Remove HTML tags from drawio label text."""
    return re.sub(r'<[^>]+>', '', s)


def parse_style(style_str):
    """Parse drawio style string into a dict of key=value pairs."""
    result = {}
    if not style_str:
        return result
    for part in style_str.split(';'):
        part = part.strip()
        if '=' in part:
            key, val = part.split('=', 1)
            result[key.strip()] = val.strip()
        elif part:
            result[part] = '1'
    return result


def extract_color(style, default='#4a90d9'):
    """Get fill color from style dict."""
    return style.get('fillColor', default)


def hex_to_rgb(hex_color):
    """Convert hex color to RGB tuple."""
    h = hex_color.lstrip('#')
    if len(h) == 6:
        return tuple(int(h[i:i+2], 16) for i in (0, 2, 4))
    return (100, 100, 100)


def luminance(hex_color):
    """Calculate relative luminance of a color."""
    r, g, b = [x / 255.0 for x in hex_to_rgb(hex_color)]
    r = r / 12.92 if r <= 0.03928 else ((r + 0.055) / 1.055) ** 2.4
    g = g / 12.92 if g <= 0.03928 else ((g + 0.055) / 1.055) ** 2.4
    b = b / 12.92 if b <= 0.03928 else ((b + 0.055) / 1.055) ** 2.4
    return 0.2126 * r + 0.7152 * g + 0.0722 * b


def text_color_for_bg(bg_color):
    """Return white or black text based on background luminance."""
    return '#FFFFFF' if luminance(bg_color) < 0.5 else '#424242'


def orthogonal_path(x1, y1, x2, y2):
    """Generate orthogonal (L-shaped) path between two points."""
    # Determine which edge to use
    dx = abs(x2 - x1)
    dy = abs(y2 - y1)

    if dy > dx:
        # Vertical-first: from bottom of src to top of tgt
        mid_y = (y1 + y2) / 2
        return [(x1, y1), (x1, mid_y), (x2, mid_y), (x2, y2)]
    else:
        # Horizontal-first
        mid_x = (x1 + x2) / 2
        return [(x1, y1), (mid_x, y1), (mid_x, y2), (x2, y2)]


def main():
    if len(sys.argv) < 3:
        print(f"Usage: {sys.argv[0]} input.drawio output.svg")
        sys.exit(1)

    input_path = sys.argv[1]
    output_path = sys.argv[2]

    tree = ET.parse(input_path)
    root = tree.getroot()
    cells = root.findall('.//mxCell')

    gm = root.find('.//mxGraphModel')
    w = int(gm.get('pageWidth', 1200)) if gm is not None else 1200
    h = int(gm.get('pageHeight', 900)) if gm is not None else 900

    # ---- Collect all cells ----
    shapes = []       # vertex cells with visual shapes
    texts = []        # text-only cells
    edges = []        # edge cells
    groups = []       # large bg cells (used as grouping backgrounds)

    for cell in cells:
        cid = cell.get('id', '')
        style_str = cell.get('style', '')
        style = parse_style(style_str)
        geo = cell.find('mxGeometry')

        if geo is None:
            continue

        x = float(geo.get('x', 0) or 0)
        y = float(geo.get('y', 0) or 0)
        bw = float(geo.get('width', 0) or 0)
        bh = float(geo.get('height', 0) or 0)

        # Edge cells
        src = cell.get('source')
        tgt = cell.get('target')
        if src and tgt:
            edges.append({
                'id': cid, 'source': src, 'target': tgt,
                'style': style, 'style_str': style_str,
                'label': strip_html_tags(cell.get('value', '')),
            })
            continue

        # Vertex cells
        if cell.get('vertex') == '1':
            label = cell.get('value', '')
            is_text = style.get('html') == '1' and (
                'text;' in style_str or style_str.startswith('text')
            )
            is_empty = not label.strip()

            if is_empty and bw > 100 and bh > 50:
                # Empty large rectangle = group background
                groups.append({
                    'id': cid, 'x': x, 'y': y, 'w': bw, 'h': bh,
                    'style': style, 'style_str': style_str,
                    'label': '',
                })
            elif is_text:
                texts.append({
                    'id': cid, 'x': x, 'y': y, 'w': bw, 'h': bh,
                    'style': style, 'style_str': style_str,
                    'label': strip_html_tags(label),
                })
            else:
                shapes.append({
                    'id': cid, 'x': x, 'y': y, 'w': bw, 'h': bh,
                    'style': style, 'style_str': style_str,
                    'label': strip_html_tags(label),
                })

    # ---- Build node position map for edge routing ----
    node_map = {}
    for s in shapes:
        node_map[s['id']] = {'x': s['x'], 'y': s['y'], 'w': s['w'], 'h': s['h']}
    # Also include group positions for edges that target groups
    for g in groups:
        node_map[g['id']] = {'x': g['x'], 'y': g['y'], 'w': g['w'], 'h': g['h']}

    # ---- Build SVG ----
    svg_parts = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}">',
        '<defs>',
        '  <marker id="ah" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">',
        '    <polygon points="0 0, 10 3.5, 0 7" fill="#424242"/>',
        '  </marker>',
        '</defs>',
    ]

    # 1. Draw group backgrounds first (behind everything)
    for g in sorted(groups, key=lambda o: -o['w'] * o['h']):
        fill = g['style'].get('fillColor', '#f5f5f5')
        stroke = g['style'].get('strokeColor', '#9e9e9e')
        rx = 10 if 'rounded' in g['style'] else 0
        if rx:
            svg_parts.append(
                f'  <rect x="{g["x"]:.0f}" y="{g["y"]:.0f}" '
                f'width="{g["w"]:.0f}" height="{g["h"]:.0f}" '
                f'rx="{rx}" fill="{fill}" stroke="{stroke}" stroke-width="1" stroke-dasharray="5,5"/>')
        else:
            svg_parts.append(
                f'  <rect x="{g["x"]:.0f}" y="{g["y"]:.0f}" '
                f'width="{g["w"]:.0f}" height="{g["h"]:.0f}" '
                f'fill="{fill}" stroke="{stroke}" stroke-width="1"/>')

    # 2. Draw shape nodes
    for s in shapes:
        fill = s['style'].get('fillColor', '#4a90d9')
        stroke = s['style'].get('strokeColor', '#2c3e50')
        stroke_w = s['style'].get('strokeWidth', '1')
        font_color = s['style'].get('fontColor', text_color_for_bg(fill))
        font_size = s['style'].get('fontSize', '11')
        is_bold = 'fontStyle' in s['style'] and '1' in s['style'].get('fontStyle', '')
        rx = 5 if 'rounded' in s['style'] else 0

        # Rounded rect
        if rx:
            svg_parts.append(
                f'  <rect x="{s["x"]:.0f}" y="{s["y"]:.0f}" '
                f'width="{s["w"]:.0f}" height="{s["h"]:.0f}" '
                f'rx="{rx}" fill="{fill}" stroke="{stroke}" stroke-width="{stroke_w}"/>')
        else:
            svg_parts.append(
                f'  <rect x="{s["x"]:.0f}" y="{s["y"]:.0f}" '
                f'width="{s["w"]:.0f}" height="{s["h"]:.0f}" '
                f'fill="{fill}" stroke="{stroke}" stroke-width="{stroke_w}"/>')

        # Label text centered in the shape
        if s['label']:
            fw = 'bold' if is_bold else 'normal'
            mid_x = s['x'] + s['w'] / 2
            mid_y = s['y'] + s['h'] / 2
            # Split multi-line labels
            lines = s['label'].split('\n') if '\n' in s['label'] else [s['label']]
            line_h = int(font_size) + 2
            start_y = mid_y - (len(lines) - 1) * line_h / 2
            for i, line in enumerate(lines):
                svg_parts.append(
                    f'  <text x="{mid_x:.0f}" y="{start_y + i * line_h:.0f}" '
                    f'font-family="Arial, sans-serif" font-size="{font_size}" '
                    f'fill="{font_color}" text-anchor="middle" dominant-baseline="middle" '
                    f'font-weight="{fw}">{html.escape(line[:40])}</text>')

    # 3. Draw text cells (labels, titles, legends)
    for t in texts:
        font_color = t['style'].get('fontColor', '#000000')
        font_size = t['style'].get('fontSize', '12')
        is_bold = 'fontStyle' in t['style'] and '1' in t['style'].get('fontStyle', '')
        align = t['style'].get('align', 'left')
        valign = t['style'].get('valign', 'middle')

        tx = t['x']
        if align == 'center':
            tx = t['x'] + t['w'] / 2
        elif align == 'right':
            tx = t['x'] + t['w']

        ty = t['y']
        if valign == 'middle':
            ty = t['y'] + t['h'] / 2
        elif valign == 'bottom':
            ty = t['y'] + t['h']

        fw = 'bold' if is_bold else 'normal'
        svg_parts.append(
            f'  <text x="{tx:.0f}" y="{ty:.0f}" '
            f'font-family="Arial, sans-serif" font-size="{font_size}" '
            f'fill="{font_color}" text-anchor="{align}" font-weight="{fw}" '
            f'dominant-baseline="{valign}">{html.escape(t['label'][:60])}</text>')

    # 4. Draw edges
    for e in edges:
        src_id = e['source']
        tgt_id = e['target']
        if src_id not in node_map or tgt_id not in node_map:
            continue

        src = node_map[src_id]
        tgt = node_map[tgt_id]

        style = e['style']
        stroke_color = style.get('strokeColor', '#424242')
        stroke_width = style.get('strokeWidth', '1.5')

        # For orthogonal edges: connect bottom of src to top of tgt
        x1 = src['x'] + src['w'] / 2
        y1 = src['y'] + src['h']
        x2 = tgt['x'] + tgt['w'] / 2
        y2 = tgt['y']

        # If target is above source, reverse
        if y2 > y1:
            x1 = src['x'] + src['w'] / 2
            y1 = src['y'] + src['h']
            x2 = tgt['x'] + tgt['w'] / 2
            y2 = tgt['y']

        points = orthogonal_path(x1, y1, x2, y2)
        path_d = 'M ' + ' L '.join(f'{px:.0f},{py:.0f}' for px, py in points)

        is_dashed = style.get('dashed') == '1'
        stroke_dash = 'stroke-dasharray="5,5" ' if is_dashed else ''

        svg_parts.append(
            f'  <path d="{path_d}" '
            f'stroke="{stroke_color}" stroke-width="{stroke_width}" '
            f'fill="none" marker-end="url(#ah)" {stroke_dash}stroke-linejoin="round"/>')

        # Edge label
        if e['label']:
            # Position at midpoint of path
            mid_idx = len(points) // 2
            mx, my = points[mid_idx]
            svg_parts.append(
                f'  <rect x="{mx - 30:.0f}" y="{my - 10:.0f}" '
                f'width="60" height="20" fill="#ffffff" stroke="none" rx="3"/>')
            svg_parts.append(
                f'  <text x="{mx:.0f}" y="{my + 1:.0f}" '
                f'font-family="Arial, sans-serif" font-size="10" '
                f'fill="#424242" text-anchor="middle" dominant-baseline="middle">'
                f'{html.escape(e["label"][:25])}</text>')

    svg_parts.append('</svg>')

    os.makedirs(os.path.dirname(output_path) or '.', exist_ok=True)
    with open(output_path, 'w') as f:
        f.write('\n'.join(svg_parts))
    print(f"SVG written: {output_path} ({os.path.getsize(output_path)} bytes)")


if __name__ == '__main__':
    main()
