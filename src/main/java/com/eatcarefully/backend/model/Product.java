package com.eatcarefully.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    private long id;

    private String name;

    private String score;

    @ManyToMany
    private List<Tag> tags;

    @ManyToMany
    private List<Ingredient> ingredients;
}
