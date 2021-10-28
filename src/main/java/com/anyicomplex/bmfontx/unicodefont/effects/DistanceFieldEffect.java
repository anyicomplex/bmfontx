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

import com.anyicomplex.bmfontx.distancefield.DistanceFieldGenerator;
import com.anyicomplex.bmfontx.unicodefont.Glyph;
import com.anyicomplex.bmfontx.unicodefont.UnicodeFont;
import com.badlogic.gdx.utils.Array;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/** A filter to create a distance field. The resulting font can be rendered with a simple custom shader to draw bitmap fonts that
 * remain crisp even under high magnification.
 * 
 * <p>
 * An example of the use of such a font is included in the libgdx test suite under the name {@code BitmapFontDistanceFieldTest}.
 * 
 * @see DistanceFieldGenerator
 * 
 * @author Thomas ten Cate */
public class DistanceFieldEffect implements ConfigurableEffect {
	private Color color = Color.WHITE;
	private int scale = 1;
	private float spread = 1;

	/** Draws the glyph to the given image, upscaled by a factor of {@link #scale}.
	 * 
	 * @param image the image to draw to
	 * @param glyph the glyph to draw */
	private void drawGlyph (BufferedImage image, Glyph glyph) {
		Graphics2D inputG = (Graphics2D)image.getGraphics();
		inputG.setTransform(AffineTransform.getScaleInstance(scale, scale));
		// We don't really want anti-aliasing (we'll discard it anyway),
		// but accurate positioning might improve the result slightly
		inputG.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		inputG.setColor(Color.WHITE);
		inputG.fill(glyph.getShape());
	}

	@Override
	public void draw (BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
		BufferedImage input = new BufferedImage(scale * glyph.getWidth(), scale * glyph.getHeight(),
			BufferedImage.TYPE_BYTE_BINARY);
		drawGlyph(input, glyph);

		DistanceFieldGenerator generator = new DistanceFieldGenerator();
		generator.setColor(color);
		generator.setDownscale(scale);
		// We multiply spread by the scale, so that changing scale will only affect accuracy
		// and not spread in the output image.
		generator.setSpread(scale * spread);
		BufferedImage distanceField = generator.generateDistanceField(input);

		g.drawImage(distanceField, new AffineTransform(), null);
	}

	@Override
	public String toString () {
		return "Distance field";
	}

	@Override
	public Array<Value> getValues () {
		Array<Value> values = new Array<>();
		values.add(EffectUtils.colorValue("Color", color));
		values.add(EffectUtils.intValue("Scale", scale,
			"The distance field is computed from an image larger than the output glyph by this factor. Set this to a higher value for more accuracy, but slower font generation."));
		values.add(EffectUtils.floatValue("Spread", spread, 1.0f, Float.MAX_VALUE,
			"The maximum distance from edges where the effect of the distance field is seen. Set this to about half the width of lines in your output font."));
		return values;
	}

	@Override
	public void setValues (Array<Value> values) {
		for (Value value : values) {
			if ("Color".equals(value.getName())) {
				color = (Color) value.getObject();
			} else if ("Scale".equals(value.getName())) {
				scale = Math.max(1, (Integer) value.getObject());
			} else if ("Spread".equals(value.getName())) {
				spread = Math.max(0, (Float) value.getObject());
			}
		}

	}
}
