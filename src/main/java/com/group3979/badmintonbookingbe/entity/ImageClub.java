package com.group3979.badmintonbookingbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageClub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long imageClubId;
    private String urlImage;
    @ManyToOne
    private Club club;
}
