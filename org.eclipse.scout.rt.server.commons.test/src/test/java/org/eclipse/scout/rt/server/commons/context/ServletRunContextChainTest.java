/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.server.commons.context;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.eclipse.scout.commons.IChainable;
import org.eclipse.scout.commons.nls.NlsLocale;
import org.eclipse.scout.rt.platform.context.internal.InitThreadLocalCallable;
import org.eclipse.scout.rt.platform.context.internal.RunMonitorCallable;
import org.eclipse.scout.rt.platform.context.internal.SubjectCallable;
import org.eclipse.scout.rt.platform.context.internal.SubjectLogCallable;
import org.eclipse.scout.rt.platform.job.PropertyMap;
import org.eclipse.scout.rt.server.commons.context.internal.ServletLogCallable;
import org.eclipse.scout.rt.server.commons.servletfilter.IHttpServletRoundtrip;
import org.eclipse.scout.rt.testing.platform.runner.PlatformTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(PlatformTestRunner.class)
public class ServletRunContextChainTest {

  @Mock
  private Callable<Void> m_targetCallable;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the correct order of interceptors in {@link ServletRunContext}.
   */
  @Test
  public void testCallableChain() throws Exception {
    Callable<Void> actualCallable = new ServletRunContext().interceptCallable(m_targetCallable);

    // 0. RunMonitorCallable
    RunMonitorCallable c0 = getFirstAndAssert(actualCallable, RunMonitorCallable.class);

    // 1. SubjectCallable
    SubjectLogCallable c1i = getNextAndAssert(c0, SubjectLogCallable.class);
    SubjectCallable c1 = getNextAndAssert(c1i, SubjectCallable.class);

    // 2. InitThreadLocalCallable for NlsLocale.CURRENT
    InitThreadLocalCallable c2 = getNextAndAssert(c1, InitThreadLocalCallable.class);
    assertSame(NlsLocale.CURRENT, ((InitThreadLocalCallable) c2).getThreadLocal());

    // 3. InitThreadLocalCallable for PropertyMap.CURRENT
    InitThreadLocalCallable c3 = getNextAndAssert(c2, InitThreadLocalCallable.class);
    assertSame(PropertyMap.CURRENT, ((InitThreadLocalCallable) c3).getThreadLocal());

    ServletLogCallable c4i = getNextAndAssert(c3, ServletLogCallable.class);

    // 4. InitThreadLocalCallable for IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_REQUEST
    InitThreadLocalCallable c4 = getNextAndAssert(c4i, InitThreadLocalCallable.class);
    assertSame(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_REQUEST, ((InitThreadLocalCallable) c4).getThreadLocal());

    // 5. InitThreadLocalCallable for IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_RESPONSE
    InitThreadLocalCallable c5 = getNextAndAssert(c4, InitThreadLocalCallable.class);
    assertSame(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_RESPONSE, ((InitThreadLocalCallable) c5).getThreadLocal());

    // 6. Target
    assertSame(m_targetCallable, c5.getNext());
  }

  /**
   * Tests that new contributions can be installed after the default contributions.
   */
  @Test
  public void testCallableChainWithContributionsAfter() throws Exception {
    ServletRunContext serverRunContext = new ServletRunContext() {

      @Override
      protected <RESULT> Callable<RESULT> interceptCallable(Callable<RESULT> next) {
        Callable<RESULT> p2 = new Contribution2<>(next); // executed 3th
        Callable<RESULT> p1 = new Contribution1<>(p2); // executed 2nd
        Callable<RESULT> head = super.interceptCallable(p1); // executed 1st
        return head;
      }
    };

    Callable<Void> actualCallable = serverRunContext.interceptCallable(m_targetCallable);

    // 0. RunMonitorCallable
    RunMonitorCallable c0 = getFirstAndAssert(actualCallable, RunMonitorCallable.class);

    // 1. SubjectCallable
    SubjectLogCallable c1i = getNextAndAssert(c0, SubjectLogCallable.class);
    SubjectCallable c1 = getNextAndAssert(c1i, SubjectCallable.class);

    // 2. InitThreadLocalCallable for NlsLocale.CURRENT
    InitThreadLocalCallable c2 = getNextAndAssert(c1, InitThreadLocalCallable.class);
    assertSame(NlsLocale.CURRENT, ((InitThreadLocalCallable) c2).getThreadLocal());

    // 3. InitThreadLocalCallable for PropertyMap.CURRENT
    InitThreadLocalCallable c3 = getNextAndAssert(c2, InitThreadLocalCallable.class);
    assertSame(PropertyMap.CURRENT, ((InitThreadLocalCallable) c3).getThreadLocal());

    ServletLogCallable c4i = getNextAndAssert(c3, ServletLogCallable.class);

    // 4. InitThreadLocalCallable for IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_REQUEST
    InitThreadLocalCallable c4 = getNextAndAssert(c4i, InitThreadLocalCallable.class);
    assertSame(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_REQUEST, ((InitThreadLocalCallable) c4).getThreadLocal());

    // 5. InitThreadLocalCallable for IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_RESPONSE
    InitThreadLocalCallable c5 = getNextAndAssert(c4, InitThreadLocalCallable.class);
    assertSame(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_RESPONSE, ((InitThreadLocalCallable) c5).getThreadLocal());

    // 6. Contribution1
    Contribution1 c6 = getNextAndAssert(c5, Contribution1.class);

    // 7. Contribution2
    Contribution2 c7 = getNextAndAssert(c6, Contribution2.class);

    // 8. Target
    assertSame(m_targetCallable, c7.getNext());
  }

  /**
   * Tests that new contributions can be installed before the default contributions.
   */
  @Test
  public void testCallableChainWithContributionsBefore() throws Exception {
    ServletRunContext serverRunContext = new ServletRunContext() {

      @Override
      protected <RESULT> Callable<RESULT> interceptCallable(Callable<RESULT> next) {
        Callable<RESULT> p2 = super.interceptCallable(next); // executed 3th
        Callable<RESULT> p1 = new Contribution2<>(p2); // executed 2nd
        Callable<RESULT> head = new Contribution1<>(p1); // executed 1st
        return head;
      }
    };

    Callable<Void> actualCallable = serverRunContext.interceptCallable(m_targetCallable);

    // 1. Contribution1
    Contribution1 c1 = getFirstAndAssert(actualCallable, Contribution1.class);

    // 2. Contribution2
    Contribution2 c2 = getNextAndAssert(c1, Contribution2.class);

    // 3. RunMonitorCallable
    RunMonitorCallable c2a = getNextAndAssert(c2, RunMonitorCallable.class);

    // 3. SubjectCallable
    SubjectLogCallable c2i = getNextAndAssert(c2a, SubjectLogCallable.class);
    SubjectCallable c3 = getNextAndAssert(c2i, SubjectCallable.class);

    // 4. InitThreadLocalCallable for NlsLocale.CURRENT
    InitThreadLocalCallable c4 = getNextAndAssert(c3, InitThreadLocalCallable.class);
    assertSame(NlsLocale.CURRENT, ((InitThreadLocalCallable) c4).getThreadLocal());

    // 5. InitThreadLocalCallable for PropertyMap.CURRENT
    InitThreadLocalCallable c5 = getNextAndAssert(c4, InitThreadLocalCallable.class);
    assertSame(PropertyMap.CURRENT, ((InitThreadLocalCallable) c5).getThreadLocal());

    ServletLogCallable c6i = getNextAndAssert(c5, ServletLogCallable.class);

    // 6. InitThreadLocalCallable for IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_REQUEST
    InitThreadLocalCallable c6 = getNextAndAssert(c6i, InitThreadLocalCallable.class);
    assertSame(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_REQUEST, ((InitThreadLocalCallable) c6).getThreadLocal());

    // 7. InitThreadLocalCallable for IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_RESPONSE
    InitThreadLocalCallable c7 = getNextAndAssert(c6, InitThreadLocalCallable.class);
    assertSame(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_RESPONSE, ((InitThreadLocalCallable) c7).getThreadLocal());

    // 8. Target
    assertSame(m_targetCallable, c7.getNext());
  }

  @SuppressWarnings("unchecked")
  private static <RESULT, TYPE> TYPE getFirstAndAssert(Callable<RESULT> first, Class<TYPE> expectedType) {
    assertTrue(expectedType.equals(first.getClass()));
    return (TYPE) first;
  }

  @SuppressWarnings("unchecked")
  private static <RESULT, TYPE> TYPE getNextAndAssert(IChainable<?> c, Class<TYPE> expectedType) {
    Callable<?> next = (Callable<?>) c.getNext();
    assertTrue(expectedType.equals(next.getClass()));
    return (TYPE) next;
  }

  private static class Contribution1<RESULT> implements Callable<RESULT>, IChainable<Callable<RESULT>> {

    private final Callable<RESULT> m_next;

    public Contribution1(Callable<RESULT> next) {
      m_next = next;
    }

    @Override
    public RESULT call() throws Exception {
      return m_next.call();
    }

    @Override
    public Callable<RESULT> getNext() {
      return m_next;
    }
  }

  private static class Contribution2<RESULT> implements Callable<RESULT>, IChainable<Callable<RESULT>> {

    private final Callable<RESULT> m_next;

    public Contribution2(Callable<RESULT> next) {
      m_next = next;
    }

    @Override
    public RESULT call() throws Exception {
      return m_next.call();
    }

    @Override
    public Callable<RESULT> getNext() {
      return m_next;
    }
  }
}
