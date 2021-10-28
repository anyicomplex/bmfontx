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

package com.anyicomplex.bmfontx;

import com.anyicomplex.bmfontx.unicodefont.UnicodeFont;
import com.anyicomplex.bmfontx.unicodefont.effects.ConfigurableEffect;
import com.anyicomplex.bmfontx.unicodefont.effects.Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/** Holds the settings needed to configure a UnicodeFont.
 * @author Nathan Sweet
 * @modifier Yi An */
public class Settings {
	private static final String RENDER_TYPE = "render_type";
	private String fontName = "Arial";
	private int fontSize = 12;
	private boolean bold, italic, mono;
	private float gamma;
	private int paddingTop, paddingLeft, paddingBottom, paddingRight, paddingAdvanceX, paddingAdvanceY;
	private int glyphPageWidth = 512, glyphPageHeight = 512;
	private String glyphText = "";
	private final Array<Effect> effects = new Array<>();
	private boolean nativeRendering;
	private boolean font2Active = false;
	private String font2File = "";
	private int renderType = UnicodeFont.RenderType.FreeType.ordinal();

	public Settings() {
	}

	/** @param bmfontxFileRef The file system or classpath location of the BMFontX settings file. */
	public Settings(String bmfontxFileRef) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.absolute(bmfontxFileRef).read(), StandardCharsets.UTF_8));
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				line = line.trim();
				if (line.length() == 0) continue;
				String[] pieces = line.split("=", 2);
				String name = pieces[0].trim();
				String value = pieces[1];
				if (name.equals("font.name")) {
					fontName = value;
				} else if (name.equals("font.gamma")) {
					gamma = Float.parseFloat(value);
				} else if (name.equals("font.mono")) {
					mono = Boolean.parseBoolean(value);
				} else if (name.equals("font.size")) {
					fontSize = Integer.parseInt(value);
				} else if (name.equals("font.bold")) {
					bold = Boolean.parseBoolean(value);
				} else if (name.equals("font.italic")) {
					italic = Boolean.parseBoolean(value);
				} else if (name.equals("font2.file")) {
					font2File = value;
				} else if (name.equals("font2.use")) {
					font2Active = Boolean.parseBoolean(value);
				} else if (name.equals("pad.top")) {
					paddingTop = Integer.parseInt(value);
				} else if (name.equals("pad.right")) {
					paddingRight = Integer.parseInt(value);
				} else if (name.equals("pad.bottom")) {
					paddingBottom = Integer.parseInt(value);
				} else if (name.equals("pad.left")) {
					paddingLeft = Integer.parseInt(value);
				} else if (name.equals("pad.advance.x")) {
					paddingAdvanceX = Integer.parseInt(value);
				} else if (name.equals("pad.advance.y")) {
					paddingAdvanceY = Integer.parseInt(value);
				} else if (name.equals("glyph.page.width")) {
					glyphPageWidth = Integer.parseInt(value);
				} else if (name.equals("glyph.page.height")) {
					glyphPageHeight = Integer.parseInt(value);
				} else if (name.equals("glyph.native.rendering")) {
					nativeRendering = Boolean.parseBoolean(value);
				} else if (name.equals("glyph.text")) {
					glyphText = value;
				} else if (name.equals(RENDER_TYPE)) {
					renderType = Integer.parseInt(value);
				} else if (name.equals("effect.class")) {
					try {
						effects.add((Effect) Class.forName(value).newInstance());
					} catch (Throwable ex) {
						throw new GdxRuntimeException("Unable to create effect instance: " + value, ex);
					}
				} else if (name.startsWith("effect.")) {
					// Set an effect value on the last added effect.
					name = name.substring(7);
					ConfigurableEffect effect = (ConfigurableEffect)effects.get(effects.size - 1);
					Array<ConfigurableEffect.Value> values = effect.getValues();
					for (ConfigurableEffect.Value effectValue : values) {
						if (effectValue.getName().equals(name)) {
							effectValue.setString(value);
							break;
						}
					}
					effect.setValues(values);
				}
			}
			reader.close();
		} catch (Throwable ex) {
			throw new GdxRuntimeException("Unable to load BMFontX font file: " + bmfontxFileRef, ex);
		}
	}

	/** @see UnicodeFont#getPaddingTop() */
	public int getPaddingTop () {
		return paddingTop;
	}

	/** @see UnicodeFont#setPaddingTop(int) */
	public void setPaddingTop (int paddingTop) {
		this.paddingTop = paddingTop;
	}

	/** @see UnicodeFont#getPaddingLeft() */
	public int getPaddingLeft () {
		return paddingLeft;
	}

	/** @see UnicodeFont#setPaddingLeft(int) */
	public void setPaddingLeft (int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	/** @see UnicodeFont#getPaddingBottom() */
	public int getPaddingBottom () {
		return paddingBottom;
	}

	/** @see UnicodeFont#setPaddingBottom(int) */
	public void setPaddingBottom (int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	/** @see UnicodeFont#getPaddingRight() */
	public int getPaddingRight () {
		return paddingRight;
	}

	/** @see UnicodeFont#setPaddingRight(int) */
	public void setPaddingRight (int paddingRight) {
		this.paddingRight = paddingRight;
	}

	/** @see UnicodeFont#getPaddingAdvanceX() */
	public int getPaddingAdvanceX () {
		return paddingAdvanceX;
	}

	/** @see UnicodeFont#setPaddingAdvanceX(int) */
	public void setPaddingAdvanceX (int paddingAdvanceX) {
		this.paddingAdvanceX = paddingAdvanceX;
	}

	/** @see UnicodeFont#getPaddingAdvanceY() */
	public int getPaddingAdvanceY () {
		return paddingAdvanceY;
	}

	/** @see UnicodeFont#setPaddingAdvanceY(int) */
	public void setPaddingAdvanceY (int paddingAdvanceY) {
		this.paddingAdvanceY = paddingAdvanceY;
	}

	/** @see UnicodeFont#getGlyphPageWidth() */
	public int getGlyphPageWidth () {
		return glyphPageWidth;
	}

	/** @see UnicodeFont#setGlyphPageWidth(int) */
	public void setGlyphPageWidth (int glyphPageWidth) {
		this.glyphPageWidth = glyphPageWidth;
	}

	/** @see UnicodeFont#getGlyphPageHeight() */
	public int getGlyphPageHeight () {
		return glyphPageHeight;
	}

	/** @see UnicodeFont#setGlyphPageHeight(int) */
	public void setGlyphPageHeight (int glyphPageHeight) {
		this.glyphPageHeight = glyphPageHeight;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public String getFontName () {
		return fontName;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public void setFontName (String fontName) {
		this.fontName = fontName;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public int getFontSize () {
		return fontSize;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public void setFontSize (int fontSize) {
		this.fontSize = fontSize;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public boolean isBold () {
		return bold;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public void setBold (boolean bold) {
		this.bold = bold;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public boolean isItalic () {
		return italic;
	}

	/** @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
	 * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean) */
	public void setItalic (boolean italic) {
		this.italic = italic;
	}

	/** @see UnicodeFont#getEffects() */
	public Array<Effect> getEffects () {
		return effects;
	}

	public boolean getNativeRendering () {
		return nativeRendering;
	}

	public void setNativeRendering (boolean nativeRendering) {
		this.nativeRendering = nativeRendering;
	}

	public String getGlyphText () {
		return this.glyphText.replace("\\n", "\n");
	}

	public void setGlyphText (String text) {
		this.glyphText = text.replace("\n", "\\n");
	}

	public String getFont2File () {
		return font2File;
	}

	public void setFont2File (String filename) {
		this.font2File = filename;
	}

	public boolean isFont2Active () {
		return font2Active;
	}

	public void setFont2Active (boolean active) {
		this.font2Active = active;
	}

	public boolean isMono () {
		return mono;
	}

	public void setMono (boolean mono) {
		this.mono = mono;
	}

	public float getGamma () {
		return gamma;
	}

	public void setGamma (float gamma) {
		this.gamma = gamma;
	}

	/** Saves the settings to a file.
	 * @throws IOException if the file could not be saved. */
	public void save (File file) throws IOException {
		PrintStream out = new PrintStream(file, "UTF-8");
		out.println("font.name=" + fontName);
		out.println("font.size=" + fontSize);
		out.println("font.bold=" + bold);
		out.println("font.italic=" + italic);
		out.println("font.gamma=" + gamma);
		out.println("font.mono=" + mono);
		out.println();
		out.println("font2.file=" + font2File);
		out.println("font2.use=" + font2Active);
		out.println();
		out.println("pad.top=" + paddingTop);
		out.println("pad.right=" + paddingRight);
		out.println("pad.bottom=" + paddingBottom);
		out.println("pad.left=" + paddingLeft);
		out.println("pad.advance.x=" + paddingAdvanceX);
		out.println("pad.advance.y=" + paddingAdvanceY);
		out.println();
		out.println("glyph.native.rendering=" + nativeRendering);
		out.println("glyph.page.width=" + glyphPageWidth);
		out.println("glyph.page.height=" + glyphPageHeight);
		out.println("glyph.text=" + glyphText);
		out.println();
		out.println(RENDER_TYPE + "=" + renderType);
		out.println();
		for (Effect item : effects) {
			ConfigurableEffect effect = (ConfigurableEffect) item;
			out.println("effect.class=" + effect.getClass().getName());
			for (ConfigurableEffect.Value value : effect.getValues()) {
				out.println("effect." + value.getName() + "=" + value.getString());
			}
			out.println();
		}
		out.close();
	}

	public void setRenderType (int renderType) {
		this.renderType = renderType;
	}

	public int getRenderType () {
		return renderType;
	}

}
