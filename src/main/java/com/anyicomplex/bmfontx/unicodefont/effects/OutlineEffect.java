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

/** Strokes glyphs with an outline.
 * @author Nathan Sweet
 * @modifier Yi An */
public class OutlineEffect implements ConfigurableEffect {
	private float width = 2;
	private Color color = Color.black;
	private int join = BasicStroke.JOIN_BEVEL;
	private Stroke stroke;

	public OutlineEffect () {
	}

	public OutlineEffect (int width, Color color) {
		this.width = width;
		this.color = color;
	}

	public void draw (BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
		g = (Graphics2D)g.create();
		if (stroke != null)
			g.setStroke(stroke);
		else
			g.setStroke(getStroke());
		g.setColor(color);
		g.draw(glyph.getShape());
		g.dispose();
	}

	public float getWidth () {
		return width;
	}

	/** Sets the width of the outline. The glyphs will need padding so the outline doesn't get clipped. */
	public void setWidth (int width) {
		this.width = width;
	}

	public Color getColor () {
		return color;
	}

	public void setColor (Color color) {
		this.color = color;
	}

	public int getJoin () {
		return join;
	}

	public Stroke getStroke () {
		if (stroke == null) return new BasicStroke(width, BasicStroke.CAP_SQUARE, join);
		return stroke;
	}

	/** Sets the stroke to use for the outline. If this is set, the other outline settings are ignored. */
	public void setStroke (Stroke stroke) {
		this.stroke = stroke;
	}

	/** Sets how the corners of the outline are drawn. This is usually only noticeable at large outline widths.
	 * @param join One of: {@link BasicStroke#JOIN_BEVEL}, {@link BasicStroke#JOIN_MITER}, {@link BasicStroke#JOIN_ROUND} */
	public void setJoin (int join) {
		this.join = join;
	}

	public String toString () {
		return "Outline";
	}

	public Array<Value> getValues () {
		Array<Value> values = new Array<>();
		values.add(EffectUtils.colorValue("Color", color));
		values.add(EffectUtils.floatValue("Width", width, 0.1f, 999, "This setting controls the width of the outline. "
			+ "The glyphs will need padding so the outline doesn't get clipped."));
		values.add(EffectUtils.optionValue("Join", String.valueOf(join),
			new String[][] {{"Bevel", BasicStroke.JOIN_BEVEL + ""}, {"Miter", BasicStroke.JOIN_MITER + ""},
				{"Round", BasicStroke.JOIN_ROUND + ""}},
			"This setting defines how the corners of the outline are drawn. "
				+ "This is usually only noticeable at large outline widths."));
		return values;
	}

	public void setValues (Array<Value> values) {
		for (Value value : values) {
			if (value.getName().equals("Color")) {
				color = (Color) value.getObject();
			} else if (value.getName().equals("Width")) {
				width = (Float) value.getObject();
			} else if (value.getName().equals("Join")) {
				join = Integer.parseInt((String) value.getObject());
			}
		}
	}
}
