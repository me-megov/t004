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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.megov.emc.t004.entities.IPvXTuple;
import me.megov.emc.t004.exceptions.T004FormatException;
import me.megov.emc.t004.helpers.IPv4Helper;
import me.megov.emc.t004.helpers.IPv6Helper;

/**
 *
 * @author megov
 */
public class IPvXAddrParser implements Comparable<IPvXAddrParser> {

    private static final String IPV4REGEXP = "^"
            + "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\."
            + "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\."
            + "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\."
            + "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)";

    private static final String IPV6STDREGEXP
            = "^([0-9a-fA-F]{1,4}):"
            + "([0-9a-fA-F]{1,4}):"
            + "([0-9a-fA-F]{1,4}):"
            + "([0-9a-fA-F]{1,4}):"
            + "([0-9a-fA-F]{1,4}):"
            + "([0-9a-fA-F]{1,4}):"
            + "([0-9a-fA-F]{1,4}):"
            + "([0-9a-fA-F]{1,4})$";

    private static final String IPV6COMPRESSEDREGEXP
            = "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$";

    private static final Pattern IPV4PATTERN = Pattern.compile(IPV4REGEXP);
    private static final Pattern IPV6STDPATTERN = Pattern.compile(IPV6STDREGEXP);
    private static final Pattern IPV6COMPRESSEDPATTERN = Pattern.compile(IPV6COMPRESSEDREGEXP);

    private IPvXTuple addr = new IPvXTuple(0, 0);
    private boolean isCompressedV6 = false;

    public IPvXAddrParser(long _hiAddr, long _loAddr) {
        this.addr = new IPvXTuple(_hiAddr, _loAddr);
    }
        
   public IPvXAddrParser(IPvXTuple _addr) {
        this.addr = _addr;
    }
    
    public IPvXAddrParser(String _str) throws T004FormatException {
        fetchAddress(_str);
    }

    
/*
    private String composeV6AddressOld() {
        Long l = addr.getHi();
        StringBuilder sb = new StringBuilder(15);
        for (int i = 48; i >= 0; i -= 16) {
            sb.append(String.format("%X", (int) (l >> i) & 0xFFFF));
            sb.append(":");
        }
        l = addr.getLo();
        for (int i = 48; i >= 0; i -= 16) {
            sb.append(String.format("%X", (int) (l >> i) & 0xFFFF));
            if (i != 0) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
*/
    private void fetchV4Address(Matcher _mat) {
        long v4Addr = 0;
        for (int i = 1; i <= 4; i++) {
            v4Addr <<= 8;
            v4Addr |= Integer.parseInt(_mat.group(i), 10);
        }
        getAddr().setHi(0L);
        getAddr().setLo((v4Addr & IPv4Helper.IPV4_LO_MASK) | IPv4Helper.IPV4_IN_V6_LOPREFIX);
    }

    private long fetchV6StdHalfAddress(Matcher _mat, int iStart, int iEnd) {
        long v6half = 0;
        for (int i = iStart; i <= iEnd; i++) {
            v6half <<= 16;
            v6half |= Integer.parseInt(_mat.group(i), 16);
        }
        return v6half;
    }

    private void fetchV6StdAddress(Matcher _mat) {
        getAddr().setHi(fetchV6StdHalfAddress(_mat, 1, 4));
        getAddr().setLo(fetchV6StdHalfAddress(_mat, 5, 8));
    }

    private void fetchV6CompressedAddress(Matcher _mat) throws T004FormatException {
        String[] arr = _mat.group(0).split(":", -1);
        int arrLen = arr.length;

        //get missing digit groups count, covered by "::" token
        int missingGrpCnt = 0;
        int itemIdx = 0; //initial element
        if (arrLen <= 8) {
            missingGrpCnt = 8 - arrLen;
        }
        //chack splitted array for empty elements
        for (int i = 0; i < arr.length; i++) {
            //for empty "::" element itself we add +1 missing group
            if (arr[i].isEmpty()) {
                missingGrpCnt++;
                //if first item is empty. address starting with :: so
                //we start from the second element (maybe also empty)
                if (i == 0) {
                    itemIdx++;
                }
            }
        }

        long v6Addr = 0;
        for (int i = 1; i <= 4; i++) {
            v6Addr <<= 16;
            String sItem = arr[itemIdx];
            if (!sItem.isEmpty()) {
                v6Addr |= Integer.parseInt(sItem, 16);
                itemIdx++;
            } else {
                if (missingGrpCnt == 0) {
                    throw new T004FormatException("Bad missing groups in high half of v6 address: " + _mat.group(0));
                }
                missingGrpCnt--;
                if (missingGrpCnt == 0) {
                    itemIdx++;
                }
            }
        }
        getAddr().setHi(v6Addr);

        v6Addr = 0;
        for (int i = 5; i <= 8; i++) {
            v6Addr <<= 16;
            String sItem = arr[itemIdx];
            if (!sItem.isEmpty()) {
                v6Addr |= Integer.parseInt(sItem, 16);
                itemIdx++;
            } else {
                if (missingGrpCnt == 0) {
                    throw new T004FormatException("Bad missing groups in low half of v6 address: " + _mat.group(0));
                }
                missingGrpCnt--;
                if (missingGrpCnt == 0) {
                    itemIdx++;
                }
            }
        }
        getAddr().setLo(v6Addr);
    }

    private void fetchAddress(String _str) throws T004FormatException {
        Matcher mat = IPV4PATTERN.matcher(_str);
        if (mat.matches() && (mat.groupCount() == 4)) {
            fetchV4Address(mat);
            return;
        } else {
            mat = IPV6STDPATTERN.matcher(_str);
            if (mat.matches() && (mat.groupCount() == 8)) {
                fetchV6StdAddress(mat);
                isCompressedV6 = false;
                return;
            } else {
                mat = IPV6COMPRESSEDPATTERN.matcher(_str);
                if (mat.matches()) {
                    fetchV6CompressedAddress(mat);
                    isCompressedV6 = true;
                    return;
                }
            }
        }
        throw new T004FormatException("Invalid inet address: " + _str);
    }

    @Override
    public String toString() {
       return addr.toString();
    }

    @Override
    public int compareTo(IPvXAddrParser o) {
        return addr.compareTo(o.getAddr());
    }

    /**
     * @return the addr
     */
    public IPvXTuple getAddr() {
        return addr;
    }


}
