package com.github.ebbnflow;

import org.junit.jupiter.api.Test;

public class UnitTestGeneratorTest {
    @Test
    public void shouldPrintCode() throws Exception {
        RandomObjectFiller randomObjectFiller = new RandomObjectFiller();
        SimplePojo simplePojo = randomObjectFiller.createAndFill(SimplePojo.class);
        UnitTestGenerator<SimplePojo> obj = new UnitTestGenerator<>(simplePojo);
        obj.generateTest();
    }

    @Test
    public void shouldPrintCodeWithNullString() throws Exception {
        RandomObjectFiller randomObjectFiller = new RandomObjectFiller();
        SimplePojo simplePojo = randomObjectFiller.createAndFill(SimplePojo.class);

        simplePojo.setMyString(null);
        UnitTestGenerator<SimplePojo> obj = new UnitTestGenerator<>(simplePojo);
        obj.generateTest();
    }

    @Test
    public void shouldPrintCodeWithNullList() throws Exception {
        RandomObjectFiller randomObjectFiller = new RandomObjectFiller();
        SimplePojo simplePojo = randomObjectFiller.createAndFill(SimplePojo.class);

        simplePojo.setMyNestedList(null);
        UnitTestGenerator<SimplePojo> obj = new UnitTestGenerator<>(simplePojo);
        obj.generateTest();
    }
}
