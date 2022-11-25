package com.example.consumer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;



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
