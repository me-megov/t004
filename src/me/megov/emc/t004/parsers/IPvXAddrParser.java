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

/**
 *
 * @author megov
 */
public class IPvXAddrParser { //implements Comparable<IPvXAddrParser> 

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

//    private IPvXTuple addr = new IPvXTuple(0, 0);
//    private boolean isCompressedV6 = false;

//    public IPvXAddrParser(long _hiAddr, long _loAddr) {
//        this.addr = new IPvXTuple(_hiAddr, _loAddr);
//    }
//        
//   public IPvXAddrParser(IPvXTuple _addr) {
//        this.addr = _addr;
//    }
    
//    public IPvXAddrParser(String _str) throws T004FormatException {
//        fetchAddress(_str);
//    }

    
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
    public static IPvXTuple parseV4Address(Matcher _mat) {
        long v4Addr = 0;
        for (int i = 1; i <= 4; i++) {
            v4Addr <<= 8;
            v4Addr |= Integer.parseInt(_mat.group(i), 10);
        }
        return new IPvXTuple(0L, (v4Addr & IPvXTuple.IPV4_LO_MASK) | IPvXTuple.IPV4_IN_V6_LOPREFIX);
    }

    private static long parseV6StdHalfAddress(Matcher _mat, int iStart, int iEnd) {
        long v6half = 0;
        for (int i = iStart; i <= iEnd; i++) {
            v6half <<= 16;
            v6half |= Integer.parseInt(_mat.group(i), 16);
        }
        return v6half;
    }

    public static IPvXTuple parseV6StdAddress(Matcher _mat) {
        return new IPvXTuple(parseV6StdHalfAddress(_mat, 1, 4),
                             parseV6StdHalfAddress(_mat, 5, 8),
                             0);
    }
    
    private static IPvXTuple parseV6CompressedAddress(String _text, Matcher _mat) throws T004FormatException {
        String[] arr = _text.split("::", -1);
        String leftPart = arr[0];
        String rightPart = arr[1];
        int missMask = 0;
//      if (leftPart.isEmpty() && rightPart.isEmpty()) {
//          return new IPvXTuple(0L,0L, 0b11111111);
//      }
        String[] arrLeft = leftPart.split(":",-1);
        String[] arrRight = rightPart.split(":",-1);
        if ((arrLeft.length==1) && (arrLeft[0].isEmpty())) {
            arrLeft = new String[0];
        }
        if ((arrRight.length==1) && (arrRight[0].isEmpty())) {
            arrRight = new String[0];
        }
        int startCompress = 7-arrLeft.length; 
        int endCompress = arrRight.length; 
        
        long hiAddr = 0;
        long loAddr = 0;
        
        String sElem;
        for (int i=7; i>=0; i--) {
            if (i>startCompress) sElem = arrLeft[7-i];
            else if (i<endCompress) sElem = arrRight[endCompress-i-1];
            else {
                sElem="0";
                missMask|= 1 << i;
                }
            //hi
            if (i>=4) {
                hiAddr<<=16;
                hiAddr|=Integer.parseInt(sElem, 16);
            } 
            //lo
            else {
                loAddr<<=16;
                loAddr|=Integer.parseInt(sElem, 16);
            }
        }
        return new IPvXTuple(hiAddr, loAddr, missMask);
    }

    private static IPvXTuple parseV6CompressedAddressOld(Matcher _mat) throws T004FormatException {
        String[] arr = _mat.group(0).split(":", -1);
        int arrLen = arr.length;
        IPvXTuple addr = new IPvXTuple(0L, 0L, 0b11111111);

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
        addr.setHi(v6Addr);

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
        addr.setLo(v6Addr);
        return addr;
    }

    public static IPvXTuple parseAddress(String _str) throws T004FormatException {
        Matcher mat = IPV4PATTERN.matcher(_str);
        if (mat.matches() && (mat.groupCount() == 4)) {
            return parseV4Address(mat);
        } else {
            mat = IPV6STDPATTERN.matcher(_str);
            if (mat.matches() && (mat.groupCount() == 8)) {
                return parseV6StdAddress(mat);
            } else {
                mat = IPV6COMPRESSEDPATTERN.matcher(_str);
                if (mat.matches()) {
                    return parseV6CompressedAddress(_str,mat);
                }
            }
        }
        throw new T004FormatException("Invalid inet address: " + _str);
    }

}
