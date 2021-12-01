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
package org.eclipse.scout.rt.platform.html.internal;

import java.util.List;

import org.eclipse.scout.rt.platform.html.IHtmlTableCell;

/**
 * Builder for a HTML table data element (&lt;td&gt;).
 */
public class HtmlTableDataBuilder extends HtmlNodeBuilder implements IHtmlTableCell {

  private static final long serialVersionUID = 1L;

  public HtmlTableDataBuilder(List<? extends CharSequence> text) {
    super("td", text);
  }

  @Override
  public IHtmlTableCell colspan(int colspan) {
    addAttribute("colspan", colspan);
    return this;
  }

  @Override
  public IHtmlTableCell cssClass(CharSequence cssClass) {
    return (IHtmlTableCell) super.cssClass(cssClass);
  }

  @Override
  public IHtmlTableCell style(CharSequence style) {
    return (IHtmlTableCell) super.style(style);
  }

  @Override
  public IHtmlTableCell appLink(CharSequence ref) {
    return (IHtmlTableCell) super.appLink(ref);
  }

  @Override
  public IHtmlTableCell addAttribute(String name, CharSequence value) {
    return (IHtmlTableCell) super.addAttribute(name, value);
  }
}
