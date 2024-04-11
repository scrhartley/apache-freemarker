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

package freemarker.template;

import static org.junit.Assert.*;

import javax.servlet.ServletContext;

import org.hamcrest.Matchers;
import org.junit.Test;

import freemarker.cache.WebappTemplateLoader;

public class SetServletContextForTemplateLoadingTest {
    @Test
    public void testSuccess() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        ServletContext servletContext = new MockServletContext();
        cfg.setServletContextForTemplateLoading(servletContext, "foo");
        cfg.setServletContextForTemplateLoading(servletContext, null);
        assertThat(cfg.getTemplateLoader(), Matchers.instanceOf(WebappTemplateLoader.class));
    }

    @Test
    public void testIllegalArgument() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        try {
            cfg.setServletContextForTemplateLoading("bad type", "foo");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), Matchers.containsString("ServletContext"));
        }
    }
}
