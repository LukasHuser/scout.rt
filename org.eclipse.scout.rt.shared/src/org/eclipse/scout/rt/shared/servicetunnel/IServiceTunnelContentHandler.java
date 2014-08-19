/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.shared.servicetunnel;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.scout.commons.osgi.BundleInspector;
import org.osgi.framework.Bundle;

/**
 * This is the content handler that defines the format in which data is
 * exchanged through a service tunnel.
 * <p>
 * Most implementations also implement {@link IServiceTunnelContentObserver}
 */
public interface IServiceTunnelContentHandler {

  /**
   * @param classresolveBundles
   *          is often created using {@link BundleInspector#getOrderedBundleList(String...)}
   */
  void initialize(Bundle[] classResolveBundles, ClassLoader rawClassLoader);

  void writeRequest(OutputStream out, IServiceTunnelRequest msg) throws Exception;

  IServiceTunnelRequest readRequest(InputStream in) throws Exception;

  void writeResponse(OutputStream out, IServiceTunnelResponse msg) throws Exception;

  IServiceTunnelResponse readResponse(InputStream in) throws Exception;

}
