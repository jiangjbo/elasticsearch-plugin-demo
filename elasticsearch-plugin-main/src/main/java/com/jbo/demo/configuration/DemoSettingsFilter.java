/*
 * Copyright 2015 floragunn UG (haftungsbeschränkt)
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
 */

package com.jbo.demo.configuration;

import com.jbo.demo.util.ConfigConstants;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class DemoSettingsFilter {

    @Inject
    public DemoSettingsFilter(final SettingsFilter settingsFilter, final Settings settings) {
        super();
        String searchguardIndex = settings.get(ConfigConstants.SG_CONFIG_INDEX, ConfigConstants.SG_DEFAULT_CONFIG_INDEX);
        settingsFilter.addFilter(String.format("%s.*", searchguardIndex));
    }
    
}
