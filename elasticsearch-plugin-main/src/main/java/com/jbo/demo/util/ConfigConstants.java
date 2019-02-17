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

package com.jbo.demo.util;

public class ConfigConstants {
    
     
    public static final String SG_CONFIG_PREFIX = "_sg_";
    
    public static final String SG_CHANNEL_TYPE = SG_CONFIG_PREFIX+"channel_type";
    


    public static final String SG_CONF_REQUEST_HEADER = SG_CONFIG_PREFIX + "conf_request";
    
    public static final String SG_REMOTE_ADDRESS = SG_CONFIG_PREFIX+"remote_address";

    public static final String SG_XFF_DONE = SG_CONFIG_PREFIX+"xff_done";

    public static final String SG_CONFIG_INDEX = "searchguard.config_index_name";

    public static final String SG_DEFAULT_CONFIG_INDEX = "searchguard";

    public static final String SG_DEFAULT_CONFIG_INDEX_TYPE_INTERNAL_USER = "internalusers";
    public static final String SG_DEFAULT_CONFIG_INDEX_TYPE_CONFIG = "config";
    public static final String SG_DEFAULT_CONFIG_INDEX_TYPE_ROLES = "roles";
    public static final String SG_DEFAULT_CONFIG_INDEX_TYPE_ROLES_MAPPINGS = "rolesmapping";
    public static final String SG_DEFAULT_CONFIG_INDEX_TYPE_ACTION_GROUPS = "actiongroups";
    public static final String SG_DEFAULT_CONFIG_INDEX_TYPE_WHITELIST = "whitelist";


    public static final String SG_WHITELIST = SG_CONFIG_PREFIX + "whitelist";

    public static final String SG_ADMIN = SG_CONFIG_PREFIX + "admin";
    public static final String DEFAULT_ADMIN = "admin";
}
