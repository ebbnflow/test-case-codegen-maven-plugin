package com.github.ebbnflow;


import com.google.common.base.Strings;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import java.beans.IntrospectionException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public void generateTest() throws IOException, InvocationTargetException, NoSuchMethodException, IntrospectionException, IllegalAccessException, FormatterException {
        rootToken = new Token();
        rootToken.setTokenStart("package " + rootObj.getClass().getPackage().getName() + ";");
        importsTokenRoot = rootToken.createChildToken();
        importsTokenRoot.createChildToken("import org.junit.jupiter.api.Test;", null);
        importsTokenRoot.createChildToken("import static org.junit.jupiter.api.Assertions.assertEquals;", null);
        classToken = rootToken.createChildToken();

        classToken.setTokenStart("public class " + rootObj.getClass().getSimpleName() + "Test {");
        classToken.setTokenEnd("}");
        classToken.createChildToken("@Test", null, false);
        mainUnitTestMethodRootToken = classToken.createChildToken("public void " + rootObj.getClass().getSimpleName() + "Validation() {", "}");
        mainUnitTestMethodRootToken.createChildToken(rootObj.getClass().getName() + " a" + rootObj.getClass().getSimpleName() + " = create" + rootObj.getClass().getSimpleName() + "();", null);
        importsTokenRoot.createChildToken("import " + rootObj.getClass().getName() + ";", null);

        generateTest(rootObj, mainUnitTestMethodRootToken);

        String packagePath = rootObj.getClass().getPackage().getName().replace(".", "/");
        printCode(MessageFormat.format("./src/test/java/{0}/{1}Test.java", packagePath, rootObj.getClass().getSimpleName()));
    }

    private void generateTest(Object object, Token assertionMethodToken) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Token factoryMethodRoot = classToken.createChildToken(
                MessageFormat.format("public {0} create{1}()'{'",
                        object.getClass().getSimpleName(),
                        object.getClass().getSimpleName()),
                "}");

        factoryMethodRoot.createChildToken(
                MessageFormat.format("{0} a{0} = new {0}();",
                        object.getClass().getSimpleName()),
                "");
        List<Field> fieldList = createFieldList(object.getClass());
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
                //else if a list
                //flatten them out
                //assert("val", list.get(0).getMyProp(), "these should equal");
                //assert("val", list.get(0).getMyProp2(), "these should equal");
                //assert("val", list.get(1).getMyProp1(), "these should equal for second item in list");

            } else {

                //else if a Pojo then

                // MyType type = createMyType(); // for every complex object under this root, there will be a createType() factory method
                factoryMethodRoot.createChildToken(type.getName() + " a" + type.getSimpleName() + " = create" + type.getSimpleName() + "();", null);
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

                String pojoCreationMethodName = MessageFormat.format("public void assert{0}({1} a{2})'{'",
                        makeFirstLetterUpperCase(item.getName()),
                        type.getSimpleName(),
                        type.getSimpleName()
                );
                Token pojoCreationToken = classToken.createChildTokenAt(2, pojoCreationMethodName, "}");
                generateTest(value, pojoCreationToken);
            }
        }

        factoryMethodRoot.createChildToken(
                MessageFormat.format("return a{0};",
                        object.getClass().getSimpleName()),
                "");
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
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);
        writeToken(writer, rootToken);
        writer.close();
        String formatSource = new Formatter().formatSource(stringWriter.toString());
        java.nio.file.Files.write( Paths.get(path), formatSource.getBytes());
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

        if (Strings.isNullOrEmpty(token.getTokenEnd()))
            return;

        writer.write(token.getTokenEnd());

        if (token.isNewLineAfterTokenEnd())
            writer.newLine();
    }

    public static boolean isClassCollection(Class<?> c) {
        return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c) || c.isArray();
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type) || type.equals(String.class);
    }
}
