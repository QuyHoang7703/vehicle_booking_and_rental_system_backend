package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.Rating;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqCreateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqUpdateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.rating.ResRatingInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.rating.ResRatingOrderDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.RatingRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.RatingService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final AccountService accountService;
    private final OrdersRepo ordersRepo;
    private final BusTripScheduleRepository busTripScheduleRepository;
    @Override
    public void createRatingForOrder(ReqCreateRatingDTO reqCreateRatingDTO) throws ApplicationException, IdInvalidException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email == null) {
            throw new ApplicationException("Access token is not found or expired");
        }

        Orders order = this.ordersRepo.findById(reqCreateRatingDTO.getOrderId())
                .orElseThrow(() -> new IdInvalidException("Order not found"));

        Account currentAccount = this.accountService.handleGetAccountByUsername(email);

        if(order.getCancelUserId()!=null && order.getCancelUserId().equals(currentAccount.getId())) {
            throw new ApplicationException("You cannot rate this order because you are the canceler");
        }
        Rating rating = new Rating();
        rating.setRatingValue(reqCreateRatingDTO.getRatingValue());
        rating.setComment(reqCreateRatingDTO.getComment());
        rating.setAccount(currentAccount);
        rating.setOrder(order);

        this.ratingRepository.save(rating);

        // Update rating total for order
        this.updateRatingTotal(order);
    }

    @Override
    public void updateRatingForOrder(ReqUpdateRatingDTO reqUpdateRatingDTO) throws ApplicationException, IdInvalidException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email == null) {
            throw new ApplicationException("Access token is not found or expired");
        }

        Account currentAccount = this.accountService.handleGetAccountByUsername(email);

        Rating rating = this.ratingRepository.findById(reqUpdateRatingDTO.getRatingId())
                .orElseThrow(() -> new IdInvalidException("Rating not found"));

        if(!rating.getAccount().equals(currentAccount)) {
            throw new ApplicationException("You don't have permission to update this rating");
        }

        rating.setRatingValue(reqUpdateRatingDTO.getRatingValue());
        rating.setComment(reqUpdateRatingDTO.getComment());

        this.ratingRepository.save(rating);

        // Update rating total for order
        this.updateRatingTotal(rating.getOrder());
    }

    @Override
    public void deleteRatingForOrder(int ratingId) throws ApplicationException {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        if(email == null) {
            throw new ApplicationException("Access token is not found or expired");
        }

        Account currentAccount = this.accountService.handleGetAccountByUsername(email);

        Rating rating = this.ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ApplicationException("Rating not found"));

        if(!rating.getAccount().equals(currentAccount)) {
            throw new ApplicationException("You don't have permission to update this rating");
        }

        this.ratingRepository.delete(rating);

        // Update rating total for order
        this.updateRatingTotal(rating.getOrder());

    }

    @Override
    public ResRatingInfoDTO getAllRatingOfOrder(Specification<Rating> specification, Pageable pageable) throws ApplicationException {
        ResultPaginationDTO res = new ResultPaginationDTO();
        Page<Rating> ratingPage = this.ratingRepository.findAll(specification, pageable);
        if(ratingPage.getContent().isEmpty()) {
            return null;
        }
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(ratingPage.getTotalPages());
        meta.setTotal(ratingPage.getTotalElements());

        res.setMeta(meta);

        List<ResRatingOrderDTO> resRatingOrderDTOS = ratingPage.getContent().stream()
                .map(rating -> {
                    try {
                        return this.convertToResRatingOrderDTO(rating);
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        res.setResult(resRatingOrderDTOS);

        ResRatingInfoDTO resRatingInfoDTO = new ResRatingInfoDTO();
        resRatingInfoDTO.setResult(res);

        // Get order from rating
        Orders order = ratingPage.getContent().get(0).getOrder();
        if(order.getOrder_type().equals(String.valueOf(OrderTypeEnum.BUS_TRIP_ORDER))){
            resRatingInfoDTO.setAverageRating(ratingPage.getContent().get(0).getOrder().getOrderBusTrip().getBusTripSchedule().getRatingTotal());
        }
        return resRatingInfoDTO;
    }

    @Override
    public ResRatingOrderDTO convertToResRatingOrderDTO(Rating rating) throws ApplicationException {
        ResRatingOrderDTO res = ResRatingOrderDTO.builder()
                .id(rating.getId())
                .accountId(rating.getAccount().getId())
                .avatar(rating.getAccount().getAvatar())
                .customerName(rating.getAccount().getName())
                .ratingValue(rating.getRatingValue())
                .comment(rating.getComment())
                .commentDate(rating.getUpdateAt()!=null?rating.getUpdateAt():rating.getCreateAt())
//                .cancelUserId(rating.getOrder().getCancelUserId())
                .build();

        return res;
    }
    @Override
    public void updateRatingTotal(Orders order) {
        String oderType = order.getOrder_type();
        List<Rating> ratings = order.getRatings();
        if(oderType.equals("BUS_TRIP_ORDER")){
            BusTripSchedule busTripSchedule = order.getOrderBusTrip().getBusTripSchedule();
            busTripSchedule.setRatingTotal(this.calculateRatingTotal(ratings));
            this.busTripScheduleRepository.save(busTripSchedule);
        }

    }

    private double calculateRatingTotal(List<Rating> ratings) {
        int numberOfRating = ratings.size();
        double ratingValue = 0;
        for(Rating rating : ratings){
            ratingValue +=  rating.getRatingValue();
        }
        return ratingValue / numberOfRating;
    }
}
