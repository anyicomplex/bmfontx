/*
 * Copyright (C) 2021 Yi An
 *
 *     This program is based on the open source of Hiero v5 <https://github.com/libgdx/libgdx/wiki/Hiero>,
 *     powered by Java & libGDX.
 *     This project also using FlatLaf <https://github.com/JFormDesigner/FlatLaf>.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * libGDX Copyright:
 *
 * Copyright 2011 See AUTHORS file.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.anyicomplex.bmfontx.unicodefont.effects;

import com.anyicomplex.bmfontx.unicodefont.Glyph;
import com.anyicomplex.bmfontx.unicodefont.UnicodeFont;
import com.badlogic.gdx.utils.Array;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Paints glyphs with a gradient fill.
 * @author Nathan Sweet
 * @modifier Yi An */
public class GradientEffect implements ConfigurableEffect {
	private Color topColor = Color.cyan, bottomColor = Color.blue;
	private int offset = 0;
	private float scale = 1;
	private boolean cyclic;

	public GradientEffect () {
	}

	public GradientEffect (Color topColor, Color bottomColor, float scale) {
		this.topColor = topColor;
		this.bottomColor = bottomColor;
		this.scale = scale;
	}

	public void draw (BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
		int ascent = unicodeFont.getAscent();
		float height = (ascent) * scale;
		float top = -glyph.getYOffset() + unicodeFont.getDescent() + offset + ascent / 2.0f - height / 2;
		g.setPaint(new GradientPaint(0, top, topColor, 0, top + height, bottomColor, cyclic));
		g.fill(glyph.getShape());
	}

	public Color getTopColor () {
		return topColor;
	}

	public void setTopColor (Color topColor) {
		this.topColor = topColor;
	}

	public Color getBottomColor () {
		return bottomColor;
	}

	public void setBottomColor (Color bottomColor) {
		this.bottomColor = bottomColor;
	}

	public int getOffset () {
		return offset;
	}

	/** Sets the pixel offset to move the gradient up or down. The gradient is normally centered on the glyph. */
	public void setOffset (int offset) {
		this.offset = offset;
	}

	public float getScale () {
		return scale;
	}

	/** Changes the height of the gradient by a percentage. The gradient is normally the height of most glyphs in the font. */
	public void setScale (float scale) {
		this.scale = scale;
	}

	public boolean isCyclic () {
		return cyclic;
	}

	/** If set to true, the gradient will repeat. */
	public void setCyclic (boolean cyclic) {
		this.cyclic = cyclic;
	}

	public String toString () {
		return "Gradient";
	}

	public Array<Value> getValues () {
		Array<Value> values = new Array<>();
		values.add(EffectUtils.colorValue("Top color", topColor));
		values.add(EffectUtils.colorValue("Bottom color", bottomColor));
		values.add(EffectUtils.intValue("Offset", offset,
			"This setting allows you to move the gradient up or down. The gradient is normally centered on the glyph."));
		values.add(EffectUtils.floatValue("Scale", scale, 0, 10, "This setting allows you to change the height of the gradient by a"
			+ "percentage. The gradient is normally the height of most glyphs in the font."));
		values.add(EffectUtils.booleanValue("Cyclic", cyclic, "If this setting is checked, the gradient will repeat."));
		return values;
	}

	public void setValues (Array<Value> values) {
		for (Value value : values) {
			if (value.getName().equals("Top color")) {
				topColor = (Color) value.getObject();
			} else if (value.getName().equals("Bottom color")) {
				bottomColor = (Color) value.getObject();
			} else if (value.getName().equals("Offset")) {
				offset = (Integer) value.getObject();
			} else if (value.getName().equals("Scale")) {
				scale = (Float) value.getObject();
			} else if (value.getName().equals("Cyclic")) {
				cyclic = (Boolean) value.getObject();
			}
		}
	}
}
