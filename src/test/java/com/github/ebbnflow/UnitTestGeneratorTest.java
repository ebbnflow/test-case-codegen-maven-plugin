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
}
