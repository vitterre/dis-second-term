package com.technokratos.agona.model;

import com.technokratos.agona.annotation.Column;
import com.technokratos.agona.annotation.Entity;
import com.technokratos.agona.annotation.PrimaryKey;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(tableName = "country")
@NoArgsConstructor
public class Country {

    @Column(name = "id")
    @PrimaryKey
    private Integer id;

    @Column(name = "name")
    private String name;
}
