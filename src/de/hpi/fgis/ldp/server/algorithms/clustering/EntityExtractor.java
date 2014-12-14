/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.algorithms.clustering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.fgis.ldp.server.datastructures.IEntity;

public class EntityExtractor {
  private static EntityExtractor INSTANCE = new EntityExtractor();

  // TODO inject as singleton
  public static EntityExtractor getInstance() {
    return EntityExtractor.INSTANCE;
  }

  private EntityExtractor() {
    // hide default constructor
  }

  public List<IEntity> extract(List<Integer> entityIds, List<IEntity> entities) {
    List<IEntity> result = new ArrayList<IEntity>();
    int index = 0;

    Iterator<IEntity> it = entities.iterator();
    for (int entityId : entityIds) {
      IEntity entity;
      do {
        entity = it.next();
      } while (entityId != entity.getId());
      // add a copy because the index is different
      result.add(entity.copy(index++));
    }
    return result;
  }
}
