package org.project.model.db.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.project.model.player.Position;

@DatabaseTable(tableName = "positions")
public class PositionsEntity {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private Position position;

    public PositionsEntity(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("id = %d, position = %s", id, position);
    }
}
