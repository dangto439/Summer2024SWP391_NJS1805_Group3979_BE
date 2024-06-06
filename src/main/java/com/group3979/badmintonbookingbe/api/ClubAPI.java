package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.ClubRequest;
import com.group3979.badmintonbookingbe.model.request.ImageClubRequest;
import com.group3979.badmintonbookingbe.model.response.ClubResponse;
import com.group3979.badmintonbookingbe.model.response.ImageClubResponse;
import com.group3979.badmintonbookingbe.service.ClubService;
import com.group3979.badmintonbookingbe.service.CourtService;
import com.group3979.badmintonbookingbe.service.ImageClubService;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class ClubAPI {
    @Autowired
    AccountUtils accountUtils;

    @Autowired
    private ClubService clubService;

    @Autowired
    CourtService courtService;

    @Autowired
    ImageClubService imageClubService;
    // Get all clubs
    @GetMapping("/clubs")
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubRequests());
    }

    // Get all clubs of current account
    @GetMapping("/current-clubs")
    public ResponseEntity<List<ClubResponse>> getAllClubsCurrentAccount() {
        return ResponseEntity.ok(clubService.getAllClubRequestsByCurrentAccountId());
    }

    // Get club by id
    @GetMapping("/club/{id}")
    public ResponseEntity<ClubResponse> getClubById(@PathVariable Long id) {
        ClubResponse clubResponse = clubService.getClubResponseById(id);
        return ResponseEntity.ok(clubResponse);
    }

    // Create new club
    @PostMapping("/club")
    public ResponseEntity createClub(@RequestBody ClubRequest clubRequest) {
        try {
            ClubResponse club = clubService.createClub(clubRequest);
            return ResponseEntity.ok(club);
        }catch (BadRequestException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update existing club
    @PutMapping("/club/{id}")
    public ResponseEntity<ClubResponse> updateClub(@PathVariable Long id, @RequestBody ClubRequest clubRequest) {
        ClubResponse updatedClub = clubService.updateClub(id, clubRequest);
        return ResponseEntity.ok(updatedClub);
    }

    // Delete club
    @DeleteMapping("/club/{id}")
    public ResponseEntity<Object> deleteClub(@PathVariable Long id) {
        boolean deleteSuccess = clubService.deleteStatusClub(id);
        if (deleteSuccess) {
            return ResponseEntity.ok("Deleted Successfully");
        }
        return new ResponseEntity<>("Delete failed", HttpStatus.BAD_REQUEST);
    }
    //delete  image by Id
    @DeleteMapping("/image-club/{imageId}")
    public ResponseEntity<Object> deleteImageClub(@PathVariable Long imageId) {
       boolean  deleteResult =  imageClubService.deleteImageClubById(imageId);
       if(deleteResult) {
          return ResponseEntity.ok("Deleted Successfully");
       }
        return new ResponseEntity<>("Delete failed", HttpStatus.BAD_REQUEST);
    }
    //get a image by Id
    @GetMapping("/image-club/{imageId}")
    public ResponseEntity<ImageClubResponse> getImageClub(@PathVariable Long imageId) {
        ImageClubResponse imageClub = imageClubService.getImageClubByImageId(imageId);
        return ResponseEntity.ok(imageClub);
    }
    //get all images of a club by clubId
    @GetMapping("/images-club/{clubId}")
    public ResponseEntity<List<ImageClubResponse>> getImagesOfClub(@PathVariable Long clubId) {
        List<ImageClubResponse> imageClubResponses = imageClubService.getALlImagesByClubId(clubId);
        return ResponseEntity.ok(imageClubResponses);
    }
    @PostMapping("/image-club")
    public ResponseEntity<ImageClubResponse> createImageClub(@RequestBody ImageClubRequest imageClubRequest) {
        ImageClubResponse imageClubResponse = imageClubService.createImageClub(imageClubRequest);
        return ResponseEntity.ok(imageClubResponse);
    }
}
