package NextLevel.demo.funding.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.funding.FundingType;
import NextLevel.demo.funding.dto.request.RequestCancelFundingDto;
import NextLevel.demo.funding.dto.request.RequestFundingDto;
import NextLevel.demo.funding.dto.request.RequestOptionFundingDto;
import NextLevel.demo.funding.service.FundingService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api1/funding")
@Controller
@RequiredArgsConstructor
@Slf4j
public class FundingController {

    private final FundingService fundingService;

    @PostMapping
    public ResponseEntity<?> funding(@RequestBody @Valid RequestFundingDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());

        if(dto.getOption() != null)
            fundingService.optionFunding(dto.getOption());
        if(dto.getFree() != null)
            fundingService.freeFunding(dto.getFree());

        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteFunding(@RequestBody RequestCancelFundingDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        if(dto.getFundingType().equals(FundingType.FREE))
            fundingService.cancelFreeFunding(dto);
        else if(dto.getFundingType().equals(FundingType.OPTION))
            fundingService.cancelOptionFunding(dto);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }
}
