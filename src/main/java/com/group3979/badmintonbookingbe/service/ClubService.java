package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.ClubStatus;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Club;
import com.group3979.badmintonbookingbe.entity.ImageClub;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.ClubRequest;
import com.group3979.badmintonbookingbe.model.response.AuthenticationResponse;
import com.group3979.badmintonbookingbe.model.response.ClubResponse;
import com.group3979.badmintonbookingbe.model.response.CourtResponse;
import com.group3979.badmintonbookingbe.model.response.NameClubOwnerAndCapacityClubResponse;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.IImageClubRespository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClubService {
    // xử lí logic CRUD
    @Autowired
    private IImageClubRespository imageClubRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private IClubRepository clubRepository;

    @Autowired
    private CourtService courtService;

    @Autowired
    private ImageClubService imageClubService;

    // R - Read All
    public List<ClubResponse> getAllClubRequests() {
        List<Club> clubs = clubRepository.findAll();
        List<ClubResponse> clubResponses = new ArrayList<>();
        for (Club club : clubs) {
            clubResponses.add(getClubResponseById(club.getClubId()));
        }
        return clubResponses;
    }

    // R - Read ClubResponse by Current Account Id
    public List<ClubResponse> getAllClubRequestsByCurrentAccountId() {
        Account account = accountUtils.getCurrentAccount();
        List<Club> clubs = clubRepository.findClubsByAccount(account);
        List<ClubResponse> clubResponses = new ArrayList<>();
        for (Club club : clubs) {
            clubResponses.add(getClubResponseById(club.getClubId()));
        }
        return clubResponses;
    }

    public ClubResponse getClubResponseById(Long id) {
        Club club = clubRepository.findByClubId(id);
        List<String> urlImages = new ArrayList<>();
        for(ImageClub imageClub : imageClubRepository.findByClub(club)){
            urlImages.add(imageClub.getUrlImage());
        }
        Account account = club.getAccount();
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .email(account.getEmail())
                .role(account.getRole())
                .phone(account.getPhone())
                .name(account.getName())
                .gender(account.getGender())
                .accountStatus(account.getAccountStatus())
                .build();
        return ClubResponse.builder()
                .clubName(club.getClubName())
                .clubStatus(club.getClubStatus())
                .clubAddress(club.getClubAddress())
                .hotline(club.getHotline())
                .closeTime(club.getCloseTime())
                .openTime(club.getOpenTime())
                .clubId(club.getClubId())
                .description(club.getDescription())
                .province(club.getProvince())
                .district(club.getDistrict())
                .urlImages(urlImages)
                .authenticationResponse(authenticationResponse).build();
    }

    // C - Create
    public ClubResponse createClub(ClubRequest clubRequest) throws BadRequestException, NotFoundException {

        Club club = new Club();
        club.setClubAddress(clubRequest.getClubAddress());
        club.setProvince(clubRequest.getProvince());
        club.setDistrict(clubRequest.getDistrict());
        club.setClubName(clubRequest.getClubName());
        club.setHotline(clubRequest.getClubHotLine());
        club.setOpenTime(clubRequest.getOpeningTime());
        club.setCloseTime(clubRequest.getClosingTime());
        club.setDescription(clubRequest.getClubDescription());
        club.setClubStatus(ClubStatus.ACTIVE);
        club.setAccount(accountUtils.getCurrentAccount());

        if (club.getOpenTime() >= club.getCloseTime() || club.getOpenTime() < 0 || club.getOpenTime() > 24 || club.getCloseTime() > 24) {
            throw new BadRequestException("Thời gian mở cửa và đóng cửa không hợp lệ. Vui lòng kiểm tra lại.");
        }

        club = clubRepository.save(club);
        for (String url : clubRequest.getUrlImages()) {
            imageClubService.createImageClub(club, url);
        }
        //create courts of club by quantity court(capacity)
        courtService.createCourtsByClub(club, clubRequest.getCapacity());
        return getClubResponseById(club.getClubId());
    }

    // U - Update
    public ClubResponse updateClub(Long id, ClubRequest clubRequest) {
        Club club = clubRepository.findByClubId(id);

        if (club != null) {
            club.setClubAddress(clubRequest.getClubAddress());
            club.setProvince(clubRequest.getProvince());
            club.setDistrict(clubRequest.getDistrict());
            club.setClubName(clubRequest.getClubName());
            club.setHotline(clubRequest.getClubHotLine());
            club.setOpenTime(clubRequest.getOpeningTime());
            club.setCloseTime(clubRequest.getClosingTime());
            club.setDescription(clubRequest.getClubDescription());
            club = clubRepository.save(club);
            return getClubResponseById(club.getClubId());
        }
        return null;
    }

    // D - Delete
    public boolean deleteStatusClub(Long id) {
        Club club = clubRepository.findByClubId(id);
        if (club != null) {
            club.setClubStatus(ClubStatus.DELETED);
            clubRepository.save(club);
            return true;
        }
        return false;
    }
    public List<ClubResponse> searchClubByName(String name){
        List<Club> clubs = clubRepository.findByClubNameContainingIgnoreCase(name);
        if(!clubs.isEmpty()){
            List<ClubResponse> clubResponses= new ArrayList<>();
            for(Club club: clubs){
                clubResponses.add(this.getClubResponseById(club.getClubId()));
            }
            return clubResponses;
        }else {
            throw new CustomException("Không tìm thấy kết quả nào");
        }
    }
    public List<ClubResponse> searchClubByDistrictProvince(String district, String province){
        List<Club> clubs = clubRepository.findByDistrictAndProvinceContainingIgnoreCase(district,province);
        if(!clubs.isEmpty()){
            List<ClubResponse> clubResponses= new ArrayList<>();
            for(Club club: clubs){
                clubResponses.add(this.getClubResponseById(club.getClubId()));
            }
            return clubResponses;
        }else {
            throw new CustomException("Không tìm thấy kết quả nào");
        }
    }

    //Lấy tên chủ sân và tổng số lượng aân
    public NameClubOwnerAndCapacityClubResponse GetNameClubOwner(Long id) {
        NameClubOwnerAndCapacityClubResponse nameClubOwnerAndCapacityClubResponse = new NameClubOwnerAndCapacityClubResponse();
        Club club = clubRepository.findByClubId(id);
        Account account = club.getAccount();
        nameClubOwnerAndCapacityClubResponse.setNameOwner(account.getName());
        List<CourtResponse> courts= courtService.getAllCourtsByClub(id);
        int capacity = courts.size();
        nameClubOwnerAndCapacityClubResponse.setCapacity(capacity);
        return nameClubOwnerAndCapacityClubResponse;
    }
    public List<ClubResponse> getTenNewestClub(){
        Pageable pageable = PageRequest.of(0,10);
        List<Club> clubs =  clubRepository.findClubsByOrderByClubIdDesc(pageable);
        List<ClubResponse> clubResponses = new ArrayList<>();
        for(Club club: clubs){
            clubResponses.add(this.getClubResponseById(club.getClubId()));
        }
        if(clubResponses.isEmpty()){
            throw new CustomException("Chưa có sân nào");
        }
        return clubResponses;
    }
}
