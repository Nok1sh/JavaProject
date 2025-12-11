package org.project.model.db.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.project.model.player.Position;

@DatabaseTable(tableName = "players")
public class PlayersEntity {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)
    private String name;

    @DatabaseField(foreign = true, canBeNull = true)
    private TeamsEntity team;

    @DatabaseField(foreign = true, canBeNull = true)
    private PositionsEntity position;

    @DatabaseField(canBeNull = true)
    private int height;

    @DatabaseField(canBeNull = true)
    private int weight;

    @DatabaseField(canBeNull = true)
    private double age;

    public PlayersEntity() {}

    public PlayersEntity(
            String name,
            TeamsEntity team,
            PositionsEntity position,
            int height,
            int weight,
            double age
    ) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public String getName() { return name; }

    public TeamsEntity getTeam() { return team; }

    public PositionsEntity getPosition() { return position; }

    public int getHeight() { return height; }

    public int getWeight() { return weight; }

    public double getAge() { return age; }

    @Override
    public String toString() {
        return String.format("name: %s, team: %s, position: %s, height: %d, weight: %d, age: %f \n", name, team != null ? team.getTeam() : "null", position != null ? position.getPosition() : "null", height, weight, age);
    }
}
