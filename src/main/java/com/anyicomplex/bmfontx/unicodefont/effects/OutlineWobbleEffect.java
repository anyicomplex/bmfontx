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

import com.badlogic.gdx.utils.Array;

import java.awt.*;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/** @author Jerry Huxtable
 * @author Nathan Sweet
 * @modifier Yi An */
public class OutlineWobbleEffect extends OutlineEffect {
	float detail = 1;
	float amplitude = 1;

	public OutlineWobbleEffect () {
		setStroke(new WobbleStroke());
	}

	public OutlineWobbleEffect (int width, Color color) {
		super(width, color);
	}

	public String toString () {
		return "Outline (Wobble)";
	}

	public Array<Value> getValues () {
		Array<Value> values = super.getValues();
		values.removeIndex(2); // Remove "Join".
		values.add(EffectUtils.floatValue("Detail", detail, 1, 50,
			"This setting controls how detailed the outline will be. " + "Smaller numbers cause the outline to have more detail."));
		values.add(EffectUtils.floatValue("Amplitude", amplitude, 0.5f, 50, "This setting controls the amplitude of the outline."));
		return values;
	}

	public void setValues (Array<Value> values) {
		super.setValues(values);
		for (Value value : values) {
			if (value.getName().equals("Detail")) {
				detail = (Float) value.getObject();
			} else if (value.getName().equals("Amplitude")) {
				amplitude = (Float) value.getObject();
			}
		}
	}

	class WobbleStroke implements Stroke {
		private static final float FLATNESS = 1;

		public Shape createStrokedShape (Shape shape) {
			GeneralPath result = new GeneralPath();
			shape = new BasicStroke(getWidth(), BasicStroke.CAP_SQUARE, getJoin()).createStrokedShape(shape);
			PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), FLATNESS);
			float[] points = new float[6];
			float moveX = 0, moveY = 0;
			float lastX = 0, lastY = 0;
			float thisX = 0, thisY = 0;
			int type = 0;
			float next = 0;
			while (!it.isDone()) {
				type = it.currentSegment(points);
				switch (type) {
				case PathIterator.SEG_MOVETO:
					moveX = lastX = randomize(points[0]);
					moveY = lastY = randomize(points[1]);
					result.moveTo(moveX, moveY);
					next = 0;
					break;

				case PathIterator.SEG_CLOSE:
					points[0] = moveX;
					points[1] = moveY;
					// Fall into....

				case PathIterator.SEG_LINETO:
					thisX = randomize(points[0]);
					thisY = randomize(points[1]);
					float dx = thisX - lastX;
					float dy = thisY - lastY;
					float distance = (float)Math.sqrt(dx * dx + dy * dy);
					if (distance >= next) {
						float r = 1.0f / distance;
						while (distance >= next) {
							float x = lastX + next * dx * r;
							float y = lastY + next * dy * r;
							result.lineTo(randomize(x), randomize(y));
							next += detail;
						}
					}
					next -= distance;
					lastX = thisX;
					lastY = thisY;
					break;
				}
				it.next();
			}

			return result;
		}

		private float randomize (float x) {
			return x + (float)Math.random() * amplitude * 2 - 1;
		}
	}
}
