package com.hanati.domain.index.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexPriceRequest {
    private String indexCode;  // 지수 코드 (KOSPI: U001, KOSDAQ: U201, KOSPI200: U180)
    private String indexName;  // 지수 이름
}