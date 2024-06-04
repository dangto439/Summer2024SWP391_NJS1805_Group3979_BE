package com.group3979.badmintonbookingbe.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageClubResponse {
    private long imageId;
    private String imageUrl;
    private long clubId;
}
