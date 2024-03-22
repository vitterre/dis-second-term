package com.technokratos.agona.model;

import com.technokratos.agona.annotation.Column;
import com.technokratos.agona.annotation.Entity;
import com.technokratos.agona.annotation.ManyToOne;
import com.technokratos.agona.annotation.PrimaryKey;
import lombok.Data;

@Data
@Entity(tableName = "author")
public class Author {
    @Column(name = "id")
    @PrimaryKey
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(to = "country")
    @Column(name = "country_id")
    private Country country;

    public Author() { }
}
