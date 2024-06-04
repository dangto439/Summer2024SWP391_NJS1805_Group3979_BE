package com.group3979.badmintonbookingbe.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageClubRequest {
    private String imageUrl;
    private long clubId;
}
