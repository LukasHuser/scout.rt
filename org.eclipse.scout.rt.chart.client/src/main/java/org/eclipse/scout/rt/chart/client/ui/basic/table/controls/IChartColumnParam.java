/*
 * Copyright (c) 2010-2022 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.rt.chart.client.ui.basic.table.controls;

import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;

public interface IChartColumnParam {

  int AGGREGATION_COUNT = -1;
  int AGGREGATION_SUM = 1;
  int AGGREGATION_AVG = 2;

  int GROUP_BY_YEARS = 256;
  int GROUP_BY_MONTHS = 257;
  int GROUP_BY_WEEKDAYS = 258;
  int GROUP_BY_DATE = 259;

  int getColumnIndex();

  IColumn getColumn();

  int getColumnModifier();

}
