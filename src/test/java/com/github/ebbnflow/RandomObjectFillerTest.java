package com.github.ebbnflow;

import org.junit.jupiter.api.Test;

public class RandomObjectFillerTest {

    @Test
    public void shouldFillObject() throws Exception {
        RandomObjectFiller randomObjectFiller = new RandomObjectFiller();
        SimplePojo simplePojo = randomObjectFiller.createAndFill(SimplePojo.class);
        System.out.println(simplePojo.toString());
    }
}
