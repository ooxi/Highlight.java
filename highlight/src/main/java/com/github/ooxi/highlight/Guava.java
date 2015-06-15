/**
 * Copyright (c) 2015 ooxi
 *     https://github.com/ooxi/Highlight.java
 *     violetland@mail.ru
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in a
 *     product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *  3. This notice may not be removed or altered from any source distribution.
 */
package com.github.ooxi.highlight;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Manually implemented the required functionallity from Google Guava in order
 * to avoid a full blown runtime dependency.
 * 
 * @author ooxi
 */
final class Guava {
	
	
	public static final class Preconditions {
		
		public static void checkState(boolean condition, String msg) throws IllegalStateException {
			if (!condition) {
				throw new IllegalStateException(msg);
			}
		}
		
		private Preconditions() {
			throw new AssertionError();
		}
	}
	
	
	
	public static final class ByteStreams {
		
		public static byte[] toByteArray(InputStream is) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[10240];
			
			for (int read = is.read(buf); -1 != read; read = is.read(buf)) {
				baos.write(buf, 0, read);
			}
			
			return baos.toByteArray();
		}
		
		
		
		private ByteStreams() {
			throw new AssertionError();
		}
	}
}
