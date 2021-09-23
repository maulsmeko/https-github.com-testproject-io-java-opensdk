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

/**
 * Extended Web driver for Desktop Browsers.
 */

package io.testproject.sdk;

import io.testproject.sdk.drivers.ReportType;
import io.testproject.sdk.drivers.ReportingDriver;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.net.URL;

/**
 * Utility class to build Driver instances.
 *
 * @param <T> Any of the supported drivers implementing the {@link ReportingDriver} interface.
 */
public final class DriverBuilder<T extends ReportingDriver> {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DriverBuilder.class);

    /**
     * Capabilities required for session initialization.
     */
    private Capabilities builderCapabilities;

    /**
     * Development token.
     */
    private String builderToken;

    /**
     * Agent API base URL (e.g. http://localhost:8585/).
     */
    private URL builderRemoteAddress;

    /**
     * Project name to report.
     */
    private String builderProjectName;

    /**
     * Job name to report.
     */
    private String builderJobName;

    /**
     * Enable / Disable reports flag.
     */
    private boolean builderDisableReports;

    /**
     * Report type to produce.
     */
    private ReportType builderReportType;

    /**
     * The name of the generated report.
     */
    private String builderReportName;

    /**
     * The path of the generated report.
     */
    private String builderReportPath;

    /**
     * The connection timeout to the agent in milliseconds.
     */
    private int builderSocketSessionTimeout;

    /**
     * Initializes a new instance of the builder.
     * Builder can be conveniently used to initialize new Drivers.
     *
     * @param capabilities Capabilities that should be passed to the server for session initialization.
     */
    public DriverBuilder(final Capabilities capabilities) {
        this.builderCapabilities = capabilities;
    }

    /**
     * Set capabilities that should be passed to the server for session initialization.
     * Token can be obtained from the <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page.
     *
     * @param capabilities Capabilities to be set.
     * @return Modified builder.
     */
    public DriverBuilder<T> withCapabilities(final Capabilities capabilities) {
        this.builderCapabilities = capabilities;
        return this;
    }

    /**
     * Set a development token to authorize with the Agent.
     * Token can be obtained from the <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page.
     *
     * @param token Token to be set.
     * @return Modified builder.
     */
    public DriverBuilder<T> withToken(final String token) {
        this.builderToken = token;
        return this;
    }

    /**
     * Set an Agent API base URL (e.g. http://localhost:8585/).
     *
     * @param remoteAddress URL to be set.
     * @return Modified builder.
     */
    public DriverBuilder<T> withRemoteAddress(final URL remoteAddress) {
        this.builderRemoteAddress = remoteAddress;
        return this;
    }

    /**
     * Set Project name to be used for reporting.
     *
     * @param projectName Project name to be set.
     * @return Modified builder.
     */
    public DriverBuilder<T> withProjectName(final String projectName) {
        this.builderProjectName = projectName;
        this.builderDisableReports = false;
        return this;
    }
    /**
     * Set Job name to be used for reporting.
     *
     * @param jobName Job name to be set.
     * @return Modified builder.
     */
    public DriverBuilder<T> withJobName(final String jobName) {
        this.builderJobName = jobName;
        this.builderDisableReports = false;
        return this;
    }

    /**
     * Set flag to enable or disable reports.
     * <em>Note:</em> Once disabled during driver construction, reports can not be enabled later.
     *
     * @param disableReports True to disable or False to enable reports.
     * @return Modified builder.
     */
    public DriverBuilder<T> withReportsDisabled(final boolean disableReports) {
        this.builderDisableReports = disableReports;
        if (disableReports) {
            this.builderProjectName = null;
            this.builderJobName = null;
        }
        return this;
    }

    /**
     * Set report type - cloud, local or both.
     * @param reportType report type - cloud, local or both.
     * @return Modified builder.
     */
    public DriverBuilder<T> withReportType(final ReportType reportType) {
        this.builderReportType = reportType;
        return this;
    }

    /**
     * Set the name of the generated report.
     * @param reportName The name to give to the generated report.
     * @return Modified builder.
     */
    public DriverBuilder<T> withReportName(final String reportName) {
        this.builderReportName = reportName;
        return this;
    }

    /**
     * Set the path of the generated report.
     * @param reportPath The path to the generated report.
     * @return Modified builder.
     */
    public DriverBuilder<T> withReportPath(final String reportPath) {
        this.builderReportPath = reportPath;
        return this;
    }

    /**
     * Set the connection timeout to the agent in milliseconds.
     * @param sessionSocketTimeout timeout to the agent in milliseconds.
     * @return Modified builder.
     */
    public DriverBuilder<T> withSessionSocketTimeout(final int sessionSocketTimeout) {
        this.builderSocketSessionTimeout = sessionSocketTimeout;
        return this;
    }

    /**
     * Builds an instance of the requested driver using set values.
     *
     * @param clazz Required driver type.
     * @return Driver instance.
     */
    public T build(final Class<T> clazz) {

        // Search for an appropriate constructor
        Constructor<T> constructor =
                ConstructorUtils.getMatchingAccessibleConstructor(clazz,
                        URL.class,
                        String.class,
                        this.builderCapabilities.getClass(),
                        String.class,
                        String.class,
                        boolean.class,
                        ReportType.class,
                        String.class,
                        String.class,
                        Integer.class);

        // Make sure that requested constructor was found
        if (constructor == null) {
            LOG.error("{} doesn't have a constructor required by the builder!", clazz.getName());
            throw new WebDriverException("Failed to create an instance of " + clazz.getName());
        }

        // Try creating an instance of the requested driver
        try {
            return constructor.newInstance(
                    builderRemoteAddress,
                    builderToken,
                    builderCapabilities,
                    builderProjectName,
                    builderJobName,
                    builderDisableReports,
                    builderReportType,
                    builderReportName,
                    builderReportPath,
                    builderSocketSessionTimeout);
        } catch (Exception e) {
            throw new WebDriverException("Failed to create an instance of " + clazz.getName(), e);
        }
    }
}
