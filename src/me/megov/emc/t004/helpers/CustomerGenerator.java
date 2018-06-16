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

    private final int childrenFactor;
    
    private final List<String> holder = new ArrayList<>();

    public class TopNetworkGenParams {

        private int netMaskMultilier = 0;
        private int netMaskAddition = 0;
        private long highNetworkWordMask = 0;
        private int highNetworkWordShift = 0;
        private boolean ipv4 = false;

        public TopNetworkGenParams(boolean _ipv4) {
            this.ipv4 = _ipv4;
        }

        public int getNetMaskMultilier() {
            return netMaskMultilier;
        }

        public TopNetworkGenParams setNetMaskMultilier(int netMaskMultilier) {
            this.netMaskMultilier = netMaskMultilier;
            return this;
        }

        public int getNetMaskAddition() {
            return netMaskAddition;
        }

        public TopNetworkGenParams setNetMaskAddition(int netMaskAddition) {
            this.netMaskAddition = netMaskAddition;
            return this;
        }

        public long getHighNetworkWordMask() {
            return highNetworkWordMask;
        }

        public TopNetworkGenParams setHighNetworkWordMask(long highNetworkWordMask) {
            this.highNetworkWordMask = highNetworkWordMask;
            return this;
        }

        public int getHighNetworkWordShift() {
            return highNetworkWordShift;
        }

        public TopNetworkGenParams setHighNetworkWordShift(int highNetworkWordShift) {
            this.highNetworkWordShift = highNetworkWordShift;
            return this;
        }

        public boolean isIpv4() {
            return ipv4;
        }
        
        public int getRandomNetworkMask() {
            return (int) Math.round(Math.random()*netMaskMultilier) + netMaskAddition;
        }
        
        public IPvXTuple getRandomNetworkAddress() {
            IPvXTuple addr;
            long randAddr = Math.round(Math.random()*highNetworkWordMask+1) << highNetworkWordShift;
            if (isIpv4()) {
                addr = new IPvXTuple(randAddr);
            } else {
                addr = new IPvXTuple(randAddr, 0L);
            }     
            return addr;
        }
    }

    public CustomerGenerator(int _childrenFactor) {
        this.childrenFactor = _childrenFactor;
    }

    public void getRandomSubnet(
            int _level,
            SubNetworkGenParams _subNetParams,            
            IPvXTupleWithMask _parent,
            List<String> _holder,
            HashSet<String> _duplicates,
            PrintStream _ps) throws T004BadDataException {
        int counter = _subNetParams.getCounter();
        String tab = "";
        if (_level > 0) {
            tab = new String(new char[_level]).replace('\0', '.');
        }
        //random address
        IPvXTuple randAddr;
        if (_subNetParams.isIpv4()) {
            randAddr = IPvXTuple.getV4RandomAddr();
        } else {
            randAddr = IPvXTuple.getV6RandomAddr();
        }
        //new network mask for this level
        int thisMask = _parent.getMask() + _level;
        //return, if we overcome the most small /32 subnets
        if (thisMask > _subNetParams.getMaxNetworkMaskValue()) return;

        //create new subnet address 
        IPvXTuple randSubnetAddr = randAddr.getHostPart(_parent.getMask());
        IPvXTuple fullAddr = _parent.getAddr().orWith(randSubnetAddr.getLowerBound(thisMask));

        //check for dublications among siblings
        String netSpec = fullAddr.toString() + "/" + thisMask;
        if (_duplicates.contains(netSpec)) return;
        _duplicates.add(netSpec);

        //form result customer string and store it
        String line = String.format("%s %s%07d.%d",netSpec, _subNetParams.isIpv4()?"V4CUST":"V6CUST", counter,_level);
        counter++;
        _subNetParams.setCounter(counter);
        holder.add(line);
        if (_level>_subNetParams.getMaxLevel()) _subNetParams.setMaxLevel(_level);

        //how many children will be for the newly generated subnet
        int randChild = (int) (this.childrenFactor - Math.round(Math.random() * _level));

        //debug report if stream is supplied
        if (_ps != null) {
            _ps.println(tab + line + " CH:" + randChild);
        }

        //recursive children generation
        if (randChild > 0) {
            HashSet<String> dups = new HashSet<>();
            for (int i = 0; i < randChild; i++) {
                getRandomSubnet(
                        _level+1,
                        _subNetParams,
                        new IPvXTupleWithMask(fullAddr, thisMask),
                        _holder,
                        dups,
                        _ps);
            }
        }
    }

    private void generateTopLevelNetworks(int _count,
            List<IPvXTupleWithMask> _nelist,
            TopNetworkGenParams _netParams) throws T004BadDataException {
        _nelist.clear();
        int iterations = 0;
        while ((_nelist.size() < _count) && (iterations < 100000)) {
            int thisMask = _netParams.getRandomNetworkMask();
            IPvXTuple addr = _netParams.getRandomNetworkAddress();
            IPvXRange range = new IPvXRange(
                    addr.getLowerBound(thisMask),
                    addr.getUpperBound(thisMask)
                    );
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

    private void processTopLevelNetwork(SubNetworkGenParams _subNetParams,
            PrintStream _ps,
            List<IPvXTupleWithMask> _genList,
            HashSet<String> _dups) {
        int currNo = _subNetParams.getCounter();
        for (IPvXTupleWithMask ipm : _genList) {
            String netSpec = ipm.getAddr().toString() + "/" + ipm.getMask();
            _dups.add(netSpec);
            String line = String.format("%s %s%07d.%d",netSpec, _subNetParams.isIpv4()?"V4CUST":"V6CUST", currNo++, 0);
            holder.add(line);
            if (_ps != null) {
                _ps.println(line);
            }
        }
        _subNetParams.setCounter(currNo);
    }

    public List<String> generate(PrintStream _ps,
            List<IPvXTupleWithMask> _topv4Networks,
            SubNetworkGenParams _subnetv4Params,
            List<IPvXTupleWithMask> _topv6Networks,
            SubNetworkGenParams _subnetv6Params
    ) throws T004BadDataException {
        holder.clear();
        int custNo = 0;
        HashSet<String> dups = new HashSet<>();
        generateTopLevelNetworks(_subnetv4Params.getTopLevelCustomerCount(), 
                                 _topv4Networks, 
                                 new TopNetworkGenParams(true)
                                        .setNetMaskMultilier(6)
                                        .setNetMaskAddition(2)
                                        .setHighNetworkWordMask(0xFF)
                                        .setHighNetworkWordShift(24)
                                );
        generateTopLevelNetworks(_subnetv6Params.getTopLevelCustomerCount(), 
                                 _topv6Networks, 
                                 new TopNetworkGenParams(false)
                                        .setNetMaskMultilier(16)
                                        .setNetMaskAddition(12)
                                        .setHighNetworkWordMask(0xFFFFFFFFFFL)
                                        .setHighNetworkWordShift(32)
                                 );
        _subnetv4Params.setCounter(custNo);
        processTopLevelNetwork(_subnetv4Params, _ps, _topv4Networks, dups);        
        _subnetv6Params.setCounter(_subnetv4Params.getCounter());
        processTopLevelNetwork(_subnetv6Params, _ps, _topv6Networks, dups);
        _subnetv4Params.setCounter(_subnetv6Params.getCounter());
        _subnetv4Params.setCounter(custNo);
        for (IPvXTupleWithMask ipm : _topv4Networks) {
            getRandomSubnet(1, _subnetv4Params, ipm, holder, dups, _ps);
        }
        _subnetv6Params.setCounter(_subnetv4Params.getCounter());
        for (IPvXTupleWithMask ipm : _topv6Networks) {
            getRandomSubnet(1, _subnetv6Params, ipm, holder, dups, _ps);
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
