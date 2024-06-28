package com.group3979.badmintonbookingbe.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "club_id")
    private Club club;
}
