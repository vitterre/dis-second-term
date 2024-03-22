package com.technokratos.agona.model;

import com.technokratos.agona.annotation.Column;
import com.technokratos.agona.annotation.Entity;
import com.technokratos.agona.annotation.ManyToOne;
import com.technokratos.agona.annotation.PrimaryKey;
import com.technokratos.agona.enums.FetchType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(tableName = "author")
@NoArgsConstructor
public class Author {

    @Column(name = "id")
    @PrimaryKey
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(to = "country", fetchType = FetchType.EAGER)
    @Column(name = "country_id")
    private Country country;
}
