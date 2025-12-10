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

    @DatabaseField(canBeNull = true)
    private String team;

    @DatabaseField(canBeNull = true)
    private Position position;

    @DatabaseField(canBeNull = true)
    private int height;

    @DatabaseField(canBeNull = true)
    private int weight;

    @DatabaseField(canBeNull = true)
    private double age;

    public PlayersEntity() {}

    public PlayersEntity(
            String name,
            String team,
            Position position,
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public double getAge() { return age; }
    public void setAge(double age) { this.age = age; }

    @Override
    public String toString() {
        return String.format("name: %s, team: %s, position: %s, height: %d, weight: %d, age: %f \n", name, team, position, height, weight, age);

    }
}
