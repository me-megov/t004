/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004;

/**
 *
 * @author megov
 */
public class MainV6Out {

    public static void main(String[] args) {

        StringBuilder sbAddrs = new StringBuilder();
        StringBuilder sbMasks = new StringBuilder();
        StringBuilder sbLower = new StringBuilder();
        StringBuilder sbUpper = new StringBuilder();
        StringBuilder sbHosts = new StringBuilder();

        int count = 30;

        for (int i = 0; i < count; i++) {
            long randHi = Math.round(Math.random() * Long.MAX_VALUE) + Math.round(Math.random() * 256);
            long randLo = Math.round(Math.random() * Long.MAX_VALUE) + Math.round(Math.random() * 256);
            boolean invertHi = Math.random() > 0.5;
            boolean invertLo = Math.random() > 0.5;

            if (invertHi) {
                randHi = -randHi;
            }
            if (invertLo) {
                randLo = -randLo;
            }

            long mask = Math.round(Math.random() * 128);

            switch (i) {
                case 0:
                    mask = 0;
                    break;
                case 1:
                    mask = 1;
                    break;
                case 2:
                    mask = 2;
                    break;
                case 3:
                    mask = 8;
                    break;
                case 4:
                    mask = 16;
                    break;
                case 5:
                    mask = 32;
                    break;
                case 6:
                    mask = 63;
                    break;
                case 7:
                    mask = 64;
                    break;
                case 8:
                    mask = 65;
                    break;
                case 9:
                    mask = 72;
                    break;
                case 10:
                    mask = 96;
                    break;
                case 11:
                    mask = 112;
                    break;
                case 12:
                    mask = 120;
                    break;
                case 13:
                    mask = 126;
                    break;
                case 14:
                    mask = 127;
                    break;
                case 15:
                    mask = 128;
                    break;

            }

            sbMasks.append(mask);

            sbAddrs.append("new IPvXTuple(0x");
            sbAddrs.append(Long.toHexString(randHi));
            sbAddrs.append("L,0x");
            sbAddrs.append(Long.toHexString(randLo));
            sbAddrs.append("L)");

            sbLower.append("new IPvXTuple(0x");
            sbUpper.append("new IPvXTuple(0x");
            sbHosts.append("new IPvXTuple(0x");
                    
            if (mask == 0) {
                sbLower.append("0000000000000000L,0x0000000000000000L) ");
                sbUpper.append("FFFFFFFFFFFFFFFFL,0xFFFFFFFFFFFFFFFFL) ");
                sbHosts.append(Long.toHexString(randHi));
                sbHosts.append("L,0x");
                sbHosts.append(Long.toHexString(randLo));
                sbHosts.append("L)");                
            } else if (mask < 64) {
                
                sbLower.append(Long.toHexString(randHi & 0xFFFFFFFFFFFFFFFFL << (64 - mask)));
                sbLower.append("L,0x0000000000000000L)");
                
                sbUpper.append(Long.toHexString(randHi | ~(0xFFFFFFFFFFFFFFFFL << (64 - mask))));
                sbUpper.append("L,0xFFFFFFFFFFFFFFFFL)");
                
                sbHosts.append(Long.toHexString(randHi & ~(0xFFFFFFFFFFFFFFFFL << (64 - mask))));
                sbHosts.append("L,0x");
                sbHosts.append(Long.toHexString(randLo));
                sbHosts.append("L)");                
                
            } else if (mask == 64) {
                sbLower.append(Long.toHexString(randHi));
                sbLower.append("L,0x0000000000000000L)");
                
                sbUpper.append(Long.toHexString(randHi));
                sbUpper.append("L,0xFFFFFFFFFFFFFFFFL)");
                
                sbHosts.append("0000000000000000L,0x");
                sbHosts.append(Long.toHexString(randLo));
                sbHosts.append("L)");
                
            } else if (mask < 128) {
                
                sbLower.append(Long.toHexString(randHi));
                sbLower.append("L,0x");
                sbLower.append(Long.toHexString(randLo & 0xFFFFFFFFFFFFFFFFL << (128 - mask)));
                sbLower.append("L)");
                
                sbUpper.append(Long.toHexString(randHi));
                sbUpper.append("L,0x");
                sbUpper.append(Long.toHexString(randLo | ~(0xFFFFFFFFFFFFFFFFL << (128 - mask))));
                sbUpper.append("L)");
                
                sbHosts.append("0000000000000000L,0x");
                sbHosts.append(Long.toHexString(randLo & ~(0xFFFFFFFFFFFFFFFFL << (128 - mask))));
                sbHosts.append("L)");

            } else {
                sbLower.append(Long.toHexString(randHi));
                sbLower.append("L,0x");
                sbLower.append(Long.toHexString(randLo));
                sbLower.append("L)");                
                sbUpper.append(Long.toHexString(randHi));
                sbUpper.append("L,0x");
                sbUpper.append(Long.toHexString(randLo));
                sbUpper.append("L)");                
                sbHosts.append("0000000000000000L,0x0000000000000000L) ");
            }

            if (i != (count - 1)) {
                sbAddrs.append(",");
                sbLower.append(",");
                sbMasks.append(",");
                sbUpper.append(",");
                sbHosts.append(",");
            }

            sbAddrs.append(" //").append(mask);
            sbLower.append(" //").append(mask);
            sbUpper.append(" //").append(mask);
            sbHosts.append(" //").append(mask);

            if (i != (count - 1)) {
                sbAddrs.append("\n");
                sbLower.append("\n");
                sbUpper.append("\n");
                sbHosts.append("\n");
            }
        }

        System.out.println(sbAddrs.toString());
        System.out.println("\n\n");
        System.out.println(sbMasks.toString());
        System.out.println("\n\n");
        System.out.println(sbLower.toString());
        System.out.println("\n\n");
        System.out.println(sbUpper.toString());
        System.out.println("\n\n");
        System.out.println(sbHosts.toString());
        

    }

}
