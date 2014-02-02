/**
 * Directory Commons JSF bundle
 * Plugin Faces Managed Bean Filter
 * Copyright (C) 2013 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.spectral.cc.core.injector.commons.tools;

import com.spectral.cc.core.injector.commons.consumer.InjectorPluginFacesMBeanRegistryConsumer;

import javax.servlet.*;
import java.io.IOException;

/**
 * This servlet filter is an helper to add new Managed Bean coming from CC plugin to the injecto servlet context thanks the injector plugin faces mbean registry consumer.<br/>
 * It must be configured properly in the web.xml file :<br/><br/>
 * <pre>
 *         <!-- Injector Plugin Faces Managed Bean Registry Filter -->
 *         <filter>
 *              <filter-name>InjectorPluginFacesMBeanRegistryFilter</filter-name>
 *              <filter-class>com.spectral.cc.core.injector.commons.tools.DirectoryPluginFacesMBeanFilter</filter-class>
 *         </filter>
 *         <filter-mapping>
 *              <filter-name>InjectorPluginFacesMBeanRegistryFilter</filter-name>
 *              <url-pattern>*.jsf</url-pattern>
 *         </filter-mapping>
 * </pre>
 */
public class InjectorPluginFacesMBeanFilter implements Filter {

    /**
     * The filter configuration object we are associated with. If this value is null, this filter instance is not currently
     * configured.
     */
    protected FilterConfig filterConfig = null;

    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * Ask the injector plugin faces managed bean registry to add registered faces managed bean to the injector servlet context,
     * and then pass control to the next filter
     *
     * @param request
     * @param response
     * @param chain
     *
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
        InjectorPluginFacesMBeanRegistryConsumer.getInstance().getInjectorPluginFacesMBeanRegistry().addPluginFacesMBeanConfigsToServletContext();

        // Pass control on to the next filter
        chain.doFilter(request, response);
    }

    /**
     * Register the injector servlet context into the injector plugin faces managed bean registry
     *
     * @param filterConfig
     *
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        while(InjectorPluginFacesMBeanRegistryConsumer.getInstance().getInjectorPluginFacesMBeanRegistry()==null)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        InjectorPluginFacesMBeanRegistryConsumer.getInstance().getInjectorPluginFacesMBeanRegistry().registerServletContext(filterConfig.getServletContext());
    }
}
