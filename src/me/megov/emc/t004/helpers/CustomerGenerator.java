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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.megov.emc.t004.entities.Customer;
import me.megov.emc.t004.entities.IPvXRange;
import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.exceptions.T004BadDataException;

/**
 *
 * @author megov
 */

public class CustomerGenerator {

    public static final int START_V4_NETWORK = 0x40000000;
    public static final int START_V4_NETMASK_BITS = 2;
    public static final int START_V6_NETWORK_HI = 0x10000000;
    public static final int START_V6_NETWORK_LO = 0x0;
    public static final int START_V6_NETMASK_BITS = 2;

    public static final int CHILDREN_FACTOR = 5;

    private final int v4Count;
    private final int v6Count;
    private final List<String> holder = new ArrayList<>();

/*    public class IPv4WithMask {

        int ipv4 = 0;
        int mask = 0;

        public IPv4WithMask(int _ipv4, int _mask) {
            ipv4 = _ipv4;
            mask = _mask;
        }
    }
  */
    
    public class IPvXWithMask {

        IPvXTuple addr = new IPvXTuple(0, 0);
        int mask = 0;

        public IPvXWithMask(IPvXTuple _addr, int _mask) {
            addr = _addr;
            mask = _mask;
        }
        
        public IPvXRange getRange() throws T004BadDataException {
            return new IPvXRange(addr.getLowerBound(mask),
                                 addr.getUpperBound(mask));
        }
        
        @Override
        public String toString() {
            return addr.toString()+"/"+mask;
        }
        
    }
    
    public CustomerGenerator(int _v4Count, int _v6Count) {
        this.v4Count = _v4Count;
        this.v6Count = _v6Count;
    }
    
    public int getRandomV4Subnet(
            int _level,
            int _counter,
            IPvXWithMask _parent,
            List<String> _holder,
            HashSet<String> _duplicates,
            PrintStream _ps) throws T004BadDataException {
        int counter = _counter;
        String tab = "";
        if (_level > 0) {
            tab = new String(new char[_level]).replace('\0', '.');
        }
        
        //random address
        IPvXTuple randAddr = new IPvXTuple(Math.round(Math.random()*0xFFFFFFFFL) & 0xFFFFFFFFL);

        //new network mask for this level
        int thisMask = _parent.mask + _level;
        
        //return, if we overcome the most small /32 subnets
        if (thisMask > 32) {
            return counter;
        }

        //create new subnet address 
        IPvXTuple randSubnetAddr = randAddr;
        if (_parent.mask > 0) {
            randSubnetAddr = randAddr.getHostPart(_parent.mask);
        }
        
        IPvXTuple fullAddr = _parent.addr.orWith(randSubnetAddr.getLowerBound(thisMask));
        
        //check for dublications among siblings
        String netSpec = fullAddr.toString() + "/" + thisMask;
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
                        new IPvXWithMask(fullAddr, thisMask),
                        _holder,
                        dups,
                        _ps);
            }
        }
        return counter;
    }

    
    public int getRandomV6Subnet(int _level,
            int _counter,
            IPvXWithMask _parent,
            List<String> _holder,
            HashSet<String> _duplicates,
            PrintStream _ps) throws T004BadDataException {

        int counter = _counter;
        String tab = "";
        if (_level > 0) {
            tab = new String(new char[_level]).replace('\0', '.');
        }
        
        //random address part inside a parent network mask
        IPvXTuple randAddr = new IPvXTuple(); //IPvXTuple.getRandomV6Segment(_parent.addr, _parent.mask);
        //new network mask for this level
        int thisMask = _parent.mask + _level; //WAS (int) Math.round(Math.random() * (31 -_parent.mask)) + 1  + ;
        
        //return, if we overcome the most small /126 subnets
        if (thisMask > 126) {
            return counter;
        }
        
        //create new subnet address 
        IPvXTuple randSubnetAddr = new IPvXTuple(randAddr);
        if (_parent.mask > 0) {
            //randSubnetAddr = IPvXTuple.getHostPart(randAddr, _parent.mask);
            throw new T004BadDataException("NEED TO REIMPLEMENT!");
        }
        /*
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
                        new IPv4WithMask((int) fullAddr, thisMask),
                        _holder,
                        dups,
                        _ps);
            }
        }*/
        return counter;
    }
    
    private List<IPvXWithMask> getTopLevelV4Networks(int _count) throws T004BadDataException {
        ArrayList<IPvXWithMask> topNetworks = new ArrayList<>(_count);
        int iterations = 0;
        while ((topNetworks.size()<_count) && (iterations<100000)) {
            int thisMask = (int)Math.round(Math.random()*6)+2;
            long randAddr = Math.round(Math.random()*0xFEL+1) << 24;
            IPvXTuple addr = new IPvXTuple(randAddr);
            IPvXRange range = new IPvXRange(addr.getLowerBound(thisMask), 
                                            addr.getUpperBound(thisMask));
            boolean isIntersected = false;
            for (IPvXWithMask net:topNetworks) {
                IPvXRange netRange = net.getRange();
                if (range.isIntersectWith(netRange)) {
                    isIntersected = true;
                    break;
                }
            }
            if (!isIntersected) {
                topNetworks.add(new IPvXWithMask(addr.getLowerBound(thisMask), thisMask));
            }
            iterations++;
        }
        return topNetworks;
    }
    
    public List<String> generate(PrintStream _ps) throws T004BadDataException {
        holder.clear();
        int custNo = 0;
        HashSet<String> dups = new HashSet<>();
        
        List<IPvXWithMask> topNetworks =  getTopLevelV4Networks(v4Count);
        for (IPvXWithMask ipm:topNetworks) {
            String netSpec = ipm.addr.toString() + "/" + ipm.mask;
            dups.add(netSpec);
            String line = String.format("%-20s", netSpec) + 
                                        " CUST"+String.format("%04d", custNo++) + ".0";
            holder.add(line);
            if (_ps != null) _ps.println(line);
        }
        for (IPvXWithMask ipm:topNetworks) {        
            custNo = getRandomV4Subnet(1,
                    custNo,
                    ipm,
                    holder,
                    dups,
                    _ps);
        }
/*
        for (int i = 0; i < v6Count; i++) {
            custNo = getRandomV6Subnet(level,
                    custNo,
                    new IPv6WithMask(new IPvXTuple(START_V6_NETWORK_HI, START_V6_NETWORK_LO), START_V6_NETMASK_BITS),
                    holder,
                    dups,
                    _ps);
        } 
*/
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
