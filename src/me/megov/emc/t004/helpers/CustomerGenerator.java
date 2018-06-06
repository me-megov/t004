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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */

public class CustomerGenerator {

    public static final int START_V4_NETWORK = 0x00000000;
    public static final int START_V4_NETMASK_BITS = 1;
    public static final int CHILDREN_FACTOR = 9;

    private final int v4Count;
    private final int v6Count;
    private final List<String> holder = new ArrayList<>();

    public class Ipv4WithMask {

        int ipv4 = 0;
        int mask = 0;

        public Ipv4WithMask(int _ipv4, int _mask) {
            ipv4 = _ipv4;
            mask = _mask;
        }
    }

    public CustomerGenerator(int _v4Count, int _v6Count) {
        this.v4Count = _v4Count;
        this.v6Count = _v6Count;
    }

    public int getRandomV4Subnet(int _level,
            int _counter,
            Ipv4WithMask _parent,
            List<String> _holder,
            HashSet<String> _duplicates,
            PrintStream _ps) throws T004BadDataException {

        int counter = _counter;
        String tab = "";
        if (_level > 0) {
            tab = new String(new char[_level]).replace('\0', '.');
        }
        
        //random address part inside a parent network mask
        long randAddr = Math.round(Math.random() * IPv4Helper.getV4BitMask(_parent.mask));
        //new network mask for this level
        int thisMask = _parent.mask + _level; //WAS (int) Math.round(Math.random() * (31 -_parent.mask)) + 1  + ;
        
        //return, if we overcome the most small /32 subnets
        if (thisMask > 32) {
            return counter;
        }
        
        //create new subnet address 
        long randSubnetAddr = randAddr;
        if (_parent.mask > 0) {
            randSubnetAddr = IPv4Helper.toHostPart(randAddr, _parent.mask);
        }
        long fullAddr = IPv4Helper.toLowerBound(_parent.ipv4 | randSubnetAddr, thisMask);
        
        //check for dublications among siblings
        String netSpec = IPv4Helper.getV4AddressString(fullAddr) + "/" + thisMask;
        if (_duplicates.contains(netSpec)) {
            return counter;
        }
        _duplicates.add(netSpec);
        
        //form result customer string and store it
        String line = String.format("%-20s", netSpec) + " CUST" + String.format("%04d", counter) + "." + _level;
        counter++;
        holder.add(line);
        
        //how many children will be for the newly generated subnet
        int randChild = (int) (CHILDREN_FACTOR - Math.round(Math.random() * _level));
        
        //debug report if stream is supplied
        if (_ps != null) _ps.println(tab + line + " CH:" + randChild);
        
        //recursive children generation
        if (randChild > 0) {
            HashSet<String> dups = new HashSet<>();
            for (int i = 0; i < randChild; i++) {
                counter = getRandomV4Subnet(_level + 1, counter,
                        new Ipv4WithMask((int) fullAddr, thisMask),
                        _holder,
                        dups,
                        _ps);
            }
        }
        return counter;
    }

    public List<String> generate(PrintStream _ps) throws T004BadDataException {
        holder.clear();
        int level = 0;
        int custNo = 0;
        HashSet<String> dups = new HashSet<>();
        for (int i = 0; i < v4Count; i++) {
            custNo = getRandomV4Subnet(level,
                    custNo,
                    new Ipv4WithMask(START_V4_NETWORK, START_V4_NETMASK_BITS),
                    holder,
                    dups,
                    _ps);
        }
        return holder;
    }

    public static boolean debugCheckTree(Customer initial, int level, PrintStream _ps, boolean _isBadTreeOrdering) {
        boolean isBadTreeOrdering = _isBadTreeOrdering;
        Iterator<Map.Entry<String, Customer>> it = initial.getSubCustomers().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Customer> ent = it.next();
            String arr[] = ent.getKey().split("\\.", -1);
            if (arr.length > 1) {
                int iLevel = Integer.parseInt(arr[arr.length - 1]);
                if (iLevel != level) {
                    _ps.println(ent.getKey() + " ERROR! Generated " + iLevel + " found at " + level + " level");
                    isBadTreeOrdering = true;
                }
            }
            isBadTreeOrdering = debugCheckTree(ent.getValue(), level + 1, _ps, isBadTreeOrdering);
        }
        return isBadTreeOrdering;
    }
}
