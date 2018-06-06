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
package me.megov.emc.t004.helpers;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.CustomerLine;
import me.megov.emc.t004.exceptions.T004Exception;
import me.megov.emc.t004.parsers.CustomerParser;

/**
 *
 * @author megov
 */
public class CustomerTreeHelper {

    public static void printTree(Customer _custRoot, int _level, PrintStream _ps) {
        String tab = "";
        if (_level > 0) {
            tab = new String(new char[_level]).replace('\0', '.');
        }
        Iterator<Map.Entry<String, Customer>> it = _custRoot.getSubCustomers().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Customer> ent = it.next();
            _ps.print(tab);
            _ps.print(ent.getKey());
            _ps.println("|");
            printTree(ent.getValue(), _level + 1, _ps);
        }
    }

    public static List<String> generateCustomerList(int _custCount, PrintStream _debugOut) throws T004Exception {
        //Customer generator gives us ordered list, where parents always before childred
        //It can be wrong fot actual input file.
        List<String> custStrings = new CustomerGenerator(_custCount, 0).generate(_debugOut);
        //So to make things worse, we textually sort our customer list for to break
        //parent-child ordering.
        Collections.sort(custStrings);
        return custStrings;
    }

    public static Customer generateCustomerTree(int _custCount, Customer _custRoot, PrintStream _debugOut) throws T004Exception {
        return generateCustomerTree(_custCount, generateCustomerList(_custCount, _debugOut), _custRoot);
    }

    public static Customer generateCustomerTree(int _custCount, List<String> _custStrings, Customer _custroot) throws T004Exception {

        CustomerParser custParser = new CustomerParser();
        List<CustomerLine> custLines = custParser.readFrom(_custStrings);

        Iterator<CustomerLine> it = custLines.iterator();
        while (it.hasNext()) {
            _custroot.addSubCustomer(it.next());
        }
        return _custroot;
    }

}
