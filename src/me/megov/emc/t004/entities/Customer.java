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
package me.megov.emc.t004.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */
public class Customer {
    
    private static Customer _root = null;
    
    private final String name;
    private final Map<String,Customer> subCustomers = new HashMap();
    private final RangeLookupFactory lookupFactory;
    private RangeLookup lookup = null;
    
  
    public static Customer getRootCustomer(RangeLookupFactory _lookupFactory)
    {
        if (_root==null) {
            _root = new Customer("",_lookupFactory);
        }
        return _root;
    };
    
    public Customer(String _name, RangeLookupFactory _lookupFactory) {
        this.name = _name;
        this.lookupFactory = _lookupFactory;
        this.lookup = _lookupFactory.getNewLookup();
    }

    public String getName() {
        return name;
    }
    
    public Customer getCustomerByAddr(IPvXTuple _addr) throws T004BadDataException {
        Customer custByRange = lookup.getEntry(_addr);
        if (custByRange==null) {
            return null;
        } else {
            Customer subCust = custByRange.getCustomerByAddr(_addr);
            if (subCust!=null) return subCust;
            else return custByRange;
        }
    }
    
    public void addSubCustomer(CustomerLine _line) throws T004BadDataException {
        String newCustName = _line.getName();
        Customer subCustomer = subCustomers.get(newCustName);
        Customer custByRange = lookup.getEntry(_line.getLowerAddr());
        if (subCustomer==null) {
            if (custByRange==null) {
                //no subcustomer, nor ranges - add new sibling here
                Customer newCustomer = new Customer(newCustName, lookupFactory);
                subCustomers.put(_line.getName(), newCustomer);
                lookup.put(_line.getNetRange(), newCustomer);
            } else {
                //no subcustomer, but have range - add as a dependent customer
                custByRange.addSubCustomer(_line);
            }
        } else {
            //subCustomersRanges.putCoalescing(_line.getNetRange(), subCustomer);
            lookup.put(_line.getNetRange(), subCustomer);
        }
    }
    
    public int getSubCustomersCount() {
        int cnt = subCustomers.size();
        Iterator<Entry<String,Customer>> it = subCustomers.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String,Customer> ent = it.next();
            cnt+=ent.getValue().getSubCustomersCount();
        }
        return cnt;
    }
       
    public Map<String, Customer> getSubCustomers() {
        return Collections.unmodifiableMap(subCustomers);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
