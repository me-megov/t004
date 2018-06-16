/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.helpers;

/**
 *
 * @author megov
 */
public class SubNetworkGenParams {

        private boolean ipv4 = false;
        private int maxNetworkMaskValue = 0;
        private int maxLevel = 0;
        private int counter = 0;
        private int topLevelCustomerCount = 0;
        
        public SubNetworkGenParams(boolean _ipv4) {
            this.ipv4 = _ipv4;
        }

        public boolean isIpv4() {
            return ipv4;
        }

        public int getMaxNetworkMaskValue() {
            return maxNetworkMaskValue;
        }

        public SubNetworkGenParams setMaxNetworkMaskValue(int maxNetworkMaskValue) {
            this.maxNetworkMaskValue = maxNetworkMaskValue;
            return this;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public SubNetworkGenParams setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public int getCounter() {
            return counter;
        }

        public SubNetworkGenParams setCounter(int counter) {
            this.counter = counter;
            return this;
        }

    public int getTopLevelCustomerCount() {
        return topLevelCustomerCount;
    }

    public SubNetworkGenParams setTopLevelCustomerCount(int topLevelCustomerCount) {
        this.topLevelCustomerCount = topLevelCustomerCount;
        return this;
    }
    
}
