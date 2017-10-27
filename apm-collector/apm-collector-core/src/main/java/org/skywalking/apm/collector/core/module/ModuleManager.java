/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.collector.core.module;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * The <code>ModuleManager</code> takes charge of all {@link Module}s in collector.
 *
 * @author wu-sheng
 */
public class ModuleManager {
    private Map<String, Module> loadedModules = new HashMap<>();

    /**
     * Init the given modules
     *
     * @param applicationConfiguration
     */
    public void init(
        ApplicationConfiguration applicationConfiguration) throws ModuleNotFoundException, ProviderNotFoundException, ServiceNotProvidedException {
        String[] moduleNames = applicationConfiguration.moduleList();
        ServiceLoader<Module> moduleServiceLoader = ServiceLoader.load(Module.class);
        LinkedList<String> moduleList = new LinkedList(Arrays.asList(moduleNames));
        for (Module module : moduleServiceLoader) {
            for (String moduleName : moduleNames) {
                if (moduleName.equals(module.name())) {
                    Module newInstance;
                    try {
                        newInstance = module.getClass().newInstance();
                    } catch (InstantiationException e) {
                        throw new ModuleNotFoundException(e);
                    } catch (IllegalAccessException e) {
                        throw new ModuleNotFoundException(e);
                    }
                    newInstance.prepare(this, applicationConfiguration.getModuleConfiguration(moduleName));
                    loadedModules.put(moduleName, newInstance);
                    moduleList.remove(moduleName);
                }
            }
        }

        if (moduleList.size() > 0) {
            throw new ModuleNotFoundException(moduleList.toString() + " missing.");
        }

        for (Module module : loadedModules.values()) {
            module.start(this, applicationConfiguration.getModuleConfiguration(module.name()));
        }

        for (Module module : loadedModules.values()) {
            module.notifyAfterCompleted();
        }
    }

    public boolean has(String moduleName) {
        return loadedModules.get(moduleName) != null;
    }

    public Module find(String moduleName) throws ModuleNotFoundException {
        Module module = loadedModules.get(moduleName);
        if (module != null)
            return module;
        throw new ModuleNotFoundException(moduleName + " missing.");
    }
}