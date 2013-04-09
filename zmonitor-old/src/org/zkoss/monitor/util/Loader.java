/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zkoss.monitor.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.zkoss.monitor.impl.ZMLog;

/**
 * 
 * The concept of this part of code is originally come from log4j.
 * 
 * @author Ceki G&uuml;lc&uuml;, (modified by) Ian YT Tsai(Zanyking)
 */
public class Loader {
	/**
	 * This method will search for <code>resource</code> in different places.
	 * The search order is as follows:
	 * 
	 * <ol>
	 * 
	 * <p>
	 * <li>Search for <code>resource</code> using the thread context class
	 * loader under Java2. If that fails, search for <code>resource</code> using
	 * the class loader that loaded this class (<code>Loader</code>). Under JDK
	 * 1.1, only the the class loader that loaded this class (
	 * <code>Loader</code>) is used.
	 * 
	 * <p>
	 * <li>Try one last time with
	 * <code>ClassLoader.getSystemResource(resource)</code>, that is is using
	 * the system class loader in JDK 1.2 and virtual machine's built-in class
	 * loader in JDK 1.1.
	 * 
	 * </ol>
	 */
	public static URL findResource(String resource) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = null;

		try {
			ZMLog.debug("Trying to find [" + resource
					+ "] using context classloader " + classLoader + ".");
			url = classLoader.getResource(resource);
			if (url != null) {
				return url;
			}

			// We could not find resource. Let us now try with the
			// classloader that loaded this class.
			classLoader = Loader.class.getClassLoader();
			if (classLoader != null) {
				ZMLog.debug("Trying to find [" + resource + "] using "
						+ classLoader + " class loader.");
				url = classLoader.getResource(resource);
				if (url != null) {
					return url;
				}
			}
		} catch (Throwable t) {
			//
			// can't be InterruptedException or InterruptedIOException
			// since not declared, must be error or RuntimeError.
		}

		// Last ditch attempt: get the resource from the class path. It
		// may be the case that clazz was loaded by the Extentsion class
		// loader which the parent of the system class loader. Hence the
		// code below.
		ZMLog.debug("Trying to find [" + resource
				+ "] using ClassLoader.getSystemResource().");
		return ClassLoader.getSystemResource(resource);
	}

	/**
	 * get resource by path from current thread's classLoader.
	 * @param resourcePath 
	 * @return
	 */
	public static URL getResource(String resourcePath) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource(resourcePath);
		return url;
	}

	/**
	 * 
	 * @param resPath
	 * @return
	 * @throws IOException
	 */
	public static InputStream getResourceAsStreamIfAny(String resPath) {
		URL url = findResource(resPath);
		try {
			return url == null ? null : url.openStream();
		} catch (IOException e) {
			ZMLog.warn(e, " URL open Connection got an exception!");
			return null;
		}
	}

}
