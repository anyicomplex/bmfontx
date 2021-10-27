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

package com.anyicomplex.bmfontx.unicodefont;

import com.anyicomplex.bmfontx.unicodefont.effects.ColorEffect;
import com.anyicomplex.bmfontx.unicodefont.effects.Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.utils.Array;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Stores a number of glyphs on a single texture.
 * @author Nathan Sweet
 * @modifier Yi An */
public class GlyphPage {
	private final UnicodeFont unicodeFont;
	private final int pageWidth, pageHeight;
	private final Texture texture;
	private final List<Glyph> pageGlyphs = new ArrayList<>(32);
	private final List<String> hashes = new ArrayList<>(32);
	Array<Row> rows = new Array<>();

	/** @param pageWidth The width of the backing texture.
	 * @param pageHeight The height of the backing texture. */
	GlyphPage (UnicodeFont unicodeFont, int pageWidth, int pageHeight) {
		this.unicodeFont = unicodeFont;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;

		texture = new Texture(pageWidth, pageHeight, Format.RGBA8888);
		rows.add(new Row());
	}

	/** Loads glyphs to the backing texture and sets the image on each loaded glyph. Loaded glyphs are removed from the list.
	 *
	 * If this page already has glyphs and maxGlyphsToLoad is -1, then this method will return 0 if all the new glyphs don't fit.
	 * This reduces texture binds when drawing since glyphs loaded at once are typically displayed together.
	 * @param glyphs The glyphs to load.
	 * @param maxGlyphsToLoad This is the maximum number of glyphs to load from the list. Set to -1 to attempt to load all the
	 *           glyphs.
	 * @return The number of glyphs that were actually loaded. */
	int loadGlyphs (List<Glyph> glyphs, int maxGlyphsToLoad) {
		GL11.glColor4f(1, 1, 1, 1);
		texture.bind();

		int loadedCount = 0;
		for (Iterator<Glyph> iter = glyphs.iterator(); iter.hasNext();) {
			Glyph glyph = iter.next();
			int width = Math.min(MAX_GLYPH_SIZE, glyph.getWidth());
			int height = Math.min(MAX_GLYPH_SIZE, glyph.getHeight());
			if (width == 0 || height == 0)
				pageGlyphs.add(glyph);
			else {
				Row bestRow = null;
				// Fit in any row before the last.
				for (int ii = 0, nn = rows.size - 1; ii < nn; ii++) {
					Row row = rows.get(ii);
					if (row.x + width >= pageWidth) continue;
					if (row.y + height >= pageHeight) continue;
					if (height > row.height) continue;
					if (bestRow == null || row.height < bestRow.height) bestRow = row;
				}
				if (bestRow == null) {
					// Fit in last row, increasing height.
					Row row = rows.peek();
					if (row.y + height >= pageHeight) continue;
					if (row.x + width < pageWidth) {
						row.height = Math.max(row.height, height);
						bestRow = row;
					} else if (row.y + row.height + height < pageHeight) {
						// Fit in new row.
						bestRow = new Row();
						bestRow.y = row.y + row.height;
						bestRow.height = height;
						rows.add(bestRow);
					}
				}
				if (bestRow == null) continue;

				if (renderGlyph(glyph, bestRow.x, bestRow.y, width, height)) bestRow.x += width;
			}

			iter.remove();
			loadedCount++;
			if (loadedCount == maxGlyphsToLoad) break;

		}

		return loadedCount;
	}

	static class Row {
		int x, y, height;
	}

	/** Loads a single glyph to the backing texture, if it fits. */
	private boolean renderGlyph (Glyph glyph, int pageX, int pageY, int width, int height) {
		scratchGraphics.setComposite(AlphaComposite.Clear);
		scratchGraphics.fillRect(0, 0, MAX_GLYPH_SIZE, MAX_GLYPH_SIZE);
		scratchGraphics.setComposite(AlphaComposite.SrcOver);

		ByteBuffer glyphPixels = scratchByteBuffer;
		int format;
		if (unicodeFont.getRenderType() == UnicodeFont.RenderType.FreeType && unicodeFont.bitmapFont != null) {
			BitmapFontData data = unicodeFont.bitmapFont.getData();
			BitmapFont.Glyph g = data.getGlyph((char)glyph.getCodePoint());
			Pixmap fontPixmap = unicodeFont.bitmapFont.getRegions().get(g.page).getTexture().getTextureData().consumePixmap();

			int fontWidth = fontPixmap.getWidth();
			int padTop = unicodeFont.getPaddingTop(), padBottom = unicodeFont.getPaddingBottom();
			int padLeftBytes = unicodeFont.getPaddingLeft() * 4;
			int padXBytes = padLeftBytes + unicodeFont.getPaddingRight() * 4;
			int glyphRowBytes = width * 4, fontRowBytes = g.width * 4;

			ByteBuffer fontPixels = fontPixmap.getPixels();
			byte[] row = new byte[glyphRowBytes];
			glyphPixels.position(0);
			for (int i = 0; i < padTop; i++)
				glyphPixels.put(row);
			glyphPixels.position((height - padBottom) * glyphRowBytes);
			for (int i = 0; i < padBottom; i++)
				glyphPixels.put(row);
			glyphPixels.position(padTop * glyphRowBytes);
			for (int y = 0, n = g.height; y < n; y++) {
				fontPixels.position(((g.srcY + y) * fontWidth + g.srcX) * 4);
				fontPixels.get(row, padLeftBytes, fontRowBytes);
				glyphPixels.put(row);
			}
			fontPixels.position(0);
			glyphPixels.position(height * glyphRowBytes);
			glyphPixels.flip();
			format = GL11.GL_RGBA;
		} else {
			// Draw the glyph to the scratch image using Java2D.
			if (unicodeFont.getRenderType() == UnicodeFont.RenderType.Native) {
				for (Effect o : unicodeFont.getEffects()) {
					if (o instanceof ColorEffect) scratchGraphics.setColor(((ColorEffect) o).getColor());
				}
				scratchGraphics.setColor(java.awt.Color.white);
				scratchGraphics.setFont(unicodeFont.getFont());
				scratchGraphics.drawString("" + (char)glyph.getCodePoint(), 0, unicodeFont.getAscent());
			} else if (unicodeFont.getRenderType() == UnicodeFont.RenderType.Java) {
				scratchGraphics.setColor(java.awt.Color.white);
				for (Effect o : unicodeFont.getEffects())
					o.draw(scratchImage, scratchGraphics, unicodeFont, glyph);
				glyph.setShape(null); // The shape will never be needed again.
			}

			width = Math.min(width, texture.getWidth());
			height = Math.min(height, texture.getHeight());

			WritableRaster raster = scratchImage.getRaster();
			int[] row = new int[width];
			for (int y = 0; y < height; y++) {
				raster.getDataElements(0, y, width, 1, row);
				scratchIntBuffer.put(row);
			}
			format = GL12.GL_BGRA;
		}

		// Simple deduplication, doesn't work across pages of course.
		String hash = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(glyphPixels);
			BigInteger bigInt = new BigInteger(1, md.digest());
			hash = bigInt.toString(16);
		} catch (NoSuchAlgorithmException ignored) {
		}
		scratchByteBuffer.clear();
		scratchIntBuffer.clear();

		try {
			for (int i = 0, n = hashes.size(); i < n; i++) {
				String other = hashes.get(i);
				if (other.equals(hash)) {
					Glyph dupe = pageGlyphs.get(i);
					glyph.setTexture(dupe.texture, dupe.u, dupe.v, dupe.u2, dupe.v2);
					return false;
				}
			}
		} finally {
			hashes.add(hash);
			pageGlyphs.add(glyph);
		}

		Gdx.gl.glTexSubImage2D(texture.glTarget, 0, pageX, pageY, width, height, format, GL11.GL_UNSIGNED_BYTE, glyphPixels);

		float u = pageX / (float)texture.getWidth();
		float v = pageY / (float)texture.getHeight();
		float u2 = (pageX + width) / (float)texture.getWidth();
		float v2 = (pageY + height) / (float)texture.getHeight();
		glyph.setTexture(texture, u, v, u2, v2);

		return true;
	}

	/** Returns the glyphs stored on this page. */
	public List<Glyph> getGlyphs () {
		return pageGlyphs;
	}

	/** Returns the backing texture for this page. */
	public Texture getTexture () {
		return texture;
	}

	public static final int MAX_GLYPH_SIZE = 256;

	private static final ByteBuffer scratchByteBuffer = ByteBuffer.allocateDirect(MAX_GLYPH_SIZE * MAX_GLYPH_SIZE * 4);

	static {
		scratchByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}

	private static final IntBuffer scratchIntBuffer = scratchByteBuffer.asIntBuffer();

	private static final BufferedImage scratchImage = new BufferedImage(MAX_GLYPH_SIZE, MAX_GLYPH_SIZE, BufferedImage.TYPE_INT_ARGB);
	static Graphics2D scratchGraphics = (Graphics2D)scratchImage.getGraphics();

	static {
		scratchGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		scratchGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	static public FontRenderContext renderContext = scratchGraphics.getFontRenderContext();
}
