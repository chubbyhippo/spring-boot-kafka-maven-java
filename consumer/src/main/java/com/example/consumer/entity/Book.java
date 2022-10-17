package com.example.consumer.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class Book {
    @Id
    @NotNull
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String author;

    @OneToOne
    @JoinColumn(name = "id")
    private LibraryEvent libraryEvent;

}
