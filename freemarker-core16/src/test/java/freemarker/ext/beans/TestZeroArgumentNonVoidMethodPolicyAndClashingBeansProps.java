/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.ext.beans;

import java.io.IOException;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.test.TemplateTest;

public class TestZeroArgumentNonVoidMethodPolicyAndClashingBeansProps extends TemplateTest {
    @Override
    protected Configuration createConfiguration() throws Exception {
        Configuration cfg = super.createConfiguration();
        // Don't use default, as then the object wrapper is a shared static mutable object:
        cfg.setIncompatibleImprovements(Configuration.VERSION_2_3_33);
        cfg.setBooleanFormat("c");
        return cfg;
    }

    @Test
    public void testAdditionalBeanPropertyReadMethod() throws TemplateException, IOException {
        addToDataModel("r", new RecordWithAdditionalBeanPropertyReadMethod(true, "N"));

        assertOutput("${r.active}", "true");
        assertOutput("${r.active()}", "true");
        assertErrorContains("${r.isActive}", "convertible to string");
        assertOutput("${r.isActive()}", "true");

        assertOutput("${r.name}", "N");
        assertOutput("${r.name()}", "N");
        assertErrorContains("${r.getName}", "convertible to string");
        assertOutput("${r.getName()}", "N");
    }

    @Test
    public void testAccidentalBeanPropertyCreatorComponent() throws TemplateException, IOException {
        addToDataModel("r", new RecordWithAccidentalBeanPropertyCreatorComponent(true, "N"));

        assertOutput("${r.active}", "true");
        assertErrorContains("${r.active()}", "Expected a method");
        assertErrorContains("${r.isActive}", "convertible to string"); // Not intuitive...
        assertOutput("${r.isActive()}", "true");

        assertOutput("${r.name}", "N");
        assertErrorContains("${r.name()}", "Expected a method");
        assertErrorContains("${r.getName}", "convertible to string"); // Not intuitive...
        assertOutput("${r.getName()}", "N");
    }

    public record RecordWithAdditionalBeanPropertyReadMethod(boolean active, String name) {
        public boolean isActive() {
            return active();
        }
        public String getName() {
            return name();
        }
    }

    public record RecordWithAccidentalBeanPropertyCreatorComponent(boolean isActive, String getName) {
    }
}
