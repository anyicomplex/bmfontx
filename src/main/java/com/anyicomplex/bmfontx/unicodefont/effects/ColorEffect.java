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

/** Makes glyphs a solid color.
 * @author Nathan Sweet
 * @modifier Yi An */
public class ColorEffect implements ConfigurableEffect {
	private Color color = Color.white;

	public ColorEffect () {
	}

	public ColorEffect (Color color) {
		this.color = color;
	}

	public void draw (BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
		g.setColor(color);
		try {
			g.fill(glyph.getShape()); // Java2D fails on some glyph shapes?!
		} catch (Throwable ignored) {
		}
	}

	public Color getColor () {
		return color;
	}

	public void setColor (Color color) {
		if (color == null) throw new IllegalArgumentException("color cannot be null.");
		this.color = color;
	}

	public String toString () {
		return "Color";
	}

	public Array<Value> getValues () {
		Array<Value> values = new Array<>();
		values.add(EffectUtils.colorValue("Color", color));
		return values;
	}

	public void setValues (Array<Value> values) {
		for (Value value : values) {
			if (value.getName().equals("Color")) {
				setColor((Color) value.getObject());
			}
		}
	}
}
