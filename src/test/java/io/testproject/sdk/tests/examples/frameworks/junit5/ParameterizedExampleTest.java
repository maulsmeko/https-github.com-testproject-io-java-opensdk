/*
 * Copyright (c) 2020 TestProject LTD. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.testproject.sdk.tests.examples.frameworks.junit5;

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.interfaces.parameterization.TestProjectParameterizer;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.By;

import java.io.IOException;

public final class ParameterizedExampleTest {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver();
    }

    @ParameterizedTest
    @DisplayName("Example Login Test (Parameterized)")
    @ArgumentsSource(TestProjectParameterizer.class)
    void testExample(final String name) {
        driver.navigate().to("https://example.testproject.io/");
        driver.findElement(By.cssSelector("#name")).sendKeys(name);
        driver.findElement(By.cssSelector("#password")).sendKeys("12345");
        driver.report().step("Login Information provided", true);
        driver.findElement(By.cssSelector("#login")).click();
        driver.report().step("Logged in successfully",
                driver.findElement(By.cssSelector("#logout")).isDisplayed(), true);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
