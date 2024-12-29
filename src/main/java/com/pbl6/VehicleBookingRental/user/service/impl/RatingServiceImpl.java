package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.Rating;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqCreateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.rating.ReqUpdateRatingDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.rating.ResRatingInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.rating.ResRatingOrderDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.RatingRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.RatingService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final AccountService accountService;
    private final OrdersRepo ordersRepo;
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final VehicleRentalServiceRepo vehicleRentalServiceRepo;
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

        order.setRating(rating);
        this.ordersRepo.save(order);

        // Update rating total for order
        this.updateRatingTotalWhenCreateRating(order);
    }

    private void updateRatingTotalWhenCreateRating(Orders order) {
        String orderType = order.getOrder_type();
        if(orderType.equals("BUS_TRIP_ORDER")){
            BusTripSchedule busTripSchedule = order.getOrderBusTrip().getBusTripSchedule();
            double currentRatingTotal = busTripSchedule.getRatingTotal();

            int numberOfOrders = ratingRepository.getNumberRatingsOfBusTripSchedule(busTripSchedule.getId());
            log.info("Number order of bus trip schedule in current: " + numberOfOrders);

            double newValueRating = ((numberOfOrders - 1) * currentRatingTotal + order.getRating().getRatingValue()) / numberOfOrders;
            busTripSchedule.setRatingTotal(Double.parseDouble(String.format("%.6f", newValueRating)));
            this.busTripScheduleRepository.save(busTripSchedule);
        }
        else if(orderType.equals("VEHICLE_RENTAL_ORDER")){
            CarRentalService carRentalService = order.getCarRentalOrders().getCarRentalService();
            double currentRatingTotal = carRentalService.getRatingTotal();
            int numberOfRating = ratingRepository.getNumberRatingOfCarRentalService(carRentalService.getId());
            double newRatingValue = ((numberOfRating - 1) * currentRatingTotal + order.getRating().getRatingValue()) / numberOfRating;
            carRentalService.setRatingTotal(Double.parseDouble(String.format("%.6f", newRatingValue)));
            this.vehicleRentalServiceRepo.save(carRentalService);
        }

    }


    @Override
    @Transactional
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
        double oldRatingValue = rating.getRatingValue();

        rating.setRatingValue(reqUpdateRatingDTO.getRatingValue());
        rating.setComment(reqUpdateRatingDTO.getComment());

        this.ratingRepository.save(rating);

        // Update rating total for order
        this.updateRatingTotalWhenUpdateRating(rating.getOrder(), oldRatingValue);
    }

    private void updateRatingTotalWhenUpdateRating(Orders order, double oldRatingValue){
        String orderType = order.getOrder_type();
        if(orderType.equals("BUS_TRIP_ORDER")){
            BusTripSchedule busTripSchedule = order.getOrderBusTrip().getBusTripSchedule();
            double currentRatingTotal = busTripSchedule.getRatingTotal();
            int numberOfOrder = this.ratingRepository.getNumberRatingsOfBusTripSchedule(busTripSchedule.getId());
            log.info("Number order of bus trip schedule in current: " + numberOfOrder);
            double updateValueRating = (currentRatingTotal * numberOfOrder - oldRatingValue + order.getRating().getRatingValue()) / numberOfOrder;
            log.info("Updated rating value " + updateValueRating);
            busTripSchedule.setRatingTotal(Double.parseDouble(String.format("%.6f", updateValueRating)));
            this.busTripScheduleRepository.save(busTripSchedule);
        }else if(orderType.equals("VEHICLE_RENTAL_ORDER")){
            CarRentalService carRentalService = order.getCarRentalOrders().getCarRentalService();
            double currentRatingTotal = carRentalService.getRatingTotal();
            int numberOfOrders = ratingRepository.getNumberRatingOfCarRentalService(carRentalService.getId());
            double updateValueRating = (currentRatingTotal * numberOfOrders - oldRatingValue + order.getRating().getRatingValue()) / numberOfOrders;
            carRentalService.setRatingTotal(Double.parseDouble(String.format("%.6f", updateValueRating)));
            this.vehicleRentalServiceRepo.save(carRentalService);
        }
    }


    @Override
    @Transactional
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

        this.updateRatingValueWhenDeleteRating(rating.getOrder(), rating.getRatingValue());
        this.ratingRepository.delete(rating);

    }

    private void updateRatingValueWhenDeleteRating(Orders order, double oldRatingValue){
        String orderType = order.getOrder_type();
        if(orderType.equals("BUS_TRIP_ORDER")){
            BusTripSchedule busTripSchedule = order.getOrderBusTrip().getBusTripSchedule();
            double currentRatingTotal = busTripSchedule.getRatingTotal();
            int numberOfOrders = ratingRepository.getNumberRatingsOfBusTripSchedule(busTripSchedule.getId());
            double updateValueRating = (currentRatingTotal * numberOfOrders - oldRatingValue) / (numberOfOrders - 1);
            busTripSchedule.setRatingTotal(Double.parseDouble(String.format("%.6f", updateValueRating)));
            this.busTripScheduleRepository.save(busTripSchedule);
        }else if(orderType.equals("VEHICLE_RENTAL_ORDER")){
            CarRentalService carRentalService = order.getCarRentalOrders().getCarRentalService();
            double currentRatingTotal = carRentalService.getRatingTotal();
            int numberOfOrders = ratingRepository.getNumberRatingOfCarRentalService(carRentalService.getId());
            double updateValueRating = (currentRatingTotal * numberOfOrders - oldRatingValue) / (numberOfOrders - 1);
            carRentalService.setRatingTotal(Double.parseDouble(String.format("%.6f", updateValueRating)));
            this.vehicleRentalServiceRepo.save(carRentalService);
        }
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
        }else{
            resRatingInfoDTO.setAverageRating(ratingPage.getContent().get(0).getOrder().getCarRentalOrders().getCarRentalService().getRatingTotal());
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








}
