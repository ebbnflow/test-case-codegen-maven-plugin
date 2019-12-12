package com.github.ebbnflow;


import com.google.common.base.Strings;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;

public class UnitTestGenerator<T> {

  private Token rootToken;
  private Token importsTokenRoot;
  private Token classToken;
  private Token mainUnitTestMethodRootToken;
  private T rootObj;

  public UnitTestGenerator(T obj) {
    this.rootObj = obj;
  }

  public void generateTest()
      throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, FormatterException {
    rootToken = new Token();
    rootToken.setTokenStart("package " + rootObj.getClass().getPackage().getName() + ";");
    importsTokenRoot = rootToken.createChildToken();
    importsTokenRoot.createChildToken("import org.junit.jupiter.api.Test;", null);
    importsTokenRoot
        .createChildToken("import static org.junit.jupiter.api.Assertions.assertEquals;", null);
    classToken = rootToken.createChildToken();

    classToken.setTokenStart("public class " + rootObj.getClass().getSimpleName() + "Test {");
    classToken.setTokenEnd("}");
    classToken.createChildToken("@Test", null, false);
    mainUnitTestMethodRootToken = classToken
        .createChildToken("public void " + rootObj.getClass().getSimpleName() + "Validation() {",
            "}");
    mainUnitTestMethodRootToken.createChildToken(
        rootObj.getClass().getSimpleName() + " a" + rootObj.getClass().getSimpleName() + " = create"
            + rootObj.getClass().getSimpleName() + "();", null);
    importsTokenRoot.createChildToken("import " + rootObj.getClass().getName() + ";", null);

    generateTest(rootObj, mainUnitTestMethodRootToken);

    String packagePath = rootObj.getClass().getPackage().getName().replace(".", "/");
    printCode(MessageFormat.format("./src/test/java/{0}/{1}Test.java", packagePath,
        rootObj.getClass().getSimpleName()));
  }

  private void generateTest(Object object, Token assertionMethodToken)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

//    if (isClassCollection(object.getClass())) {
//      createCollectionFactoryAndAssertions(object, assertionMethodToken);
//    } else {
    createObjectFactoryAndAssertions(object, assertionMethodToken);
//    }

  }

  private void createObjectFactoryAndAssertions(Object object, Token assertionMethodToken)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

    //public MyClass createMyClass(){
    Token factoryMethodRoot = classToken.createChildToken(
        MessageFormat.format("public {0} create{1}()'{'",
            object.getClass().getSimpleName(),
            object.getClass().getSimpleName()),
        "}");

    //MyClass aClass = new MyClass();
    factoryMethodRoot.createChildToken(
        MessageFormat.format("{0} a{0} = new {0}();",
            object.getClass().getSimpleName()),
        "");

    List<Field> fieldList = createFieldList(object.getClass());

    //write the assertion lines for each field to the
    for (Field item : fieldList) {
      Class<?> type = item.getType();
      String getterName = findGetterName(type, item);
      String setterName = findSetterName(type, item);
      Object value = PropertyUtils.getProperty(object, item.getName());

      //if is primitive then assert and add a line to the creation method
      if (isPrimitiveOrWrapper(type)) {
        String writeValue = value.toString();
        if (value instanceof String) {
          writeValue = MessageFormat.format("\"{0}\"", value);
        }

        assertionMethodToken.createChildToken(
            MessageFormat.format("assertEquals({0}, a{1}.{2}(), {3});",
                writeValue,
                object.getClass().getSimpleName(),
                getterName,
                MessageFormat.format("\"{0} must equal\"", item.getName())),
            null);

        factoryMethodRoot.createChildToken(
            MessageFormat.format("a{0}.{1}({2});",
                object.getClass().getSimpleName(),
                setterName,
                writeValue),
            "");

      } else if (isClassCollection(type)) {

        createCollectionMethodCall(object,
            assertionMethodToken,
            factoryMethodRoot,
            item,
            type,
            getterName,
            setterName,
            value);

      } else {

        //else if a Pojo then
        createObjectMethodCall(object,
            assertionMethodToken,
            factoryMethodRoot,
            item,
            type,
            getterName,
            setterName,
            value);

      }
    }

    factoryMethodRoot.createChildToken(
        MessageFormat.format("return a{0};",
            object.getClass().getSimpleName()),
        "");
  }

//  private void createCollectionFactoryAndAssertions(Object object, Token assertionMethodToken) {
//
//
//
//
//
//
//
//  }

  private void createCollectionMethodCall(Object parentObject, Token assertionMethodToken,
      Token factoryMethodRoot, Field field, Class<?> type, String getterName, String setterName,
      Object listObj)
       {


    if (type.equals(List.class)) {
      ParameterizedType listType = (ParameterizedType) field.getGenericType();
      Class<?> clazz = (Class<?>) listType.getActualTypeArguments()[0];

      //List<MyType> typeList = createMyTypeList();
      String simpleListName = listType.getRawType().getTypeName().split("\\.")[2];
      String createStatement = MessageFormat
          .format("{0}<{1}>  a{1}{2} = create{1}{2}();",
              listType.getRawType().getTypeName(),
              clazz.getSimpleName(),
              simpleListName);

      factoryMethodRoot.createChildToken(createStatement, null);

      //rootObj.setMyObjList(typeList);
      factoryMethodRoot.createChildToken(
          MessageFormat.format("a{0}.{1}(a{2}{3});",
              parentObject.getClass().getSimpleName(), //aSimplePojo
              setterName, //setMyNestedList
              clazz.getSimpleName(), //NestedClass
              simpleListName // List
          ),
          "");

      //import com.ebbnflow.MyType;
      importsTokenRoot.createChildToken("import " + clazz.getCanonicalName() + ";", null);

      //assertMyNestedList(aSimplePojo.getStudent());
      assertionMethodToken.createChildToken(
          MessageFormat.format("assert{0}(a{1}.{2}());",
              makeFirstLetterUpperCase(field.getName()),
              parentObject.getClass().getSimpleName(),
              getterName),
          null);

      String assertionMethodName = MessageFormat
          .format("public void assert{0}({1}<{2}> a{2}{3})'{'",
              makeFirstLetterUpperCase(field.getName()),
              listType.getRawType().getTypeName(),
              clazz.getSimpleName(),
              simpleListName);

      Token nextAssertionMethodToken = classToken.createChildTokenAt(2, assertionMethodName, "}");

      createListAssertionsAndFactoryMethod(listObj, listType, clazz, simpleListName,
          nextAssertionMethodToken);

      //generateTest(value, nextAssertionMethodToken);

    } else if (type.equals(java.util.Map.class)) {

    }
  }

  private void createListAssertionsAndFactoryMethod(Object object, ParameterizedType listType, Class<?> clazz,
      String simpleListName, Token nextAssertionMethodToken) {

    //public List<MyClass> createMyClassList(){
    Token factoryMethodListRoot = classToken.createChildToken(
        MessageFormat.format("public {0}<{1}> create{1}{2}()'{'",
            listType.getRawType().getTypeName(),
            clazz.getSimpleName(),
            simpleListName),
        "}");

    //MyClass aClass = new MyClass();
    factoryMethodListRoot.createChildToken(
        MessageFormat.format("{0}<{1}> a{1}{2} = new java.util.ArrayList<>();",
            listType.getRawType().getTypeName(),
            clazz.getSimpleName(),
            simpleListName),
        "");

    List objList = (List)object;
    int size = objList.size();
    for(int ii = 0; ii < size; ii++){
      Object o = objList.get(ii);
      if (isPrimitiveOrWrapper(o.getClass())){

      } else if (isClassCollection(o.getClass())){

      } else {
        //is complex object
        //assertMyItem1(myList.get(1));

        //MyItem item1 = createMyItem1();
        //myList.add(item1);

        //public MyItem createMyItem1(){
        //  MyItem item = new MyItem();
        //  item.setThing("some thing");
        //  return item;
        //}
      }
    }
//    writeListAssertions(clazz, object);

    factoryMethodListRoot.createChildToken(
        MessageFormat.format("return a{0}{1};",
            clazz.getSimpleName(),
            simpleListName),
        "");
  }

//  private <Type> void writeListAssertions(Class<Type> clazz, Object obj) {
//    //flatten them out
//    //assert("val", list.get(0).getMyProp(), "these should equal");
//    //assert("val", list.get(0).getMyProp2(), "these should equal");
//    //assert("val", list.get(1).getMyProp1(), "these should equal for second item in list");
//
//  }

  private void createObjectMethodCall(Object object, Token assertionMethodToken,
      Token factoryMethodRoot, Field item, Class<?> type, String getterName, String setterName,
      Object value)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    // MyType type = createMyType(); // for every complex object under this root, there will be a createType() factory method
    factoryMethodRoot.createChildToken(
        type.getSimpleName() + " a" + type.getSimpleName() + " = create" + type.getSimpleName()
            + "();",
        null);
    //rootObj.setMyObj(myObj);
    factoryMethodRoot.createChildToken(
        MessageFormat.format("a{0}.{1}(a{2});",
            object.getClass().getSimpleName(),
            setterName,
            type.getSimpleName()
        ),
        "");
    //import com.ebbnflow.MyObj;
    importsTokenRoot.createChildToken("import " + type.getName() + ";", null);

    //assertStudent(aSimplePojo.getStudent());
    assertionMethodToken.createChildToken(
        MessageFormat.format("assert{0}(a{1}.{2}());",
            makeFirstLetterUpperCase(item.getName()),
            object.getClass().getSimpleName(),
            getterName),
        null);

    String assertionMethodName = MessageFormat.format("public void assert{0}({1} a{2})'{'",
        makeFirstLetterUpperCase(item.getName()),
        type.getSimpleName(),
        type.getSimpleName()
    );

    Token nextAssertionMethodToken = classToken.createChildTokenAt(2, assertionMethodName, "}");
    generateTest(value, nextAssertionMethodToken);
  }

  private String makeFirstLetterUpperCase(String name) {
    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  private String findSetterName(Class<?> type, Field item) {
    return "set" + makeFirstLetterUpperCase(item.getName());
  }

  private String findGetterName(Class<?> type, Field item) {
    String getterName;
    if (type.equals(boolean.class) || type.equals(Boolean.TYPE)) {
      getterName = "is" + makeFirstLetterUpperCase(item.getName());
    } else {
      getterName = "get" + makeFirstLetterUpperCase(item.getName());
    }
    return getterName;
  }

  private List<Field> createFieldList(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

    if (clazz.getSuperclass() != null) {
      fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
    }
    return fields;
  }

  public void printCode(String path) throws IOException, FormatterException {
    FileWriter writer = new FileWriter(path);
    BufferedWriter bufferedWriter = new BufferedWriter(writer);
    writeToken(bufferedWriter, rootToken);
    bufferedWriter.close();
    //reading it in again - makes it easier to debug - not efficient code tho.
    String codeString = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    String formatSource = new Formatter().formatSource(codeString);
    java.nio.file.Files.write(Paths.get(path), formatSource.getBytes());
  }

  private void writeToken(BufferedWriter writer, Token token) throws IOException {
    if (!Strings.isNullOrEmpty(token.getTokenStart())) {
      writer.write(token.getTokenStart());
      writer.newLine();
    }
    if (null != token.getChildTokens() && token.getChildTokens().size() > 0) {
      for (Token nestedToken : token.getChildTokens()) {
        writeToken(writer, nestedToken);
      }
    }

    if (Strings.isNullOrEmpty(token.getTokenEnd())) {
      return;
    }

    writer.write(token.getTokenEnd());

    if (token.isNewLineAfterTokenEnd()) {
      writer.newLine();
    }
  }

  public static boolean isClassCollection(Class<?> c) {
    return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c) || c.isArray();
  }

  public static boolean isPrimitiveOrWrapper(Class<?> type) {
    return ClassUtils.isPrimitiveOrWrapper(type) || type.equals(String.class);
  }
}
