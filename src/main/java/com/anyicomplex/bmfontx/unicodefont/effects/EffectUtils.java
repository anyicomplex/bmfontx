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

import com.anyicomplex.bmfontx.unicodefont.GlyphPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/** Provides utility methods for effects.
 * @author Nathan Sweet
 * @modifier Yi An */
public class EffectUtils {
	 private static final BufferedImage scratchImage = new BufferedImage(
			 GlyphPage.MAX_GLYPH_SIZE, GlyphPage.MAX_GLYPH_SIZE, BufferedImage.TYPE_INT_ARGB);

	/** Returns an image that can be used by effects as a temp image. */
	static public BufferedImage getScratchImage () {
		Graphics2D g = (Graphics2D)scratchImage.getGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, GlyphPage.MAX_GLYPH_SIZE, GlyphPage.MAX_GLYPH_SIZE);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.white);
		return scratchImage;
	}

	/** Returns a value that represents a color. */
	static public ConfigurableEffect.Value colorValue (String name, Color currentValue) {
		return new DefaultValue(name, EffectUtils.toString(currentValue)) {
			public void showDialog () {
				Color newColor = JColorChooser.showDialog(null, "Choose a color", EffectUtils.fromString(value));
				if (newColor != null) value = EffectUtils.toString(newColor);
			}

			public Object getObject () {
				return EffectUtils.fromString(value);
			}
		};
	}

	/** Returns a value that represents an int. */
	static public ConfigurableEffect.Value intValue (String name, final int currentValue, final String description) {
		return new DefaultValue(name, String.valueOf(currentValue)) {
			public void showDialog () {
				JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentValue, Short.MIN_VALUE, Short.MAX_VALUE, 1));
				if (showValueDialog(spinner, description)) value = String.valueOf(spinner.getValue());
			}

			public Object getObject () {
				return Integer.valueOf(value);
			}
		};
	}

	/** Returns a value that represents a float, from 0 to 1 (inclusive). */
	static public ConfigurableEffect.Value floatValue (String name, final float currentValue, final float min, final float max,
													   final String description) {
		return new DefaultValue(name, String.valueOf(currentValue)) {
			public void showDialog () {
				JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentValue, min, max, 0.1f));
				if (showValueDialog(spinner, description)) value = String.valueOf(((Double)spinner.getValue()).floatValue());
			}

			public Object getObject () {
				return Float.valueOf(value);
			}
		};
	}

	/** Returns a value that represents a boolean. */
	static public ConfigurableEffect.Value booleanValue (String name, final boolean currentValue, final String description) {
		return new DefaultValue(name, String.valueOf(currentValue)) {
			public void showDialog () {
				JCheckBox checkBox = new JCheckBox();
				checkBox.setSelected(currentValue);
				if (showValueDialog(checkBox, description)) value = String.valueOf(checkBox.isSelected());
			}

			public Object getObject () {
				return Boolean.valueOf(value);
			}
		};
	}

	/** Returns a value that represents a fixed number of options. All options are strings.
	 * @param options The first array has an entry for each option. Each entry is either a String[1] that is both the display value
	 *           and actual value, or a String[2] whose first element is the display value and second element is the actual
	 *           value. */
	static public ConfigurableEffect.Value optionValue (String name, final String currentValue, final String[][] options, final String description) {
		return new DefaultValue(name, currentValue) {
			public void showDialog () {
				int selectedIndex = -1;
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
				for (int i = 0; i < options.length; i++) {
					model.addElement(options[i][0]);
					if (getValue(i).equals(currentValue)) selectedIndex = i;
				}
				JComboBox comboBox = new JComboBox(model);
				comboBox.setSelectedIndex(selectedIndex);
				if (showValueDialog(comboBox, description)) value = getValue(comboBox.getSelectedIndex());
			}

			private String getValue (int i) {
				if (options[i].length == 1) return options[i][0];
				return options[i][1];
			}

			public String toString () {
				for (int i = 0; i < options.length; i++)
					if (getValue(i).equals(value)) return options[i][0];
				return "";
			}

			public Object getObject () {
				return value;
			}
		};
	}

	/** Converts a color to a string. */
	static public String toString (Color color) {
		if (color == null) throw new IllegalArgumentException("color cannot be null.");
		String r = Integer.toHexString(color.getRed());
		if (r.length() == 1) r = "0" + r;
		String g = Integer.toHexString(color.getGreen());
		if (g.length() == 1) g = "0" + g;
		String b = Integer.toHexString(color.getBlue());
		if (b.length() == 1) b = "0" + b;
		return r + g + b;
	}

	/** Converts a string to a color. */
	static public Color fromString (String rgb) {
		if (rgb == null || rgb.length() != 6) return Color.white;
		return new Color(Integer.parseInt(rgb.substring(0, 2), 16), Integer.parseInt(rgb.substring(2, 4), 16),
			Integer.parseInt(rgb.substring(4, 6), 16));
	}

	/** Provides generic functionality for an effect's configurable value. */
	static private abstract class DefaultValue implements ConfigurableEffect.Value {
		String value;
		String name;

		public DefaultValue (String name, String value) {
			this.value = value;
			this.name = name;
		}

		public void setString (String value) {
			this.value = value;
		}

		public String getString () {
			return value;
		}

		public String getName () {
			return name;
		}

		public String toString () {
			return value == null ? "" : value;
		}

		public boolean showValueDialog (final JComponent component, String description) {
			ValueDialog dialog = new ValueDialog(component, name, description);
			dialog.setMinimumSize(dialog.getSize());
			dialog.setLocationRelativeTo(null);
			EventQueue.invokeLater(new Runnable() {
				public void run () {
					JComponent focusComponent = component;
					dialog.setMinimumSize(new Dimension(
							Math.max(dialog.getWidth(), (focusComponent.getWidth() + focusComponent.getBounds().width)),
							dialog.getHeight() + focusComponent.getHeight() + focusComponent.getBounds().height
					));
					if (focusComponent instanceof JSpinner)
						focusComponent = ((JSpinner.DefaultEditor)((JSpinner)component).getEditor()).getTextField();
					focusComponent.requestFocusInWindow();
				}
			});
			dialog.setVisible(true);
			return dialog.okPressed;
		}
	}

	/** Provides generic functionality for a dialog to configure a value. */
	static private class ValueDialog extends JDialog {
		public boolean okPressed = false;

		public ValueDialog (JComponent component, String name, String description) {
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setLayout(new GridBagLayout());
			setModal(true);

			if (component instanceof JSpinner)
				((JSpinner.DefaultEditor)((JSpinner)component).getEditor()).getTextField().setColumns(4);

			JPanel descriptionPanel = new JPanel();
			descriptionPanel.setLayout(new GridBagLayout());
			getContentPane().add(descriptionPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			descriptionPanel.setBackground(Color.white);
			descriptionPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
			{
				JTextArea descriptionText = new JTextArea(description);
				descriptionPanel.add(descriptionText, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
				descriptionText.setWrapStyleWord(true);
				descriptionText.setLineWrap(true);
				descriptionText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
				descriptionText.setEditable(false);
			}

			JPanel panel = new JPanel();
			getContentPane().add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
			panel.add(new JLabel(name + ":"));
			panel.add(component);

			JPanel buttonPanel = new JPanel();
			getContentPane().add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			{
				JButton okButton = new JButton("OK");
				buttonPanel.add(okButton);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed (ActionEvent evt) {
						okPressed = true;
						setVisible(false);
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				buttonPanel.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed (ActionEvent evt) {
						setVisible(false);
					}
				});
			}

			setSize(new Dimension(320, 175));
		}
	}
}
