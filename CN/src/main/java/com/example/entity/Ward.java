package com.example.entity;

public class Ward {
    private Integer id;
    private String name;
    private String level;
    private String position;
    private String wardName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWard_name() {
        return wardName;
    }

    public void setWard_name(String wardName) {
        this.wardName = wardName;
    }

    @Override
    public String toString() {
        return "Ward{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", position='" + position + '\'' +
                ", ward_name='" + wardName + '\'' +
                '}';
    }
}
