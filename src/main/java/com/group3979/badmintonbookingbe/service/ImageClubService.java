package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.ImageClub;
import com.group3979.badmintonbookingbe.model.request.ImageClubRequest;
import com.group3979.badmintonbookingbe.model.response.ImageClubResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.IImageClubRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageClubService {

    @Autowired
    IImageClubRespository imageClubRepository;

    @Autowired
    IClubRepository clubRepository;

    public List<ImageClubResponse> getALlImagesByClubId(long clubId) {
        Club club = clubRepository.findByClubId(clubId);
        List<ImageClub> imageClubs = imageClubRepository.findByClub(club);
        if (!imageClubs.isEmpty()) {
            List<ImageClubResponse> imageClubResponses = new ArrayList<>();
            for (ImageClub imageClub : imageClubs) {
                ImageClubResponse imageClubResponse = new ImageClubResponse();
                imageClubResponse.setClubId(imageClub.getClub().getClubId());
                imageClubResponse.setImageUrl(imageClub.getUrlImage());
                imageClubResponse.setImageId(imageClub.getImageClubId());
                imageClubResponses.add(imageClubResponse);
            }
            return imageClubResponses;
        }
        return null;
    }

    public ImageClubResponse getImageClubByImageId(long ImageClubId) {
        ImageClub imageClub = imageClubRepository.findById(ImageClubId);
        if (imageClub != null) {
            ImageClubResponse imageClubResponse = new ImageClubResponse();
            imageClubResponse.setClubId(imageClub.getClub().getClubId());
            imageClubResponse.setImageUrl(imageClub.getUrlImage());
            imageClubResponse.setImageId(imageClub.getImageClubId());
            return imageClubResponse;
        }
        return null;
    }

    public ImageClubResponse createImageClub(ImageClubRequest imageClubRequest) {
        ImageClub imageClub = new ImageClub();
        imageClub.setUrlImage(imageClubRequest.getImageUrl());
        Club club = clubRepository.findByClubId(imageClubRequest.getClubId());
        imageClub.setClub(club);
        imageClub = imageClubRepository.save(imageClub);
        ImageClubResponse imageClubResponse = new ImageClubResponse();
        imageClubResponse.setClubId(imageClub.getClub().getClubId());
        imageClubResponse.setImageUrl(imageClub.getUrlImage());
        imageClubResponse.setImageId(imageClub.getImageClubId());
        return imageClubResponse;
    }
    public ImageClubResponse createImageClub(Club club, String url) {
        ImageClub imageClub = new ImageClub();
        imageClub.setUrlImage(url);
        imageClub.setClub(club);
        imageClub = imageClubRepository.save(imageClub);
        ImageClubResponse imageClubResponse = new ImageClubResponse();
        imageClubResponse.setClubId(imageClub.getClub().getClubId());
        imageClubResponse.setImageUrl(imageClub.getUrlImage());
        imageClubResponse.setImageId(imageClub.getImageClubId());
        return imageClubResponse;
    }

    public boolean deleteImageClubById(long id) {
        ImageClub imageClub = imageClubRepository.findById(id);
        boolean imageClubDeleted = false;
        if (imageClub != null) {
            imageClubRepository.delete(imageClub);
            imageClubDeleted = true;
        }
        return imageClubDeleted;
    }
}
