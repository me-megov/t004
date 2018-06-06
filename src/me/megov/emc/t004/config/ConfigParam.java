/*
 * Copyright 2018 megov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.megov.emc.t004.config;

import java.util.HashMap;
import me.megov.emc.t004.exceptions.T004FormatException;

/**
 *
 * @author megov
 */
public enum ConfigParam {

    DATADIR(        "DATADIR",      "/data",            "Directory for data files (customers, log)"),
    CUSTOMERFILE(   "CUSTOMERFILE", "customers.txt",    "Customers definition filename"),
    LOGFILE(        "LOGFILE",      "log.txt",          "Traffic log filename"),
    OUTPUTDIR(      "OUTPUTDIR",    "/data",            "Output directory for report "),
    OUTPUTFILE(     "OUTPUTFILE",   "report.txt",       "Report filename");

    private final static HashMap<String, ConfigParam> paramLookupByName = new HashMap<>(5);

    static {
        for (ConfigParam param : values()) {
            paramLookupByName.put(param.getName(), param);
        }
    }

    private final String name;
    private final String defValue;
    private final String title;

    ConfigParam(String _name, String _defValue, String _title) {
        this.name = _name;
        this.defValue = _defValue;
        this.title = _title;
    }

    ConfigParam(String _name, String _defValue) {
        this(_name, _defValue, _name);
    }

    ConfigParam(String _name) {
        this(_name, "", _name);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDefValue() {
        return defValue;
    }
    
    public static ConfigParam getParamByName(String _name) throws T004FormatException {
        ConfigParam param = paramLookupByName.get(_name);
        if (param==null) throw new T004FormatException("Invalid config parameter: "+_name);
        else return param;
    }

}
