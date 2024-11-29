package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.service.UtilityService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")

public class UtilityController {
    private final UtilityService utilityService;

    @PostMapping("/utilities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utility> createUtility(@RequestPart("utilityInfo") Utility utility
            , @RequestPart(value = "utilityImage", required = false) MultipartFile utilityImage) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.utilityService.createUtility(utility, utilityImage));
    }

    @PutMapping("/utilities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utility> updateUtility(@RequestPart("utilityInfo") Utility utility
            , @RequestPart(value = "utilityImage", required = false) MultipartFile utilityImage) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.utilityService.updateUtility(utility, utilityImage));
    }

    @DeleteMapping("/utilities")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Deleted this utility")
    public ResponseEntity<Void> deleteUtility(@RequestParam("idUtility") int idUtility) throws IdInvalidException {
        this.utilityService.deleteUtility(idUtility);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/utilities-all")
    @ApiMessage("Fetch all utilities")
    public ResponseEntity<ResultPaginationDTO> getAllUtilities(@Filter Specification<Utility> spec, Pageable pageable) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.utilityService.getAllUtility(spec, pageable));
    }

    @GetMapping("/utilities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utility> getUtilityById(@RequestParam("utilityId") int utilityId) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.utilityService.getUtilityById(utilityId));
    }






}
