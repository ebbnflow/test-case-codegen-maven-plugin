package com.github.ebbnflow;


import com.google.common.base.Strings;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;

import javax.swing.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;

public class UnitTestGenerator<T> {
    private Token rootToken;
    private Token importsTokenRoot;
    private Token classToken;
    private T obj;

    public UnitTestGenerator(T obj) {
        this.obj = obj;
    }

    public void generateTest() throws IOException, InvocationTargetException, NoSuchMethodException, IntrospectionException, IllegalAccessException {
        rootToken = new Token();
        rootToken.setTokenStart("package " + obj.getClass().getPackage().getName() + ";");
        importsTokenRoot = rootToken.createChildToken();
        importsTokenRoot.createChildToken("import org.junit.jupiter.api.Test;", null);
        importsTokenRoot.createChildToken("import static org.junit.jupiter.api.Assertions.assertEquals;", null);
        classToken = rootToken.createChildToken();

        classToken.setTokenStart("public class " + obj.getClass().getSimpleName() + "Test {");
        classToken.setTokenEnd("}");
        classToken.createChildToken("@Test", null, false);
        Token mainUnitTestMethodRootToken = classToken.createChildToken("public void " + obj.getClass().getSimpleName() + "Validation() {", "}");

        generateTest(this.obj, mainUnitTestMethodRootToken);

        String packagePath = obj.getClass().getPackage().getName().replace(".", "/");
        printCode(MessageFormat.format("./src/test/java/{0}/{1}Test.java", packagePath, obj.getClass().getSimpleName()));
    }

    private void generateTest(Object object, Token assertionMethodToken) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // MyType type = createMyType(); // for every complex object under this root, there will be a createType() factory method
        Token factoryMethodRoot = writeFactoryCreationMethod(obj, assertionMethodToken);

        factoryMethodRoot.createChildToken(
                MessageFormat.format("{0} a{0} = new {0}();",
                        object.getClass().getSimpleName()),
                "");
        List<Field> fieldList = createFieldList(object.getClass());
        for (Field item : fieldList) {
            Class<?> type = item.getType();
            //if is primitive then assert and add a line to the creation method
            if (isPrimitiveOrWrapper(type)) {
                Object value = PropertyUtils.getProperty(object, item.getName());
                String writeValue = value.toString();
                if (value instanceof String) {
                    writeValue = MessageFormat.format("\"{0}\"", value);
                }

                String getterName;
                if (type.equals(boolean.class) || type.equals(Boolean.TYPE)) {
                    getterName = "is" + Character.toUpperCase(item.getName().charAt(0)) + item.getName().substring(1);
                } else {
                    getterName = "get" + Character.toUpperCase(item.getName().charAt(0)) + item.getName().substring(1);
                }

                assertionMethodToken.createChildToken(
                        MessageFormat.format("assertEquals({0}, a{1}.{2}(), {3});",
                                writeValue, obj.getClass().getSimpleName(), getterName,
                        MessageFormat.format("\"{0} must equal\"", item.getName())),
                        null);

                String setterName = "set" + Character.toUpperCase(item.getName().charAt(0)) + item.getName().substring(1);
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
                //1.assertionMethodToken.createChild("assertNestedThing(thing);", null)
                //2 call generateTest(nestedObj, classToken.createChildToken());
            }


        }
        //assertEquals(type.field1, "value of field 1", "field name should be equal")
        //assertMyNestedObj(type.getMyNestedObj()); //for every nested obj, make an assertion method that will assert all items in that object.
        //}

        factoryMethodRoot.createChildToken(
                MessageFormat.format("return a{0};",
                        object.getClass().getSimpleName()),
                "");
    }

    private Token writeFactoryCreationMethod(T obj, Token token) {
        token.createChildToken(obj.getClass().getName() + " a" + obj.getClass().getSimpleName() + " = create" + obj.getClass().getSimpleName() + "();", null);
        importsTokenRoot.createChildToken("import " + obj.getClass().getName() + ";", null);
        return classToken.createChildToken(MessageFormat.format("public {0} create{1}()'{'", obj.getClass().getSimpleName(), obj.getClass().getSimpleName()), "}");
    }

    private List<Field> createFieldList(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        }
        return fields;
    }

    public void printCode(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        FileWriter fstream = new FileWriter(path);
        BufferedWriter info = new BufferedWriter(fstream);
        writeToken(info, rootToken);
        info.close();
    }

    private void writeToken(BufferedWriter info, Token token) throws IOException {
        if (!Strings.isNullOrEmpty(token.getTokenStart())) {
            info.write(token.getTokenStart());
            info.newLine();
        }
        if (null != token.getChildTokens() && token.getChildTokens().size() > 0) {
            for (Token nestedToken : token.getChildTokens()) {
                writeToken(info, nestedToken);
            }
        }

        if (Strings.isNullOrEmpty(token.getTokenEnd()))
            return;

        info.write(token.getTokenEnd());

        if (token.isNewLineAfterTokenEnd())
            info.newLine();
    }

    //    public static boolean isCollection(Object ob) {
//        return ob instanceof Collection || ob instanceof Map || ob.getClass().isArray();
//    }
    public static boolean isClassCollection(Class<?> c) {
        return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c) || c.isArray();
    }
    public static boolean isPrimitiveOrWrapper(Class<?> type){
        return ClassUtils.isPrimitiveOrWrapper(type) || type.equals(String.class);
    }
}
