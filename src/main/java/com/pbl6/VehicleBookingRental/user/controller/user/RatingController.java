package com.pbl6.VehicleBookingRental.user.controller.user;

import com.pbl6.VehicleBookingRental.user.domain.Rating;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqCreateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqUpdateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.rating.ResRatingInfoDTO;
import com.pbl6.VehicleBookingRental.user.service.RatingService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/ratings")
    @ApiMessage("Created new rating")
    public ResponseEntity<Void> createRating(@RequestBody ReqCreateRatingDTO reqCreateRatingDTO) throws Exception {
        this.ratingService.createRatingForOrder(reqCreateRatingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PatchMapping("/ratings")
    @ApiMessage("Updated the rating")
    public ResponseEntity<Void> updateRating(@RequestBody ReqUpdateRatingDTO reqUpdateRatingDTO) throws Exception {
        this.ratingService.updateRatingForOrder(reqUpdateRatingDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("ratings/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable int ratingId) throws ApplicationException {
        this.ratingService.deleteRatingForOrder(ratingId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("ratings")
    public ResponseEntity<ResRatingInfoDTO> getAllRatingOfOrder(@Filter Specification<Rating> spec, Pageable pageable) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.ratingService.getAllRatingOfOrder(spec, pageable));
    }
}
