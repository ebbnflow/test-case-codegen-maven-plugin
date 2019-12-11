package com.github.ebbnflow;

import org.junit.jupiter.api.Test;

public class UnitTestGeneratorTest {
    @Test
    public void shouldPrintCode() throws Exception {
        UnitTestGenerator<SimplePojo> obj = new UnitTestGenerator<>();
        RandomObjectFiller randomObjectFiller = new RandomObjectFiller();
        SimplePojo simplePojo = randomObjectFiller.createAndFill(SimplePojo.class);
        obj.generateTest(simplePojo);
    }
}
