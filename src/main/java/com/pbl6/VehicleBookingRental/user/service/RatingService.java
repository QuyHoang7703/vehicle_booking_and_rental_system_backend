package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.Rating;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqCreateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqUpdateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.rating.ResRatingInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.rating.ResRatingOrderDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RatingService {
    void createRatingForOrder(ReqCreateRatingDTO reqCreateRatingDTO) throws ApplicationException, IdInvalidException;
    void updateRatingForOrder(ReqUpdateRatingDTO reqUpdateRatingDTO) throws ApplicationException, IdInvalidException;
    void deleteRatingForOrder(int ratingId) throws ApplicationException;
    ResRatingInfoDTO getAllRatingOfOrder(Specification<Rating> specification, Pageable pageable) throws ApplicationException;
    ResRatingOrderDTO convertToResRatingOrderDTO(Rating rating) throws ApplicationException;
//    public void updateRatingTotal(Orders order);

}
