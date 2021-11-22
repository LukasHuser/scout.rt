/*
 * Copyright (c) 2010-2021 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.rt.ui.html.res.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.eclipse.scout.rt.platform.exception.PlatformException;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.resource.BinaryResources;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.ImmutablePair;
import org.eclipse.scout.rt.server.commons.servlet.cache.HttpCacheControl;
import org.eclipse.scout.rt.shared.ui.webresource.WebResourceDescriptor;
import org.eclipse.scout.rt.shared.ui.webresource.WebResources;

public class WebResourceLoader extends AbstractResourceLoader {

  private final boolean m_minify;
  private final boolean m_cacheEnabled;
  private final String m_theme;

  public WebResourceLoader(boolean minify, boolean cacheEnabled, String theme) {
    m_minify = minify;
    m_cacheEnabled = cacheEnabled;
    m_theme = theme;
  }

  @Override
  public BinaryResource loadResource(String pathInfo) {
    return lookupResource(pathInfo)
        .map(this::toBinaryResources)
        .map(br -> br.withFilename(pathInfo))
        .map(BinaryResources::build)
        .orElse(null);
  }

  public boolean acceptFile(String file) {
    return lookupResource(file).isPresent();
  }

  public Optional<WebResourceDescriptor> resolveResource(String pathInfo) {
    return lookupResource(pathInfo).map(ImmutablePair::getLeft);
  }

  protected Optional<ImmutablePair<WebResourceDescriptor, Integer>> lookupResource(String file) {
    return WebResources.resolveScriptResource(file, m_minify, m_theme)
        .map(descriptor -> new ImmutablePair<>(descriptor, HttpCacheControl.MAX_AGE_ONE_YEAR))
        .or(() -> WebResources.resolveWebResource(file, m_minify)
            .map(descriptor -> new ImmutablePair<>(descriptor, HttpCacheControl.MAX_AGE_4_HOURS)));
  }

  protected BinaryResources toBinaryResources(ImmutablePair<WebResourceDescriptor, Integer> res) {
    URL url = res.getLeft().getUrl();
    try {
      URLConnection connection = url.openConnection();
      byte[] bytes = IOUtility.readFromUrl(url);
      return BinaryResources.create()
          .withContent(bytes)
          .withCharset(StandardCharsets.UTF_8)
          .withLastModified(connection.getLastModified())
          .withCachingAllowed(m_cacheEnabled)
          .withCacheMaxAge(res.getRight());
    }
    catch (IOException e) {
      throw new PlatformException("Unable to read from url '{}'.", url, e);
    }
  }

}
