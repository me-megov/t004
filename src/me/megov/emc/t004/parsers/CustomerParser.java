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
package me.megov.emc.t004.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.megov.emc.t004.entities.CustomerLine;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.exceptions.T004FormatException;

/**
 *
 * @author megov
 */
public class CustomerParser {
    
    private static final String CUST_REGEXP = "^\\s*(\\S+)/(\\d+)\\s+([A-Za-z0-9\\.]+)\\s*$";
    private static final Pattern CUST_PATTERN = Pattern.compile(CUST_REGEXP);    
    
    public CustomerLine parseLine(int lineNum, String line) throws T004Exception {
        Matcher mat = CUST_PATTERN.matcher(line);
        if (!mat.matches() && (mat.groupCount()!=3)) {
            throw new T004FormatException("Error parsing customers at line "+lineNum+" with value: "+line);
        }
        String netAddr = mat.group(1);
        String mask = mat.group(2);
        String custName = mat.group(3);
        return new CustomerLine(netAddr, Integer.valueOf(mask), custName);
    }
    
    public static List<CustomerLine> netMaskSort(List<CustomerLine> _in) {
        _in.sort((CustomerLine o1, CustomerLine o2) -> Integer.compare(o1.getMask(), o2.getMask()));
        return _in;
    }
  
    public List<CustomerLine> readFrom(List<String> _listCl) throws T004Exception  {
        List<CustomerLine> list = new ArrayList<>();
        int i = 1;
        for (String line: _listCl) {
            CustomerLine custLine = parseLine(i++, line);
            list.add(custLine);
        }
        return netMaskSort(list);
    }
    
    
    public List<CustomerLine> readFrom(BufferedReader _breader) throws T004Exception  {
        String line;
        List<CustomerLine> list = new ArrayList<>();
        int i = 1;
        try {
        while ((line = _breader.readLine()) != null) {
            CustomerLine custLine = parseLine(i++, line);
            list.add(custLine);
        }
        return netMaskSort(list);
        } catch (IOException ex) {
            throw new T004Exception(ex);
        } 
    }
    
}
