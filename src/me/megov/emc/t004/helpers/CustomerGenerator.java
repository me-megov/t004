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
import me.megov.emc.t004.entities.IPvXTupleWithMask;
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

    public class TopGenParams {

        int maskMul = 0;
        int maskAdd = 0;
        long highAnd = 0;
        int highShift = 0;
        boolean ipv4 = false;

        public TopGenParams(boolean _ipv4, int _maskMul, int _maskAdd, long _highAnd, int _highShift) {
            this.ipv4 = _ipv4;
            this.maskMul = _maskMul;
            this.maskAdd = _maskAdd;
            this.highAnd = _highAnd;
            this.highShift = _highShift;
        }
    }

    public CustomerGenerator(int _v4Count, int _v6Count) {
        this.v4Count = _v4Count;
        this.v6Count = _v6Count;
    }

    public int getRandomSubnet(
            int _level,
            int _counter,
            IPvXTupleWithMask _parent,
            List<String> _holder,
            HashSet<String> _duplicates,
            TopGenParams _par,
            PrintStream _ps) throws T004BadDataException {
        int counter = _counter;
        String tab = "";
        if (_level > 0) {
            tab = new String(new char[_level]).replace('\0', '.');
        }
        //random address
        IPvXTuple randAddr;
        if (_par.ipv4) {
            randAddr = IPvXTuple.getV4RandomAddr();
        } else {
            randAddr = IPvXTuple.getV6RandomAddr();
        }
        //new network mask for this level
        int thisMask = _parent.getMask() + _level;
        //return, if we overcome the most small /32 subnets
        if (thisMask > _par.maskMul) {
            return counter;
        }

        //create new subnet address 
        IPvXTuple randSubnetAddr = randAddr.getHostPart(_parent.getMask());
        IPvXTuple fullAddr = _parent.getAddr().orWith(randSubnetAddr.getLowerBound(thisMask));

        //check for dublications among siblings
        String netSpec = fullAddr.toString() + "/" + thisMask;
        if (_duplicates.contains(netSpec)) {
            return counter;
        }
        _duplicates.add(netSpec);

        //form result customer string and store it
        String line = netSpec + " CUST" + String.format("%04d", counter) + "." + _level;
        counter++;
        holder.add(line);

        //how many children will be for the newly generated subnet
        int randChild = (int) (CHILDREN_FACTOR - Math.round(Math.random() * _level));

        //debug report if stream is supplied
        if (_ps != null) {
            _ps.println(tab + line + " CH:" + randChild);
        }

        //recursive children generation
        if (randChild > 0) {
            HashSet<String> dups = new HashSet<>();
            for (int i = 0; i < randChild; i++) {
                counter = getRandomSubnet(_level + 1, counter,
                        new IPvXTupleWithMask(fullAddr, thisMask),
                        _holder,
                        dups,
                        _par,
                        _ps);
            }
        }
        return counter;
    }

    private void generateTopLevelNetworks(int _count,
            List<IPvXTupleWithMask> _nelist,
            TopGenParams _par) throws T004BadDataException {
        _nelist.clear();
        int iterations = 0;
        while ((_nelist.size() < _count) && (iterations < 100000)) {
            int thisMask = (int) Math.round(Math.random() * _par.maskMul) + _par.maskAdd;
            IPvXTuple addr;
            long randAddr = Math.round(Math.random() * _par.highAnd + 1) << _par.highShift;
            if (_par.ipv4) {
                addr = new IPvXTuple(randAddr);
            } else {
                addr = new IPvXTuple(randAddr, 0L);
            }
            IPvXRange range = new IPvXRange(addr.getLowerBound(thisMask),
                    addr.getUpperBound(thisMask));
            boolean isIntersected = false;
            for (IPvXTupleWithMask net : _nelist) {
                IPvXRange netRange = net.getRange();
                if (range.isIntersectWith(netRange)) {
                    isIntersected = true;
                    break;
                }
            }
            if (!isIntersected) {
                _nelist.add(new IPvXTupleWithMask(addr.getLowerBound(thisMask), thisMask));
            }
            iterations++;
        }
    }

    private int processTopLevelNetwork(int _custNo,
            PrintStream _ps,
            List<IPvXTupleWithMask> _genList,
            HashSet<String> _dups) {
        int currNo = _custNo;
        for (IPvXTupleWithMask ipm : _genList) {
            String netSpec = ipm.getAddr().toString() + "/" + ipm.getMask();
            _dups.add(netSpec);
            String line = String.format("%-20s", netSpec)
                    + " CUST" + String.format("%04d", currNo++) + ".0";
            holder.add(line);
            if (_ps != null) {
                _ps.println(line);
            }
        }
        return currNo;
    }

    public List<String> generate(PrintStream _ps,
            List<IPvXTupleWithMask> _topV4Networks,
            List<IPvXTupleWithMask> _topV6Networks
    ) throws T004BadDataException {
        holder.clear();
        int custNo = 0;
        HashSet<String> dups = new HashSet<>();
        generateTopLevelNetworks(v4Count, _topV4Networks, new TopGenParams(true, 6, 2, 0xFF, 24));
        generateTopLevelNetworks(v6Count, _topV6Networks, new TopGenParams(false, 16, 16, 0xFFFFFFFFFFL, 32));
        custNo = processTopLevelNetwork(custNo, _ps, _topV4Networks, dups);        
        custNo = processTopLevelNetwork(custNo, _ps, _topV6Networks, dups);
        for (IPvXTupleWithMask ipm : _topV4Networks) {
            custNo = getRandomSubnet(1, custNo, ipm, holder, dups, new TopGenParams(true, 32, 0, 0, 0), _ps);
        }
        for (IPvXTupleWithMask ipm : _topV6Networks) {
            custNo = getRandomSubnet(1, custNo, ipm, holder, dups, new TopGenParams(false, 128, 0, 0, 0), _ps);
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
