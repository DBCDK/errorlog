/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.monitoring.errorlog.model;

import java.util.List;

public class ErrorLogAppView {
    final List<ErrorLogEntity> entities;
    final long size;
    final long firstPosition;

    public ErrorLogAppView(List<ErrorLogEntity> entities, long size, long firstPosition) {
        this.entities = entities;
        this.size = size;
        this.firstPosition = firstPosition;
    }

    public List<ErrorLogEntity> getEntities() {
        return entities;
    }

    /**
     * @return Total number errorlog entries for a particular
     * application. Be advised that because of paging constraints,
     * size may differ from entities.size().
     *
     */
    public long getSize() {
        return size;
    }

    /**
     * @return Zero based offset of the first entity in this
     * ErrorLogAppView instance.
     */
    public long getFirstPosition() {
        return firstPosition;
    }
}
