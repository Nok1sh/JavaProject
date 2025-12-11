package org.project.model.db.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "teams")
public class TeamsEntity {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String team;

    public TeamsEntity(String team) {
        this.team = team;
    }
    
    public TeamsEntity() {}

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return String.format("id = %d, team = %s", id, team);
    }
}
