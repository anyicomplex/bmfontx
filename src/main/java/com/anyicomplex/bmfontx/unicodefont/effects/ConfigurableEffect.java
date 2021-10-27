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

import java.util.List;

/** An effect that has a number of configuration values. This allows the effect to be configured in the BMFontX GUI and to be saved
 * and loaded to and from a file.
 * @author Nathan Sweet
 * @modifier Yi An */
public interface ConfigurableEffect extends Effect {
	/** Returns the list of {@link Value}s for this effect. This list is not typically backed by the effect, so changes to the
	 * values will not take affect until {@link #setValues(List)} is called. */
	public List<Value> getValues ();

	/** Sets the list of {@link Value}s for this effect. */
	public void setValues (List<Value> values);

	/** Represents a configurable value for an effect. */
	static public interface Value {
		/** Returns the name of the value. */
		public String getName ();

		/** Sets the string representation of the value. */
		public void setString (String value);

		/** Gets the string representation of the value. */
		public String getString ();

		/** Gets the object representation of the value. */
		public Object getObject ();

		/** Shows a dialog allowing a user to configure this value. */
		public void showDialog ();
	}
}