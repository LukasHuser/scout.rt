/*
 * Copyright (c) 2010-2019 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.migration.ecma6.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.scout.migration.ecma6.MigrationUtility;
import org.eclipse.scout.migration.ecma6.PathInfo;
import org.eclipse.scout.migration.ecma6.WorkingCopy;
import org.eclipse.scout.migration.ecma6.context.Context;
import org.eclipse.scout.migration.ecma6.model.api.INamedElement;
import org.eclipse.scout.migration.ecma6.model.api.INamedElement.Type;
import org.eclipse.scout.migration.ecma6.model.old.JsClass;
import org.eclipse.scout.migration.ecma6.model.old.JsClassVariable;
import org.eclipse.scout.migration.ecma6.model.old.JsFile;
import org.eclipse.scout.rt.platform.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Order(5010)
public class T5010_ResolveClassConstantsReferencesAndCreateImports extends AbstractResolveReferencesAndCreateImportTask {
  private static final Logger LOG = LoggerFactory.getLogger(T5010_ResolveClassConstantsReferencesAndCreateImports.class);

  @Override
  public void process(PathInfo pathInfo, Context context) {
    WorkingCopy workingCopy = context.ensureWorkingCopy(pathInfo.getPath());

    String source = workingCopy.getSource();
    JsFile jsFile = context.ensureJsFile(workingCopy);

    source = updateLocalReferences(jsFile, source, context, workingCopy.getLineDelimiter());

    source = updateForeignReferences(jsFile, source, context);

    workingCopy.setSource(source);
  }

  protected String updateLocalReferences(JsFile jsFile, String source, Context context, String lineDelimiter) {
    List<JsClass> jsClasses = jsFile.getJsClasses();
    List<JsClassVariable> vars = jsClasses
        .stream()
        .map(jsClass -> new ArrayList<>(jsClass.getVariables()))
        .flatMap(List::stream)
        .collect(Collectors.toList());
    if (vars.size() == 0) {
      return source;
    }
    if (jsClasses.size() != 1) {
      // check if any of the local static methods is used
      Matcher matcher = Pattern.compile(vars
          .stream()
          .map(c -> Pattern.quote(c.getJsClass().getFullyQualifiedName() + "." + c.getName()))
          .collect(Collectors.joining("|"))).matcher(source);
      if (matcher.find()) {
        source = MigrationUtility.prependTodo(source, "Replace local references (constants).", lineDelimiter);
        LOG.warn("Could not replace local references for constants in '" + jsFile.getPath() + "',.");
      }
      return source;
    }

    for (JsClassVariable v : vars) {
      JsClass jsClass = v.getJsClass();
      source = createImportForReferences(jsClass.getFullyQualifiedName() + "." + v.getName(), null, jsClass.getName() + "." + v.getName(), source, jsFile, context);
    }
    return source;
  }

  protected String updateForeignReferences(JsFile jsFile, String source, Context context) {
    Set<String> currentClassesFqn = jsFile.getJsClasses().stream().map(JsClass::getFullyQualifiedName).collect(Collectors.toSet());

    List<INamedElement> constants = context.getApi().getElements(Type.Constant, fun -> !currentClassesFqn.contains(fun.getParent().getFullyQualifiedName()));
    constants.addAll(context.getLibraries().getElements(Type.Constant));

    for (INamedElement constant : constants) {
      String replacement = constant.getParent().getName() + "." + constant.getName();
      source = createImportForReferences(constant.getFullyQualifiedName(), constant.getAncestor(Type.Class).getFullyQualifiedName(), replacement, source, jsFile, context);
    }
    return source;
  }

}