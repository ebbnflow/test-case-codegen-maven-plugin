package com.github.ebbnflow;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomObjectFillerTest {

    private static final Logger log  = LoggerFactory.getLogger(RandomObjectFillerTest.class);

    @Test
    public void shouldFillObject() throws Exception {
        RandomObjectFiller randomObjectFiller = new RandomObjectFiller();
        SimplePojo simplePojo = randomObjectFiller.createAndFill(SimplePojo.class);
        log.debug(simplePojo.toString());
    }
}
