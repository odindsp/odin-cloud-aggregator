package com.pxene.odin.cloud.web.api;

import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.SizeVO;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhangshiyi
 */
@FeignClient(name = "ODIN-CLOUD-CHANNEL-SERVICE")
public interface ImageSizeClient {

    @GetMapping(value = "/sizes")
    String listSizes();
}
