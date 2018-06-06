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

/**
 *
 * @author megov
 */
public class LogStats {
    
    private long totalLines = 0L;
    private long totalTraffic = 0L;
    private long totalUnknownTraffic = 0L;
    private int totalCustomersCount = 0;

    /**
     * @return the totalLines
     */
    public long getTotalLines() {
        return totalLines;
    }

    /**
     * @param totalLines the totalLines to set
     */
    public void setTotalLines(long totalLines) {
        this.totalLines = totalLines;
    }

    /**
     * @return the totalTraffic
     */
    public long getTotalTraffic() {
        return totalTraffic;
    }

    /**
     * @param totalTraffic the totalTraffic to set
     */
    public void setTotalTraffic(long totalTraffic) {
        this.totalTraffic = totalTraffic;
    }

    /**
     * @return the totalCustomersCount
     */
    public int getTotalCustomersCount() {
        return totalCustomersCount;
    }

    /**
     * @param totalCustomersCount the totalCustomersCount to set
     */
    public void setTotalCustomersCount(int totalCustomersCount) {
        this.totalCustomersCount = totalCustomersCount;
    }
    
    public void clear() {
        this.totalCustomersCount = 0;
        this.totalLines = 0L;
        this.totalTraffic = 0L;
    }

    /**
     * @return the totalUnknownTraffic
     */
    public long getTotalUnknownTraffic() {
        return totalUnknownTraffic;
    }

    /**
     * @param totalUnknownTraffic the totalUnknownTraffic to set
     */
    public void setTotalUnknownTraffic(long totalUnknownTraffic) {
        this.totalUnknownTraffic = totalUnknownTraffic;
    }
            
    
}
