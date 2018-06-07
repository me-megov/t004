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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static me.megov.emc.t004.config.ConfigParam.values;
import me.megov.emc.t004.exceptions.T004FormatException;

/**
 *
 * @author megov
 */
public class Config {

    private static final String CFG_REGEXP = "^\\s*(\\S+)\\s+(.+)\\s*$";
    private static final Pattern CFG_PATTERN = Pattern.compile(CFG_REGEXP);

    private HashMap<String, String> params = new HashMap<>(5);

    public Config(List<String> _configList) throws T004FormatException {
        int i = 0;
        for (String line : _configList) {
            processConfigLine(i++, line);
        }
        completeConfigDefaults();
    }

    public Config(File _configFile) throws T004FormatException {
        try {
            BufferedReader cfgReader = new BufferedReader(new FileReader(_configFile));
            String line;
            int i = 1;
            while ((line = cfgReader.readLine()) != null) {
                processConfigLine(i++, line);
            }
            completeConfigDefaults();
        } catch (IOException ex) {
            throw new T004FormatException(ex);
        }
    }

    public Config(String[] _configLines) throws T004FormatException {
        this(Arrays.asList(_configLines));
    }

    public Config(String _configFileName) throws T004FormatException {
        this(new File(_configFileName));
    }
    
    public Config() {
        completeConfigDefaults();
    }
    
    public void mergeCmdLine(String[] _cmdLine) throws T004FormatException {
        for (String s:_cmdLine) {
            if (s.startsWith("--")) {
                String[] arr = s.split("=");
                if (arr.length!=2) throw new T004FormatException("Bad cmdline parameter: "+s);
                arr[0] = arr[0].replaceFirst("--", "");
                String cp = params.get(arr[0]);
                if (cp==null) throw new T004FormatException("Cmdline parameter: "+arr[0]+" not found");
                params.put(arr[0],arr[1]);
                }
        }
    }
        
    private void completeConfigDefaults() {
        for (ConfigParam paramEnum : values()) {
            if (params.get(paramEnum.getName()) == null) {
                params.put(paramEnum.getName(), paramEnum.getDefValue());
            }
        }
    }
    
    private void processConfigLine(int lineNum, String line) throws T004FormatException {
        Matcher mat = CFG_PATTERN.matcher(line);
        if ((!mat.matches()) || (mat.groupCount() != 2)) {
            throw new T004FormatException("Сonfig line " + lineNum + " is invalid:" + line);
        }
        String paramName = mat.group(1);
        String paramValue = mat.group(2);
        ConfigParam.getParamByName(paramName);//check for parameter existence
        params.put(paramName, paramValue);
    }

    public String getValue(ConfigParam _param) throws T004FormatException {
        String val = params.get(_param.getName());
        if (val == null) {
            throw new T004FormatException("Config parameter " + _param.getName() + " is not set");
        } else {
            return val;
        }
    }
    
    public int getIntValue(ConfigParam _param) throws T004FormatException {
        String val = getValue(_param);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            throw new T004FormatException("Invalid int parameter " + _param.getName(),ex);
        }
    }
    

}
