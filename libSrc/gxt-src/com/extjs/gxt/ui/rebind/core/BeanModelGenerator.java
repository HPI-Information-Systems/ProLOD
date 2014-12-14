/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.rebind.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelMarker;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.NestedModelUtil;
import com.extjs.gxt.ui.client.data.BeanModelMarker.BEAN;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class BeanModelGenerator extends Generator {

  protected TypeOracle oracle;
  protected JClassType beanModelMarkerType;
  protected JClassType beanModelTagType;
  protected List<JClassType> beans;

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
    oracle = context.getTypeOracle();
    beanModelMarkerType = oracle.findType(BeanModelMarker.class.getName());
    beanModelTagType = oracle.findType(BeanModelTag.class.getName());

    try {
      // final all beans and bean markers
      beans = new ArrayList<JClassType>();
      JClassType[] types = oracle.getTypes();
      for (JClassType type : types) {
        if (isBeanMarker(type)) {
          beans.add(getMarkerBean(type));
        } else if (isBean(type)) {
          beans.add(type);
        }
      }

      final String genPackageName = BeanModelLookup.class.getPackage().getName();
      final String genClassName = "BeanModelLookupImpl";

      ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
      composer.setSuperclass(BeanModelLookup.class.getCanonicalName());
      composer.addImport(BeanModelFactory.class.getName());
      composer.addImport(Map.class.getName());
      composer.addImport(FastMap.class.getName());

      PrintWriter pw = context.tryCreate(logger, genPackageName, genClassName);

      if (pw != null) {
        SourceWriter sw = composer.createSourceWriter(context, pw);

        sw.println("private Map<String, BeanModelFactory> m;");

        sw.println("public BeanModelFactory getFactory(Class b) {");
        sw.indent();
        sw.println("String n = b.getName();");
        sw.println("if (m == null) {");
        sw.indentln("m = new FastMap<BeanModelFactory>();");
        sw.println("}");
        sw.println("if (m.get(n) == null) {");
        sw.indent();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < beans.size(); i++) {
          JClassType bean = beans.get(i);
          String name = createBean(bean, logger, context);
          String factory = createFactory(bean, name, logger, context);

          if (i > 0) {
            sw.print(" else ");
          }
          sw.println("if (" + bean.getQualifiedSourceName() + ".class.getName().equals(n)) {");
          sw.indentln("m" + i + "();");

          sb.append("private void m" + i + "() {\n");
          sb.append("  m.put(" + bean.getQualifiedSourceName() + ".class.getName(), new " + factory + "());\n");
          sb.append("}\n");

          sw.print("}");
        }
        sw.outdent();
        sw.println("}");
        sw.println("return m.get(n);");
        sw.outdent();
        sw.println("}");

        sw.println(sb.toString());
        sw.commit(logger);
      }

      return composer.getCreatedClassName();

    } catch (Exception e) {
      logger.log(TreeLogger.ERROR, "Class " + typeName + " not found.", e);
      throw new UnableToCompleteException();
    }

  }

  protected String createFactory(JClassType bean, String beanModelName, TreeLogger logger, GeneratorContext context)
      throws Exception {
    final String genPackageName = BeanModelLookup.class.getPackage().getName();
    final String genClassName = "BeanModel_" + bean.getQualifiedSourceName().replace(".", "_") + "_Factory";

    ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
    composer.setSuperclass(BeanModelFactory.class.getCanonicalName());
    PrintWriter pw = context.tryCreate(logger, genPackageName, genClassName);

    if (pw != null) {
      SourceWriter sw = composer.createSourceWriter(context, pw);
      sw.println("public BeanModel newInstance() {");
      sw.println("return new " + beanModelName + "();");
      sw.println("}");
      sw.commit(logger);
    }
    return composer.getCreatedClassName();
  }

  protected String createBean(JClassType bean, TreeLogger logger, GeneratorContext context) throws Exception {
    final String genPackageName = bean.getPackage().getName();
    final String genClassName = "BeanModel_" + bean.getQualifiedSourceName().replace(".", "_");

    ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
    composer.setSuperclass(BeanModel.class.getCanonicalName());
    composer.addImport(BeanModel.class.getName());
    composer.addImport(NestedModelUtil.class.getName());
    PrintWriter pw = context.tryCreate(logger, genPackageName, genClassName);

    if (pw != null) {
      List<JMethod> getters = findGetters(bean);
      List<JMethod> setters = findSetters(bean);
      SourceWriter sw = composer.createSourceWriter(context, pw);

      sw.println("public " + genClassName + "(){");
      for (JMethod method : getters) {
        String s = method.getName();
        String p = lowerFirst(s.substring(s.startsWith("g") ? 3 : 2)); // get or
        // is
        sw.println("beanProperties.add(\"" + p + "\");");
      }
      sw.println("}");

      createGetMethods(getters, sw, bean.getQualifiedSourceName());
      createSetMethods(setters, sw, bean.getQualifiedSourceName());

      // delegate equals to bean
      sw.println("public boolean equals(Object obj) {");
      sw.println("  if (obj instanceof " + "BeanModel" + ") {");
      sw.println("    obj = ((BeanModel)obj).getBean();");
      sw.println("  }");
      sw.println("  return bean.equals(obj);");
      sw.println("}");

      // delegate hashCode to bean
      sw.println("public int hashCode(){");
      sw.println("  return bean.hashCode();");
      sw.println("}");

      sw.commit(logger);
    }
    return composer.getCreatedClassName();
  }

  protected JClassType getMarkerBean(JClassType type) throws NotFoundException {
    BEAN pojo = type.getAnnotation(BEAN.class);
    return oracle.getType(pojo.value().getCanonicalName());
  }

  protected boolean isBean(JClassType type) {
    return !type.equals(beanModelTagType) && type.isAssignableTo(beanModelTagType)
        && !type.isAssignableTo(oracle.findType(ModelData.class.getName()));
  }

  protected boolean isBeanMarker(JClassType type) {
    return !type.equals(beanModelMarkerType) && type.isAssignableTo(beanModelMarkerType)
        && !type.isAssignableTo(oracle.findType(ModelData.class.getName()));
  }

  protected void createGetMethods(List<JMethod> getters, SourceWriter sw, String typeName) {
    sw.println("public <X> X get(String s) {");

    sw.println("if (allowNestedValues && NestedModelUtil.isNestedProperty(s)) {");
    sw.indentln("return (X)NestedModelUtil.getNestedValue(this, s);");
    sw.println("}");

    for (JMethod method : getters) {
      JClassType returnType = method.getReturnType().isClassOrInterface();
      String s = method.getName();
      String p = lowerFirst(s.substring(s.startsWith("g") ? 3 : 2)); // get or

      sw.println("if (s.equals(\"" + p + "\")) {");
      sw.println("Object value = ((" + typeName + ")bean)." + s + "();");

      try {
        if (returnType != null && returnType.isAssignableTo(oracle.getType(List.class.getName())) && returnType.isParameterized() != null) {
              JParameterizedType type = returnType.isParameterized();
              JClassType[] params = type.getTypeArgs();
              if (beans.contains(params[0])) {
                sw.println("if (value != null) {");
                sw.indent();
                sw.println("java.util.List list = (java.util.List)value;");
                sw.println("java.util.List temp = new java.util.ArrayList();");
                sw.println("for (Object obj : list) {");
                sw.indent();
                sw.println("temp.add(");
                sw.println(BeanModelLookup.class.getCanonicalName() + ".get().getFactory("
                    + params[0].getQualifiedSourceName() + ".class).createModel(obj));");
                sw.outdent();
                sw.println("}");
                sw.println("return (X) temp;");
                sw.outdent();
                sw.println("}");
          }
        }
        // swap returnType as generic types were not matching
        // (beans.contains(returnType))
        if (returnType != null) {
          String t = returnType.getQualifiedSourceName();
          if (t.indexOf("extends") == -1) {
            returnType = oracle.getType(t);
          }
        }
        if (beans.contains(returnType)) {
          sw.println("if (value != null) {");
          sw.println("    BeanModel nestedModel = nestedModels.get(s);");
          sw.println("    if (nestedModel != null) {");
          sw.println("      Object bean = nestedModel.getBean();");
          sw.println("      if (!bean.equals(value)){");
          sw.println("        nestedModel = null;");
          sw.println("      }");
          sw.println("    }");
          sw.println("    if (nestedModel == null) {");
          sw.println("        nestedModel = " + BeanModelLookup.class.getCanonicalName() + ".get().getFactory("
              + returnType.getQualifiedSourceName() + ".class).createModel(value);");
          sw.println("        nestedModels.put(s, nestedModel);");
          sw.println("    }");
          sw.println("    return (X)processValue(nestedModel);");
          sw.println("}");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      sw.println("return (X)processValue(value);");
      sw.println("}");
    }
    sw.println("return super.get(s);");
    sw.println("}");
  }

  protected String lowerFirst(String propName) {
    if (propName.length() == 0) {
      return propName;
    } else if (propName.length() == 1) {
      return propName.toLowerCase();
    } else {
      return propName.substring(0, 1).toLowerCase() + propName.substring(1);
    }
  }

  protected String getMethodAttributeType(JMethod method) {
    return method.getParameters()[0].getType().getQualifiedSourceName();
  }

  protected void createSetMethods(List<JMethod> properties, SourceWriter sw, String typeName) {
    sw.println("public <X> X set(String s, X val) {");
    sw.indent();
    sw.println("Object obj = val;");
    sw.println("if (allowNestedValues && val instanceof BeanModel) {");
    sw.indent();
    sw.println("obj = ((BeanModel)val).getBean();");
    sw.println("if (nestedModels.containsKey(s)) {");
    sw.indent();
    sw.println("nestedModels.put(s, (BeanModel)val);");
    sw.outdent();
    sw.println("}");
    sw.outdent();
    sw.println("}");

    sw.println("if (allowNestedValues && NestedModelUtil.isNestedProperty(s)) {");
    sw.indent();
    sw.println("X old = (X) NestedModelUtil.setNestedValue(this, s, val);");
    sw.println("notifyPropertyChanged(s, val, old);");
    sw.println("return old;");
    sw.outdent();
    sw.println("}");

    for (JMethod method : properties) {
      String s = method.getName();
      String p = lowerFirst(s.substring(3));
      String type = getMethodAttributeType(method);

      if (type.indexOf("extends") != -1) {
        type = "java.lang.Object";
      }

      if (type.equals("byte")) {
        type = "Byte";
      } else if (type.equals("char")) {
        type = "Character";
      } else if (type.equals("short")) {
        type = "Short";
      } else if (type.equals("int")) {
        type = "Integer";
      } else if (type.equals("long")) {
        type = "Long";
      } else if (type.equals("float")) {
        type = "Float";
      } else if (type.equals("double")) {
        type = "Double";
      } else if (type.equals("boolean")) {
        type = "Boolean";
      }

      sw.println("if (s.equals(\"" + p + "\")) {");
      sw.indent();
      sw.println("Object old = get(s);");
      sw.println("((" + typeName + ")bean)." + s + "((" + type + ")obj);");
      sw.println("notifyPropertyChanged(s, val, old);");
      sw.println("return (X)old;");
      sw.outdent();
      sw.println("}");
    }
    sw.println("return super.set(s, val);");
    sw.outdent();
    sw.println("}");
  }

  protected List<JMethod> findGetters(JClassType cls) {
    List<JMethod> methods = new ArrayList<JMethod>();
    addGetters(cls, methods);
    return methods;
  }

  protected void addGetters(JClassType cls, List<JMethod> methods) {

    // ignore methods of Object
    if (cls.getSuperclass() != null) {
      addGetters(cls.getSuperclass(), methods);
      for (JMethod m : cls.getMethods()) {
        if (m.isPublic() || m.isProtected()) {
          String name = m.getName();
          if ((name.matches("get.*") || name.matches("is.*")) && m.getParameters().length == 0) {
            methods.add(m);
          }
        }
      }
    }

  }

  protected List<JMethod> findSetters(JClassType cls) {
    List<JMethod> methods = new ArrayList<JMethod>();
    addSetters(cls, methods);
    return methods;
  }

  protected void addSetters(JClassType cls, List<JMethod> methods) {
    if (cls.getSuperclass() != null) {
      addSetters(cls.getSuperclass(), methods);
    }
    for (JMethod m : cls.getMethods()) {
      if (m.isPublic() || m.isProtected()) {
        String name = m.getName();
        if (name.matches("set.*") && m.getParameters().length == 1) {
          methods.add(m);
        }
      }
    }
  }

}
