package com.pbl6.VehicleBookingRental.user.service.impl.statistic;

import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.dto.response.homePage.PopularRouteDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.DropOffLocationRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.service.HomePageService;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomePageServiceImpl implements HomePageService {
    private final ImageRepository imageRepository;
    private final BusTripRepository busTripRepository;
    private final DropOffLocationRepository dropOffLocationRepository;
    private final BusPartnerRepository busPartnerRepository;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final OrdersRepo ordersRepo;
    @Override
    public Map<String, Integer> getHighLightNumber() {
        Map<String, Integer> map = new LinkedHashMap<>();
        List<BusPartner> busPartners = this.busPartnerRepository.findAll();
        map.put("Nhà xe chất lượng cao", this.busPartnerRepository.findAll().size());
        map.put("Tuyến đường", this.busTripRepository.findAll().size());
        map.put("Hợp tác phát triển", this.businessPartnerRepository.findAll().size());
        map.put("Giao dịch thanh toán thành công", this.ordersRepo.findAll().size());
        return map;
    }

    @Override
    public List<PopularRouteDTO> getPopularRoutes() {
        List<PopularRouteDTO> popularRouteDTOS = new ArrayList<>();
        List<Object[]> objects = this.busTripRepository.findTopBusTrips();
        for(Object[] object: objects) {
            PopularRouteDTO popularRouteDTO = new PopularRouteDTO();
            popularRouteDTO.setRoute(object[0].toString() + "-" + object[1].toString());

            DropOffLocation dropOffLocation= this.dropOffLocationRepository.findPriceTicketForArrivalLocation(object[1].toString()).get(0);
            popularRouteDTO.setInfoPrice(CurrencyFormatterUtil.formatToVND(dropOffLocation.getPriceTicket()));

            Bus representativeBus = dropOffLocation.getBusTrip().getBusPartner().getBuses().get(0);
            Images representativeImage = this.imageRepository.findByOwnerTypeAndOwnerId("BUS", representativeBus.getId()).get(0);
            popularRouteDTO.setImageUrl(representativeImage.getPathImage());

            log.info("Departure location {}, arrival location {}", object[0], object[1]);
            popularRouteDTOS.add(popularRouteDTO);
        }
        return popularRouteDTOS;
    }
}
