/**
 * SORT_ORDER.java
 * @author Vagisha Sharma
 * Aug 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer;

public enum SORT_ORDER {ASC, DESC;
      public static SORT_ORDER getSortByForString(String sortOrder) {
          if(sortOrder == null || sortOrder.equalsIgnoreCase("ASC")) return ASC;
          else return DESC;
      }
  }