/*
 * Copyright (c) 2010-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.rt.platform.html;

import java.io.Serializable;

/**
 * Marker interface for any HTML content that may contain bind variables.
 */
public interface IHtmlContent extends CharSequence, Serializable {

  String toHtml();

  String toPlainText();

  /**
   * See {@link #withNewLineToBr(boolean)}.
   */
  boolean isNewLineToBr();

  /**
   * @param newLineToBr
   *          {@code true} if new lines should be replaced by &lt;br&gt; tags, {@code false} otherwise (default is
   *          {@code true}).
   */
  IHtmlContent withNewLineToBr(boolean newLineToBr);
}
