package com.github.ebbnflow;


import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class MyMojoTest
{

    public MojoRule rule = new MojoRule()
    {
        @BeforeAll
        @Override
        protected void before() throws Throwable
        {
        }

        @AfterAll
        @Override
        protected void after()
        {
        }
    };

    /**
     * @throws Exception if any
     */
    @Test
    public void testSomething()
            throws Exception
    {
        File pom = new File( "target/test-classes/project-to-test/" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        MyMojo myMojo = ( MyMojo ) rule.lookupConfiguredMojo( pom, "touch" );
        assertNotNull( myMojo );
        myMojo.execute();

        File outputDirectory = ( File ) rule.getVariableValueFromObject( myMojo, "outputDirectory" );
        assertNotNull( outputDirectory );
        assertTrue( outputDirectory.exists() );

        File touch = new File( outputDirectory, "touch.txt" );
        assertTrue( touch.exists() );

    }

    /** Do not need the MojoRule. */
    @WithoutMojo
    @Test
    public void testSomethingWhichDoesNotNeedTheMojoAndProbablyShouldBeExtractedIntoANewClassOfItsOwn()
    {
        assertTrue( true );
    }

}

